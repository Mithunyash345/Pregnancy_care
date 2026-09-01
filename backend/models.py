import uuid
from sqlalchemy import Column, String, Integer, Boolean, Numeric, Date, Time, DateTime, Text, ForeignKey
from sqlalchemy.dialects.postgresql import UUID
from sqlalchemy.orm import relationship
from sqlalchemy.sql import func
from .database import Base

class User(Base):
    __tablename__ = "users"

    user_id = Column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    full_name = Column(String, nullable=False)
    email = Column(String, unique=True, nullable=False, index=True)
    phone = Column(String, nullable=True)
    password_hash = Column(String, nullable=False)
    role = Column(String, nullable=False)  # "mother", "companion", "doctor"
    profile_image = Column(Text, nullable=True)
    is_verified = Column(Boolean, default=False)
    created_at = Column(DateTime, default=func.now())
    updated_at = Column(DateTime, default=func.now(), onupdate=func.now())

    # Relationships
    profile = relationship("PregnancyProfile", back_populates="user", uselist=False)
    settings = relationship("UserSettings", back_populates="user", uselist=False)
    notifications = relationship("Notification", back_populates="user")
    doctor_profile = relationship("Doctor", back_populates="user", uselist=False)

class UserSettings(Base):
    __tablename__ = "user_settings"

    setting_id = Column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    user_id = Column(UUID(as_uuid=True), ForeignKey("users.user_id", ondelete="CASCADE"), nullable=False)
    language = Column(String, default="en")
    notifications_enabled = Column(Boolean, default=True)
    dark_mode = Column(Boolean, default=False)

    user = relationship("User", back_populates="settings")

class PregnancyProfile(Base):
    __tablename__ = "pregnancy_profiles"

    profile_id = Column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    user_id = Column(UUID(as_uuid=True), ForeignKey("users.user_id", ondelete="CASCADE"), nullable=False)
    due_date = Column(Date, nullable=False)
    pregnancy_week = Column(Integer, default=1)
    trimester = Column(Integer, default=1)
    blood_group = Column(String, nullable=True)
    height_cm = Column(Numeric, nullable=True)
    pre_pregnancy_weight = Column(Numeric, nullable=True)
    current_weight = Column(Numeric, nullable=True)
    medical_conditions = Column(Text, nullable=True)
    allergies = Column(Text, nullable=True)
    created_at = Column(DateTime, default=func.now())

    user = relationship("User", back_populates="profile")

class Doctor(Base):
    __tablename__ = "doctors"

    doctor_id = Column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    user_id = Column(UUID(as_uuid=True), ForeignKey("users.user_id", ondelete="CASCADE"), nullable=False)
    specialization = Column(String, nullable=True)
    hospital_name = Column(String, nullable=True)
    experience_years = Column(Integer, nullable=True)
    consultation_fee = Column(Numeric, nullable=True)
    about = Column(Text, nullable=True)
    available = Column(Boolean, default=True)

    user = relationship("User", back_populates="doctor_profile")
    appointments = relationship("Appointment", back_populates="doctor")

class Appointment(Base):
    __tablename__ = "appointments"

    appointment_id = Column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    mother_id = Column(UUID(as_uuid=True), ForeignKey("users.user_id", ondelete="CASCADE"), nullable=True)
    doctor_id = Column(UUID(as_uuid=True), ForeignKey("doctors.doctor_id", ondelete="CASCADE"), nullable=True)
    appointment_date = Column(DateTime, nullable=True)
    consultation_type = Column(String, nullable=True)  # "Routine Review", "Emergency", "Follow-up"
    status = Column(String, default="Pending")  # "Pending", "Approved", "Completed", "Cancelled"
    notes = Column(Text, nullable=True)
    created_at = Column(DateTime, default=func.now())

    doctor = relationship("Doctor", back_populates="appointments")

class MedicalReport(Base):
    __tablename__ = "medical_reports"

    report_id = Column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    mother_id = Column(UUID(as_uuid=True), ForeignKey("users.user_id", ondelete="CASCADE"), nullable=True)
    report_name = Column(String, nullable=True)
    report_type = Column(String, nullable=True)
    file_url = Column(Text, nullable=True)
    uploaded_at = Column(DateTime, default=func.now())

    ocr_results = relationship("OCRResult", back_populates="report", cascade="all, delete-orphan")

class OCRResult(Base):
    __tablename__ = "ocr_results"

    ocr_id = Column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    report_id = Column(UUID(as_uuid=True), ForeignKey("medical_reports.report_id", ondelete="CASCADE"), nullable=True)
    extracted_text = Column(Text, nullable=True)
    processed = Column(Boolean, default=False)
    created_at = Column(DateTime, default=func.now())

    report = relationship("MedicalReport", back_populates="ocr_results")

