from sqlalchemy.orm import Session
from passlib.context import CryptContext
import uuid
from datetime import date, time, datetime, timedelta
from typing import List, Optional
from . import models, schemas

# Password Hashing
pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")

def verify_password(plain_password, hashed_password):
    return pwd_context.verify(plain_password, hashed_password)

def get_password_hash(password):
    return pwd_context.hash(password)

# --- USER CRUD ---
def get_user(db: Session, user_id: uuid.UUID):
    return db.query(models.User).filter(models.User.user_id == user_id).first()

def get_user_by_email(db: Session, email: str):
    return db.query(models.User).filter(models.User.email == email).first()

def create_user(db: Session, user: schemas.UserCreate):
    hashed_password = get_password_hash(user.password)
    db_user = models.User(
        full_name=user.full_name,
        email=user.email,
        phone=user.phone,
        password_hash=hashed_password,
        role=user.role,
        profile_image=user.profile_image,
        is_verified=False
    )
    db.add(db_user)
    db.commit()
    db.refresh(db_user)
    
    # Initialize settings automatically
    db_settings = models.UserSettings(user_id=db_user.user_id)
    db.add(db_settings)
    db.commit()
    
    return db_user

def create_email_verification(db: Session, user_id: uuid.UUID, email: str, otp_code: str, expires_in_minutes: int = 10):
    otp_hash = get_password_hash(otp_code)
    expires_at = datetime.utcnow() + timedelta(minutes=expires_in_minutes)
    
    db_verification = models.EmailVerification(
        user_id=user_id,
        email=email.strip().lower(),
        otp_hash=otp_hash,
        expires_at=expires_at,
        attempts=0,
        is_verified=False
    )
    db.add(db_verification)
    db.commit()
    db.refresh(db_verification)
    return db_verification

def get_active_verification(db: Session, email: str):
    return db.query(models.EmailVerification)\
        .filter(models.EmailVerification.email == email.strip().lower())\
        .order_by(models.EmailVerification.created_at.desc())\
        .first()

# --- SETTINGS ---
def get_user_settings(db: Session, user_id: uuid.UUID):
    return db.query(models.UserSettings).filter(models.UserSettings.user_id == user_id).first()

def update_user_settings(db: Session, user_id: uuid.UUID, settings_update: schemas.UserSettingsBase):
    db_settings = get_user_settings(db, user_id)
    if not db_settings:
        db_settings = models.UserSettings(user_id=user_id)
        db.add(db_settings)
    
    db_settings.language = settings_update.language
    db_settings.notifications_enabled = settings_update.notifications_enabled
    db_settings.dark_mode = settings_update.dark_mode
    db.commit()
    db.refresh(db_settings)
    return db_settings

# --- PREGNANCY PROFILE ---
def get_pregnancy_profile(db: Session, user_id: uuid.UUID):
    return db.query(models.PregnancyProfile).filter(models.PregnancyProfile.user_id == user_id).first()

def create_pregnancy_profile(db: Session, user_id: uuid.UUID, profile: schemas.PregnancyProfileCreate):
    db_profile = models.PregnancyProfile(
        user_id=user_id,
        due_date=profile.due_date,
        pregnancy_week=profile.pregnancy_week,
        trimester=profile.trimester,
        blood_group=profile.blood_group,
        height_cm=profile.height_cm,
        pre_pregnancy_weight=profile.pre_pregnancy_weight,
        current_weight=profile.current_weight,
        medical_conditions=profile.medical_conditions,
        allergies=profile.allergies
    )
    db.add(db_profile)
    db.commit()
    db.refresh(db_profile)
    return db_profile

def update_pregnancy_profile(db: Session, user_id: uuid.UUID, profile_update: schemas.PregnancyProfileCreate):
    db_profile = get_pregnancy_profile(db, user_id)
    if not db_profile:
        return create_pregnancy_profile(db, user_id, profile_update)
        
    db_profile.due_date = profile_update.due_date
    db_profile.pregnancy_week = profile_update.pregnancy_week
    db_profile.trimester = profile_update.trimester
    db_profile.blood_group = profile_update.blood_group
    db_profile.height_cm = profile_update.height_cm
    db_profile.pre_pregnancy_weight = profile_update.pre_pregnancy_weight
    db_profile.current_weight = profile_update.current_weight
    db_profile.medical_conditions = profile_update.medical_conditions
    db_profile.allergies = profile_update.allergies
    db.commit()
    db.refresh(db_profile)
    return db_profile

# --- DOCTOR CRUD ---
def get_doctors(db: Session):
    doctors = db.query(models.Doctor).all()
    # Add full_name from related User
    for doctor in doctors:
        user = db.query(models.User).filter(models.User.user_id == doctor.user_id).first()
        doctor.full_name = user.full_name if user else "Dr. Unknown"
    return doctors

def get_doctor_by_user_id(db: Session, user_id: uuid.UUID):
    return db.query(models.Doctor).filter(models.Doctor.user_id == user_id).first()

