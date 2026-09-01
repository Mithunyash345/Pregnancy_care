from pydantic import BaseModel, EmailStr
from typing import Optional, List
from uuid import UUID
from datetime import date, time, datetime
from decimal import Decimal

# --- AUTH & USER ---
class UserBase(BaseModel):
    full_name: str
    email: EmailStr
    phone: Optional[str] = None
    role: str  # "mother", "companion", "doctor"
    profile_image: Optional[str] = None

class UserCreate(UserBase):
    password: str

class UserLogin(BaseModel):
    email: EmailStr
    password: str

class UserResponse(UserBase):
    user_id: UUID
    is_verified: bool
    created_at: datetime
    updated_at: datetime

    class Config:
        from_attributes = True

class Token(BaseModel):
    access_token: str
    token_type: str
    role: str
    user_id: UUID
    full_name: str

class TokenData(BaseModel):
    email: Optional[str] = None
    role: Optional[str] = None

# --- SETTINGS ---
class UserSettingsBase(BaseModel):
    language: str = "en"
    notifications_enabled: bool = True
    dark_mode: bool = False

class UserSettingsResponse(UserSettingsBase):
    setting_id: UUID
    user_id: UUID

    class Config:
        from_attributes = True

# --- PREGNANCY PROFILE ---
class PregnancyProfileBase(BaseModel):
    due_date: date
    pregnancy_week: int = 1
    trimester: int = 1
    blood_group: Optional[str] = None
    height_cm: Optional[Decimal] = None
    pre_pregnancy_weight: Optional[Decimal] = None
    current_weight: Optional[Decimal] = None
    medical_conditions: Optional[str] = None
    allergies: Optional[str] = None

class PregnancyProfileCreate(PregnancyProfileBase):
    pass

class PregnancyProfileResponse(PregnancyProfileBase):
    profile_id: UUID
    user_id: UUID
    created_at: datetime

    class Config:
        from_attributes = True

# --- DOCTORS ---
class DoctorBase(BaseModel):
    specialization: Optional[str] = None
    hospital_name: Optional[str] = None
    experience_years: Optional[int] = None
    consultation_fee: Optional[Decimal] = None
    about: Optional[str] = None
    available: bool = True

class DoctorCreate(DoctorBase):
    user_id: UUID

class DoctorResponse(DoctorBase):
    doctor_id: UUID
    user_id: UUID
    full_name: Optional[str] = None  # Helper to return doctor user's full name

    class Config:
        from_attributes = True

# --- APPOINTMENTS ---
class AppointmentBase(BaseModel):
    doctor_id: UUID
    appointment_date: datetime
    consultation_type: Optional[str] = "Routine Review"  # "Routine Review", "Emergency", "Follow-up"
    notes: Optional[str] = None

class AppointmentCreate(AppointmentBase):
    pass

class AppointmentUpdateStatus(BaseModel):
    status: str  # "Pending", "Approved", "Completed", "Cancelled"

class AppointmentResponse(BaseModel):
    appointment_id: UUID
    mother_id: UUID
    doctor_id: UUID
    doctor_name: Optional[str] = None
    patient_name: Optional[str] = None
    appointment_date: datetime
    consultation_type: Optional[str]
    status: str
    notes: Optional[str]
    created_at: datetime

    class Config:
        from_attributes = True

# --- HEALTH METRICS ---
class HealthMetricBase(BaseModel):
    weight: Optional[Decimal] = None
    blood_pressure: Optional[str] = None
    blood_sugar: Optional[Decimal] = None
    heart_rate: Optional[int] = None

class HealthMetricCreate(HealthMetricBase):
    pass

class HealthMetricResponse(HealthMetricBase):
    metric_id: UUID
    mother_id: UUID
    recorded_at: datetime

    class Config:
        from_attributes = True

# --- WATER, STEPS, SLEEP TRACKING ---
class WaterTrackingBase(BaseModel):
    intake_date: date
    glasses: int

class WaterTrackingResponse(WaterTrackingBase):
    water_id: UUID
    mother_id: UUID

    class Config:
        from_attributes = True

class StepTrackingBase(BaseModel):
    tracking_date: date
    steps: int

class StepTrackingResponse(StepTrackingBase):
    step_id: UUID
    mother_id: UUID

    class Config:
        from_attributes = True

class SleepTrackingBase(BaseModel):
    tracking_date: date
    sleep_hours: Decimal

class SleepTrackingResponse(SleepTrackingBase):
    sleep_id: UUID
    mother_id: UUID

    class Config:
        from_attributes = True

# --- MEDICATION REMINDERS ---
class MedicationReminderBase(BaseModel):
    medicine_name: str
    dosage: str
    reminder_time: time
    start_date: date
    end_date: date

class MedicationReminderCreate(MedicationReminderBase):
    pass

class MedicationReminderResponse(MedicationReminderBase):
    medication_id: UUID
    mother_id: UUID

    class Config:
        from_attributes = True

# --- NOTIFICATIONS ---
class NotificationResponse(BaseModel):
    notification_id: UUID
    user_id: UUID
    title: str
    message: str
    notification_type: str
    is_read: bool
    created_at: datetime

    class Config:
        from_attributes = True

# --- MILESTONES ---
class PregnancyMilestoneResponse(BaseModel):
    milestone_id: UUID
    week_number: int
    title: str
    description: str

    class Config:
        from_attributes = True

# --- EMERGENCY CONTACTS ---
class EmergencyContactBase(BaseModel):
    name: str
    relationship: str
    phone: str

class EmergencyContactCreate(EmergencyContactBase):
    pass

class EmergencyContactResponse(EmergencyContactBase):
    contact_id: UUID
    mother_id: UUID
    created_at: datetime

    class Config:
        from_attributes = True

# --- FAMILY COMPANIONS ---
class FamilyCompanionBase(BaseModel):
    companion_email: EmailStr
    relationship: Optional[str] = "Companion"

class FamilyCompanionLinkCode(BaseModel):
    link_code: str
    relationship: Optional[str] = "Companion"

class FamilyCompanionResponse(BaseModel):
    companion_id: UUID
    mother_id: UUID
    companion_user_id: UUID
    companion_name: Optional[str] = None
    relationship: Optional[str]
    created_at: datetime

    class Config:
        from_attributes = True

# --- MEDICAL REPORTS & OCR ---
class OCRResultResponse(BaseModel):
    ocr_id: UUID
    extracted_text: Optional[str]
    processed: bool
    created_at: datetime

    class Config:
        from_attributes = True

class MedicalReportBase(BaseModel):
    report_name: str
    report_type: str
    file_url: Optional[str] = None

class MedicalReportResponse(MedicalReportBase):
    report_id: UUID
    mother_id: UUID
    uploaded_at: datetime
    ocr_results: List[OCRResultResponse] = []

    class Config:
        from_attributes = True

# --- AI CHAT HISTORY ---
class AIChatHistoryBase(BaseModel):
    user_message: str

class AIChatHistoryResponse(BaseModel):
    chat_id: UUID
    mother_id: UUID
    user_message: str
    ai_response: str
    created_at: datetime

    class Config:
        from_attributes = True
