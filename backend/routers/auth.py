import hashlib
import random
import smtplib
from datetime import datetime, timedelta

from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText

from fastapi import APIRouter, Depends, HTTPException, status
from fastapi.security import OAuth2PasswordBearer, OAuth2PasswordRequestForm
from jose import JWTError, jwt
from sqlalchemy.orm import Session

from .. import crud, schemas, models
from ..config import settings
from ..database import get_db


router = APIRouter(
    prefix="/auth",
    tags=["Authentication"]
)


oauth2_scheme = OAuth2PasswordBearer(
    tokenUrl="auth/login-form"
)


# ============================================================
# OTP CONFIGURATION
# ============================================================

OTP_EXPIRY_MINUTES = 10
MAX_OTP_ATTEMPTS = 5


# ============================================================
# OTP HELPERS
# ============================================================

def generate_otp() -> str:
    """Generate a secure 6-digit OTP."""
    return f"{random.SystemRandom().randint(100000, 999999)}"


def hash_otp(otp: str) -> str:
    """Hash OTP before storing it in the database."""
    return hashlib.sha256(otp.encode("utf-8")).hexdigest()


# ============================================================
# SEND EMAIL
# ============================================================

def send_otp_email(to_email: str, otp_code: str) -> bool:
    """Send OTP to user's email using SMTP."""

    if not settings.SMTP_USER or not settings.SMTP_PASSWORD:
        print(
            f"\n[SMTP ERROR] SMTP credentials are not configured."
            f"\nOTP was NOT sent to {to_email}\n"
        )
        return False

    try:
        msg = MIMEMultipart("alternative")

        msg["From"] = settings.SMTP_USER
        msg["To"] = to_email
        msg["Subject"] = "Bloom Pregnancy Care - Email Verification OTP"

        body = f"""
        <html>
        <body style="font-family: Arial, sans-serif;
                     padding: 20px;
                     color: #333;
                     background-color: #fafafa;">

            <div style="max-width: 600px;
                        margin: auto;
                        background: white;
                        padding: 30px;
                        border-radius: 12px;
                        border: 1px solid #eee;">

                <h2 style="color: #ff8a80;
                           text-align: center;">
                    Welcome to Bloom! 🌸
                </h2>

                <p>Hello,</p>

                <p>
                    Thank you for registering with
                    Bloom Pregnancy Care.
                </p>

                <p>
                    Your email verification OTP is:
                </p>

                <div style="text-align: center;
                            margin: 30px 0;">

                    <div style="font-size: 32px;
                                font-weight: bold;
                                letter-spacing: 6px;
                                padding: 15px 30px;
                                background-color: #fff5f5;
                                border-radius: 8px;
                                display: inline-block;
                                color: #d32f2f;
                                border: 1px dashed #ff8a80;">

                        {otp_code}

                    </div>
                </div>

                <p>
                    This OTP is valid for
                    <strong>10 minutes</strong>.
                </p>

                <p style="font-size: 13px; color: #666;">
                    If you did not request this verification,
                    please ignore this email.
                </p>

                <hr>

                <p style="font-size: 12px;
                          color: #999;
                          text-align: center;">

                    Bloom Pregnancy Care Team © 2026

                </p>

            </div>

        </body>
        </html>
        """

        msg.attach(MIMEText(body, "html"))

        server = smtplib.SMTP(
            settings.SMTP_SERVER,
            settings.SMTP_PORT
        )

        server.starttls()

        server.login(
            settings.SMTP_USER,
            settings.SMTP_PASSWORD
        )

        server.sendmail(
            settings.SMTP_USER,
            to_email,
            msg.as_string()
        )

        server.quit()

        return True

    except Exception as e:

        print(f"[SMTP ERROR] {e}")

        return False


# ============================================================
# CREATE OTP RECORD
# ============================================================

