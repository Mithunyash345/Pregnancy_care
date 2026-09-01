import random
import smtplib
from datetime import datetime, timedelta
from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText
from fastapi import APIRouter, Depends, HTTPException, status
from fastapi.security import OAuth2PasswordBearer, OAuth2PasswordRequestForm
from jose import JWTError, jwt
from sqlalchemy import text
from sqlalchemy.orm import Session
from uuid import UUID

from .. import crud, schemas, models
from ..config import settings
from ..database import get_db

router = APIRouter(
    prefix="/auth",
    tags=["Authentication"]
)

# Global in-memory OTP repository
otp_store = {}

oauth2_scheme = OAuth2PasswordBearer(tokenUrl="auth/login-form")

def send_otp_email(to_email: str, otp_code: str):
    """Sends a real email to the user with the generated OTP code."""
    # Check if credentials are configured
    if not settings.SMTP_USER or not settings.SMTP_PASSWORD:
        print(f"\n[SMTP SIMULATION] Credentials not configured. Simulated code for {to_email}: {otp_code}\n")
        return False
        
    try:
        msg = MIMEMultipart()
        msg['From'] = settings.SMTP_USER
        msg['To'] = to_email
        msg['Subject'] = "Bloom Pregnancy Care - Your Verification OTP Code"
        
        body = f"""
        <html>
        <body style="font-family: Arial, sans-serif; padding: 20px; color: #333; background-color: #fafafa;">
            <div style="max-width: 600px; margin: 0 auto; background: white; padding: 30px; border-radius: 12px; border: 1px solid #eee;">
                <h2 style="color: #ff8a80; text-align: center; margin-bottom: 20px;">Welcome to Bloom!</h2>
                <p>Hello,</p>
                <p>Thank you for registering with Bloom Pregnancy Care. Please use the following 6-digit One-Time Password (OTP) to verify your account and access your dashboard:</p>
                <div style="text-align: center; margin: 30px 0;">
                    <div style="font-size: 32px; font-weight: bold; letter-spacing: 6px; padding: 15px 30px; background-color: #fff5f5; border-radius: 8px; display: inline-block; color: #d32f2f; border: 1px dashed #ff8a80;">
                        {otp_code}
                    </div>
                </div>
                <p style="font-size: 13px; color: #666;">This verification code is valid for 10 minutes. If you did not make this request, you can safely ignore this email.</p>
                <hr style="border: none; border-top: 1px solid #eee; margin: 30px 0 20px 0;">
                <p style="font-size: 12px; color: #999; text-align: center;">Bloom Pregnancy Care Team &copy; 2026</p>
            </div>
        </body>
        </html>
        """
        msg.attach(MIMEText(body, 'html'))
        
        server = smtplib.SMTP(settings.SMTP_SERVER, settings.SMTP_PORT)
        server.starttls()
        server.login(settings.SMTP_USER, settings.SMTP_PASSWORD)
        server.sendmail(settings.SMTP_USER, to_email, msg.as_string())
        server.quit()
        return True
    except Exception as e:
        print(f"\n[SMTP ERROR] Failed to send email to {to_email}: {e}\n")
        return False

def create_access_token(data: dict, expires_delta: timedelta = None):
    to_encode = data.copy()
    if expires_delta:
        expire = datetime.utcnow() + expires_delta
    else:
        expire = datetime.utcnow() + timedelta(minutes=settings.ACCESS_TOKEN_EXPIRE_MINUTES)
    to_encode.update({"exp": expire})
    encoded_jwt = jwt.encode(to_encode, settings.SECRET_KEY, algorithm=settings.ALGORITHM)
    return encoded_jwt

def get_current_user(token: str = Depends(oauth2_scheme), db: Session = Depends(get_db)):
    credentials_exception = HTTPException(
        status_code=status.HTTP_401_UNAUTHORIZED,
        detail="Could not validate credentials",
        headers={"WWW-Authenticate": "Bearer"},
    )
    try:
        payload = jwt.decode(token, settings.SECRET_KEY, algorithms=[settings.ALGORITHM])
        email: str = payload.get("sub")
        if email is None:
            raise credentials_exception
        token_data = schemas.TokenData(email=email)
    except JWTError:
        raise credentials_exception
        
    user = crud.get_user_by_email(db, email=token_data.email)
    if user is None:
        raise credentials_exception
    return user