def create_doctor_profile(db: Session, user_id: uuid.UUID, doctor_profile: schemas.DoctorBase):
    db_doctor = models.Doctor(
        user_id=user_id,
        specialization=doctor_profile.specialization,
        hospital_name=doctor_profile.hospital_name,
        experience_years=doctor_profile.experience_years,
        consultation_fee=doctor_profile.consultation_fee,
        about=doctor_profile.about,
        available=doctor_profile.available
    )
    db.add(db_doctor)
    db.commit()
    db.refresh(db_doctor)
    return db_doctor

# --- APPOINTMENTS ---
def get_appointments_for_mother(db: Session, mother_id: uuid.UUID):
    appointments = db.query(models.Appointment).filter(models.Appointment.mother_id == mother_id).all()
    for appt in appointments:
        doc = db.query(models.Doctor).filter(models.Doctor.doctor_id == appt.doctor_id).first()
        if doc:
            doc_user = db.query(models.User).filter(models.User.user_id == doc.user_id).first()
            appt.doctor_name = doc_user.full_name if doc_user else "Dr. Sarah Jenkins"
        else:
            appt.doctor_name = "Dr. Sarah Jenkins"
    return appointments

def get_appointments_for_doctor(db: Session, doctor_id: uuid.UUID):
    appointments = db.query(models.Appointment).filter(models.Appointment.doctor_id == doctor_id).all()
    for appt in appointments:
        patient = db.query(models.User).filter(models.User.user_id == appt.mother_id).first()
        appt.patient_name = patient.full_name if patient else "Unknown Patient"
    return appointments

def create_appointment(db: Session, mother_id: uuid.UUID, appt: schemas.AppointmentCreate):
    db_appt = models.Appointment(
        mother_id=mother_id,
        doctor_id=appt.doctor_id,
        appointment_date=appt.appointment_date,
        consultation_type=appt.consultation_type,
        status="Pending",
        notes=appt.notes
    )
    db.add(db_appt)
    db.commit()
    db.refresh(db_appt)
    return db_appt

def update_appointment_status(db: Session, appointment_id: uuid.UUID, status: str):
    db_appt = db.query(models.Appointment).filter(models.Appointment.appointment_id == appointment_id).first()
    if db_appt:
        db_appt.status = status
        db.commit()
        db.refresh(db_appt)
    return db_appt

# --- WELLNESS TRACKING ---
def get_water_logs(db: Session, mother_id: uuid.UUID, tracking_date: date):
    return db.query(models.WaterTracking).filter(
        models.WaterTracking.mother_id == mother_id,
        models.WaterTracking.intake_date == tracking_date
    ).first()

def log_water(db: Session, mother_id: uuid.UUID, intake_date: date, glasses: int):
    log = get_water_logs(db, mother_id, intake_date)
    if log:
        log.glasses = glasses
    else:
        log = models.WaterTracking(mother_id=mother_id, intake_date=intake_date, glasses=glasses)
        db.add(log)
    db.commit()
    db.refresh(log)
    return log

def get_step_logs(db: Session, mother_id: uuid.UUID, tracking_date: date):
    return db.query(models.StepTracking).filter(
        models.StepTracking.mother_id == mother_id,
        models.StepTracking.tracking_date == tracking_date
    ).first()

def log_steps(db: Session, mother_id: uuid.UUID, tracking_date: date, steps: int):
    log = get_step_logs(db, mother_id, tracking_date)
    if log:
        log.steps = steps
    else:
        log = models.StepTracking(mother_id=mother_id, tracking_date=tracking_date, steps=steps)
        db.add(log)
    db.commit()
    db.refresh(log)
    return log

def get_sleep_logs(db: Session, mother_id: uuid.UUID, tracking_date: date):
    return db.query(models.SleepTracking).filter(
        models.SleepTracking.mother_id == mother_id,
        models.SleepTracking.tracking_date == tracking_date
    ).first()

def log_sleep(db: Session, mother_id: uuid.UUID, tracking_date: date, sleep_hours: float):
    log = get_sleep_logs(db, mother_id, tracking_date)
    if log:
        log.sleep_hours = sleep_hours
    else:
        log = models.SleepTracking(mother_id=mother_id, tracking_date=tracking_date, sleep_hours=sleep_hours)
        db.add(log)
    db.commit()
    db.refresh(log)
    return log

# --- HEALTH METRICS ---
def get_health_metrics(db: Session, mother_id: uuid.UUID):
    return db.query(models.HealthMetric).filter(models.HealthMetric.mother_id == mother_id).order_by(models.HealthMetric.recorded_at.desc()).all()

def create_health_metric(db: Session, mother_id: uuid.UUID, metric: schemas.HealthMetricCreate):
    db_metric = models.HealthMetric(
        mother_id=mother_id,
        weight=metric.weight,
        blood_pressure=metric.blood_pressure,
        blood_sugar=metric.blood_sugar,
        heart_rate=metric.heart_rate
    )
    db.add(db_metric)
    db.commit()
    db.refresh(db_metric)
    return db_metric