def create_email_verification(
    db: Session,
    user: models.User
):
    """Create OTP record and send OTP email."""

    otp = generate_otp()

    otp_hash = hash_otp(otp)

    expires_at = datetime.utcnow() + timedelta(
        minutes=OTP_EXPIRY_MINUTES
    )

    # Invalidate previous OTPs for this user
    previous_records = (
        db.query(models.EmailVerification)
        .filter(
            models.EmailVerification.user_id == user.user_id,
            models.EmailVerification.is_verified == False
        )
        .all()
    )

    for record in previous_records:
        record.is_verified = True

    verification = models.EmailVerification(
        user_id=user.user_id,
        email=user.email,
        otp_hash=otp_hash,
        expires_at=expires_at,
        attempts=0,
        is_verified=False
    )

    db.add(verification)
    db.commit()
    db.refresh(verification)

    # Send email
    email_sent = send_otp_email(
        user.email,
        otp
    )

    if not email_sent:
        # Remove verification record if email couldn't be sent
        db.delete(verification)
        db.commit()

        raise HTTPException(
            status_code=500,
            detail="Unable to send verification email"
        )

    return verification


# ============================================================
# JWT
# ============================================================

def create_access_token(
    data: dict,
    expires_delta: timedelta = None
):

    to_encode = data.copy()

    if expires_delta:

        expire = datetime.utcnow() + expires_delta

    else:

        expire = datetime.utcnow() + timedelta(
            minutes=settings.ACCESS_TOKEN_EXPIRE_MINUTES
        )

    to_encode.update({
        "exp": expire
    })

    return jwt.encode(
        to_encode,
        settings.SECRET_KEY,
        algorithm=settings.ALGORITHM
    )


# ============================================================
# CURRENT USER
# ============================================================

def get_current_user(
    token: str = Depends(oauth2_scheme),
    db: Session = Depends(get_db)
):

    credentials_exception = HTTPException(
        status_code=status.HTTP_401_UNAUTHORIZED,
        detail="Could not validate credentials",
        headers={
            "WWW-Authenticate": "Bearer"
        }
    )

    try:

        payload = jwt.decode(
            token,
            settings.SECRET_KEY,
            algorithms=[settings.ALGORITHM]
        )

        email = payload.get("sub")

        if email is None:
            raise credentials_exception

        token_data = schemas.TokenData(
            email=email
        )

    except JWTError:

        raise credentials_exception

    user = crud.get_user_by_email(
        db,
        email=token_data.email
    )

    if user is None:
        raise credentials_exception

    return user


# ============================================================
# REGISTER
# ============================================================

@router.post(
    "/register",
    response_model=schemas.UserResponse
)
def register(
    user: schemas.UserCreate,
    db: Session = Depends(get_db)
):

    email_lower = user.email.strip().lower()

    # Check existing user
    db_user = crud.get_user_by_email(
        db,
        email=email_lower
    )

    if db_user:

        raise HTTPException(
            status_code=400,
            detail="Email already registered"
        )

    # Create user
    created_user = crud.create_user(
        db=db,
        user=user
    )

    # Make sure email is initially unverified
    created_user.is_verified = False

    db.commit()
    db.refresh(created_user)

    # Create OTP + send email
    create_email_verification(
        db,
        created_user
    )

    return created_user


# ============================================================
# VERIFY OTP
# ============================================================

@router.post("/verify-otp")
def verify_otp(
    email: str,
    otp: str,
    db: Session = Depends(get_db)
):

    email_lower = email.strip().lower()

    user = crud.get_user_by_email(
        db,
        email=email_lower
    )

    if not user:

        raise HTTPException(
            status_code=404,
            detail="User not found"
        )

    if user.is_verified:

        return {
            "status": "success",
            "message": "Email is already verified"
        }

    verification = (
        db.query(models.EmailVerification)
        .filter(
            models.EmailVerification.user_id == user.user_id,
            models.EmailVerification.is_verified == False
        )
        .order_by(
            models.EmailVerification.created_at.desc()
        )
        .first()
    )

    if not verification:

        raise HTTPException(
            status_code=400,
            detail="No active OTP found. Please request a new OTP."
        )

    # Check attempt limit
    if verification.attempts >= MAX_OTP_ATTEMPTS:

        raise HTTPException(
            status_code=400,
            detail="Maximum OTP attempts exceeded. Please request a new OTP."
        )

    # Check expiration
    if datetime.utcnow() > verification.expires_at:

        raise HTTPException(
            status_code=400,
            detail="OTP has expired. Please request a new OTP."
        )

    # Increment attempts
    verification.attempts += 1

    # Compare hash
    if hash_otp(otp) != verification.otp_hash:

        db.commit()

        raise HTTPException(
            status_code=400,
            detail="Invalid OTP"
        )

    # Successful verification
    verification.is_verified = True

    user.is_verified = True

    db.commit()

    return {
        "status": "success",
        "message": "Email verified successfully"
    }