class HealthMetric(Base):
    __tablename__ = "health_metrics"

    metric_id = Column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    mother_id = Column(UUID(as_uuid=True), ForeignKey("users.user_id", ondelete="CASCADE"), nullable=True)
    weight = Column(Numeric, nullable=True)
    blood_pressure = Column(String, nullable=True)
    blood_sugar = Column(Numeric, nullable=True)
    heart_rate = Column(Integer, nullable=True)
    recorded_at = Column(DateTime, default=func.now())

class MedicationReminder(Base):
    __tablename__ = "medication_reminders"

    medication_id = Column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    mother_id = Column(UUID(as_uuid=True), ForeignKey("users.user_id", ondelete="CASCADE"), nullable=True)
    medicine_name = Column(String, nullable=True)
    dosage = Column(String, nullable=True)
    reminder_time = Column(Time, nullable=True)
    start_date = Column(Date, nullable=True)
    end_date = Column(Date, nullable=True)

class Notification(Base):
    __tablename__ = "notifications"

    notification_id = Column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    user_id = Column(UUID(as_uuid=True), ForeignKey("users.user_id", ondelete="CASCADE"), nullable=True)
    title = Column(String, nullable=True)
    message = Column(Text, nullable=True)
    notification_type = Column(String, nullable=True)  # "Alert", "Tip", "Appointment"
    is_read = Column(Boolean, default=False)
    created_at = Column(DateTime, default=func.now())

    user = relationship("User", back_populates="notifications")

class PregnancyMilestone(Base):
    __tablename__ = "pregnancy_milestones"

    milestone_id = Column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    week_number = Column(Integer, nullable=True)
    title = Column(String, nullable=True)
    description = Column(Text, nullable=True)

class SleepTracking(Base):
    __tablename__ = "sleep_tracking"

    sleep_id = Column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    mother_id = Column(UUID(as_uuid=True), ForeignKey("users.user_id", ondelete="CASCADE"), nullable=True)
    tracking_date = Column(Date, nullable=True)
    sleep_hours = Column(Numeric, nullable=True)

class StepTracking(Base):
    __tablename__ = "step_tracking"

    step_id = Column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    mother_id = Column(UUID(as_uuid=True), ForeignKey("users.user_id", ondelete="CASCADE"), nullable=True)
    tracking_date = Column(Date, nullable=True)
    steps = Column(Integer, nullable=True)

class WaterTracking(Base):
    __tablename__ = "water_tracking"

    water_id = Column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    mother_id = Column(UUID(as_uuid=True), ForeignKey("users.user_id", ondelete="CASCADE"), nullable=True)
    intake_date = Column(Date, nullable=True)
    glasses = Column(Integer, nullable=True)

class EmergencyContact(Base):
    __tablename__ = "emergency_contacts"

    contact_id = Column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    mother_id = Column(UUID(as_uuid=True), ForeignKey("users.user_id", ondelete="CASCADE"), nullable=True)
    name = Column(String, nullable=True)
    relationship = Column(String, nullable=True)
    phone = Column(String, nullable=True)
    created_at = Column(DateTime, default=func.now())

class FamilyCompanion(Base):
    __tablename__ = "family_companions"

    companion_id = Column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    mother_id = Column(UUID(as_uuid=True), ForeignKey("users.user_id", ondelete="CASCADE"), nullable=True)
    companion_user_id = Column(UUID(as_uuid=True), ForeignKey("users.user_id", ondelete="CASCADE"), nullable=True)
    relationship = Column(String, nullable=True)
    created_at = Column(DateTime, default=func.now())

class AIChatHistory(Base):
    __tablename__ = "ai_chat_history"

    chat_id = Column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    mother_id = Column(UUID(as_uuid=True), ForeignKey("users.user_id", ondelete="CASCADE"), nullable=True)
    user_message = Column(Text, nullable=True)
    ai_response = Column(Text, nullable=True)
    created_at = Column(DateTime, default=func.now())

class EmailVerification(Base):
    __tablename__ = "email_verifications"

    verification_id = Column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    user_id = Column(UUID(as_uuid=True), ForeignKey("users.user_id", ondelete="CASCADE"), nullable=False)
    email = Column(String(150), nullable=False)
    otp_hash = Column(Text, nullable=False)
    expires_at = Column(DateTime, nullable=False)
    attempts = Column(Integer, default=0)
    is_verified = Column(Boolean, default=False)
    created_at = Column(DateTime, default=func.now())

    user = relationship("User")