# --- MEDICATION REMINDERS ---
def get_medications(db: Session, mother_id: uuid.UUID):
    return db.query(models.MedicationReminder).filter(models.MedicationReminder.mother_id == mother_id).all()

def create_medication(db: Session, mother_id: uuid.UUID, med: schemas.MedicationReminderCreate):
    db_med = models.MedicationReminder(
        mother_id=mother_id,
        medicine_name=med.medicine_name,
        dosage=med.dosage,
        reminder_time=med.reminder_time,
        start_date=med.start_date,
        end_date=med.end_date
    )
    db.add(db_med)
    db.commit()
    db.refresh(db_med)
    return db_med

def delete_medication(db: Session, medication_id: uuid.UUID):
    db_med = db.query(models.MedicationReminder).filter(models.MedicationReminder.medication_id == medication_id).first()
    if db_med:
        db.delete(db_med)
        db.commit()
        return True
    return False

# --- NOTIFICATIONS ---
def get_notifications(db: Session, user_id: uuid.UUID):
    return db.query(models.Notification).filter(models.Notification.user_id == user_id).order_by(models.Notification.created_at.desc()).all()

def create_notification(db: Session, user_id: uuid.UUID, title: str, message: str, notification_type: str):
    db_notif = models.Notification(
        user_id=user_id,
        title=title,
        message=message,
        notification_type=notification_type,
        is_read=False
    )
    db.add(db_notif)
    db.commit()
    db.refresh(db_notif)
    return db_notif

def mark_notification_read(db: Session, notification_id: uuid.UUID):
    db_notif = db.query(models.Notification).filter(models.Notification.notification_id == notification_id).first()
    if db_notif:
        db_notif.is_read = True
        db.commit()
        db.refresh(db_notif)
    return db_notif

# --- EMERGENCY CONTACTS ---
def get_emergency_contacts(db: Session, mother_id: uuid.UUID):
    return db.query(models.EmergencyContact).filter(models.EmergencyContact.mother_id == mother_id).all()

def create_emergency_contact(db: Session, mother_id: uuid.UUID, contact: schemas.EmergencyContactCreate):
    db_contact = models.EmergencyContact(
        mother_id=mother_id,
        name=contact.name,
        relationship=contact.relationship,
        phone=contact.phone
    )
    db.add(db_contact)
    db.commit()
    db.refresh(db_contact)
    return db_contact

def delete_emergency_contact(db: Session, contact_id: uuid.UUID):
    db_contact = db.query(models.EmergencyContact).filter(models.EmergencyContact.contact_id == contact_id).first()
    if db_contact:
        db.delete(db_contact)
        db.commit()
        return True
    return False

# --- FAMILY COMPANIONS ---
def get_linked_companions(db: Session, mother_id: uuid.UUID):
    companions = db.query(models.FamilyCompanion).filter(models.FamilyCompanion.mother_id == mother_id).all()
    for companion in companions:
        c_user = db.query(models.User).filter(models.User.user_id == companion.companion_user_id).first()
        companion.companion_name = c_user.full_name if c_user else "Companion"
    return companions

def link_companion(db: Session, mother_id: uuid.UUID, companion_user_id: uuid.UUID, relationship: str):
    db_link = models.FamilyCompanion(
        mother_id=mother_id,
        companion_user_id=companion_user_id,
        relationship=relationship
    )
    db.add(db_link)
    db.commit()
    db.refresh(db_link)
    return db_link

# --- AI CHAT HISTORY ---
def get_chat_history(db: Session, mother_id: uuid.UUID):
    return db.query(models.AIChatHistory).filter(models.AIChatHistory.mother_id == mother_id).order_by(models.AIChatHistory.created_at.asc()).all()

def log_chat_message(db: Session, mother_id: uuid.UUID, user_message: str, ai_response: str):
    db_chat = models.AIChatHistory(
        mother_id=mother_id,
        user_message=user_message,
        ai_response=ai_response
    )
    db.add(db_chat)
    db.commit()
    db.refresh(db_chat)
    return db_chat

# --- REPORTS & OCR ---
def get_reports(db: Session, mother_id: uuid.UUID):
    return db.query(models.MedicalReport).filter(models.MedicalReport.mother_id == mother_id).order_by(models.MedicalReport.uploaded_at.desc()).all()

def create_report(db: Session, mother_id: uuid.UUID, report_name: str, report_type: str, file_url: Optional[str] = None):
    db_report = models.MedicalReport(
        mother_id=mother_id,
        report_name=report_name,
        report_type=report_type,
        file_url=file_url
    )
    db.add(db_report)
    db.commit()
    db.refresh(db_report)
    return db_report

def create_ocr_result(db: Session, report_id: uuid.UUID, extracted_text: str, processed: bool = False):
    db_ocr = models.OCRResult(
        report_id=report_id,
        extracted_text=extracted_text,
        processed=processed
    )
    db.add(db_ocr)
    db.commit()
    db.refresh(db_ocr)
    return db_ocr