# ============================================================
# RESEND OTP
# ============================================================

@router.post("/resend-otp")
def resend_otp(
    email: str,
    db: Session = Depends(get_db)
):

    email_lower = email.strip().lower()

    user = crud.get_user_by_email(
        db,
        email=email_lower
    )

    if not user:

        raise HTTPException(
            status_code=404,
            detail="User not found"
        )

    if user.is_verified:

        raise HTTPException(
            status_code=400,
            detail="Email is already verified"
        )

    # Create new OTP
    create_email_verification(
        db,
        user
    )

    return {
        "status": "success",
        "message": "A new OTP has been sent to your email"
    }


# ============================================================
# LOGIN
# ============================================================

@router.post(
    "/login",
    response_model=schemas.Token
)
def login(
    login_data: schemas.UserLogin,
    db: Session = Depends(get_db)
):

    email_lower = login_data.email.strip().lower()

    user = crud.get_user_by_email(
        db,
        email=email_lower
    )

    if not user:

        raise HTTPException(
            status_code=401,
            detail="Incorrect email or password"
        )

    if not crud.verify_password(
        login_data.password,
        user.password_hash
    ):

        raise HTTPException(
            status_code=401,
            detail="Incorrect email or password"
        )

    # Don't allow login before email verification
    if not user.is_verified:

        raise HTTPException(
            status_code=403,
            detail="Please verify your email before logging in"
        )

    access_token = create_access_token(
        data={
            "sub": user.email,
            "role": user.role
        }
    )

    return {
        "access_token": access_token,
        "token_type": "bearer",
        "role": user.role,
        "user_id": user.user_id,
        "full_name": user.full_name
    }


# ============================================================
# LOGIN FORM - SWAGGER / OAUTH
# ============================================================

@router.post(
    "/login-form",
    response_model=schemas.Token
)
def login_form(
    form_data: OAuth2PasswordRequestForm = Depends(),
    db: Session = Depends(get_db)
):

    email_lower = form_data.username.strip().lower()

    user = crud.get_user_by_email(
        db,
        email=email_lower
    )

    if not user:

        raise HTTPException(
            status_code=401,
            detail="Incorrect email or password"
        )

    if not crud.verify_password(
        form_data.password,
        user.password_hash
    ):

        raise HTTPException(
            status_code=401,
            detail="Incorrect email or password"
        )

    if not user.is_verified:

        raise HTTPException(
            status_code=403,
            detail="Please verify your email before logging in"
        )

    access_token = create_access_token(
        data={
            "sub": user.email,
            "role": user.role
        }
    )

    return {
        "access_token": access_token,
        "token_type": "bearer",
        "role": user.role,
        "user_id": user.user_id,
        "full_name": user.full_name
    }


# ============================================================
# PASSWORD RESET OTP
# ============================================================

@router.post("/reset-password")
def reset_password(
    email: str,
    db: Session = Depends(get_db)
):

    email_lower = email.strip().lower()

    user = crud.get_user_by_email(
        db,
        email=email_lower
    )

    if not user:

        raise HTTPException(
            status_code=404,
            detail="User not found"
        )

    create_email_verification(
        db,
        user
    )

    return {
        "status": "success",
        "message": "Password reset OTP sent to your email"
    }