@router.post("/register", response_model=schemas.UserResponse)
def register(user: schemas.UserCreate, db: Session = Depends(get_db)):
    email_lower = user.email.strip().lower()
    db_user = crud.get_user_by_email(db, email=email_lower)
    if db_user:
        raise HTTPException(status_code=400, detail="Email already registered")
        
    created_user = crud.create_user(db=db, user=user)
    
    # Generate and store a real 6-digit OTP code
    otp_code = f"{random.randint(100000, 999999)}"
    otp_store[email_lower] = otp_code
    
    # Dispatch verification email
    send_otp_email(user.email, otp_code)
    
    return created_user

@router.post("/login", response_model=schemas.Token)
def login(login_data: schemas.UserLogin, db: Session = Depends(get_db)):
    email_lower = login_data.email.strip().lower()
    user = crud.get_user_by_email(db, email=email_lower)
    if not user or not crud.verify_password(login_data.password, user.password_hash):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Incorrect email or password",
            headers={"WWW-Authenticate": "Bearer"},
        )
    
    # Generate and dispatch new verification OTP on login
    otp_code = f"{random.randint(100000, 999999)}"
    otp_store[email_lower] = otp_code
    send_otp_email(user.email, otp_code)

    access_token = create_access_token(
        data={"sub": user.email, "role": user.role}
    )
    return {
        "access_token": access_token,
        "token_type": "bearer",
        "role": user.role,
        "user_id": user.user_id,
        "full_name": user.full_name
    }

# Standard OAuth2 form-compatible login (used by Swagger UI Docs)
@router.post("/login-form", response_model=schemas.Token)
def login_form(form_data: OAuth2PasswordRequestForm = Depends(), db: Session = Depends(get_db)):
    email_lower = form_data.username.strip().lower()
    user = crud.get_user_by_email(db, email=email_lower)
    if not user or not crud.verify_password(form_data.password, user.password_hash):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Incorrect email or password",
            headers={"WWW-Authenticate": "Bearer"},
        )
    
    # Generate and dispatch verification OTP
    otp_code = f"{random.randint(100000, 999999)}"
    otp_store[email_lower] = otp_code
    send_otp_email(user.email, otp_code)

    access_token = create_access_token(
        data={"sub": user.email, "role": user.role}
    )
    return {
        "access_token": access_token,
        "token_type": "bearer",
        "role": user.role,
        "user_id": user.user_id,
        "full_name": user.full_name
    }

@router.post("/verify-otp")
def verify_otp(email: str, otp: str, db: Session = Depends(get_db)):
    email_lower = email.strip().lower()
    saved_otp = otp_store.get(email_lower)
    
    # Fallback to bypass development backdoor code "123456" if SMTP credentials are blank
    is_smtp_unconfigured = not settings.SMTP_USER or not settings.SMTP_PASSWORD
    
    if (saved_otp and otp == saved_otp) or (is_smtp_unconfigured and otp == "123456"):
        # Confirm user is verified in database
        db_user = crud.get_user_by_email(db, email=email_lower)
        if db_user:
            db_user.is_verified = True
            db.commit()
            
        # Remove used OTP code
        if email_lower in otp_store:
            del otp_store[email_lower]
            
        return {"status": "success", "message": "OTP verified successfully"}
        
    raise HTTPException(status_code=400, detail="Invalid or expired OTP code")

@router.post("/reset-password")
def reset_password(email: str):
    email_lower = email.strip().lower()
    otp_code = f"{random.randint(100000, 999999)}"
    otp_store[email_lower] = otp_code
    send_otp_email(email, otp_code)
    return {"status": "success", "message": f"Password reset OTP sent to {email}"}

@router.post("/reset-db")
def reset_database_data(db: Session = Depends(get_db)):
    """Truncates all tables for a fresh start with new registration testing."""
    try:
        db.execute(
            text(
                "TRUNCATE TABLE "
                "users, user_settings, pregnancy_profiles, doctors, appointments, "
                "medical_reports, ocr_results, health_metrics, medication_reminders, "
                "notifications, pregnancy_milestones, sleep_tracking, step_tracking, "
                "water_tracking, emergency_contacts, family_companions, ai_chat_history "
                "CASCADE;"
            )
        )
        db.commit()
        # Reset local cache
        otp_store.clear()
        return {"status": "success", "message": "All database rows truncated successfully. Fresh start ready!"}
    except Exception as e:
        db.rollback()
        raise HTTPException(status_code=500, detail=f"Database reset failed: {str(e)}")
