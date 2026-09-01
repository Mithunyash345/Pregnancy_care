from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from typing import List
from uuid import UUID
from .. import crud, schemas, models
from .auth import get_current_user
from ..database import get_db

router = APIRouter(
    prefix="/doctors",
    tags=["Doctors & Appointments"]
)

# --- DOCTOR LISTINGS & PROFILE ---
@router.get("", response_model=List[schemas.DoctorResponse])
def get_all_doctors(
    current_user: models.User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    return crud.get_doctors(db)

@router.post("/profile", response_model=schemas.DoctorResponse)
def create_or_update_doctor_profile(
    doctor_info: schemas.DoctorBase,
    current_user: models.User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    if current_user.role != "doctor":
        raise HTTPException(status_code=400, detail="Only users with role 'doctor' can set up a doctor profile")
    
    db_doctor = crud.get_doctor_by_user_id(db, current_user.user_id)
    if db_doctor:
        # Update existing
        db_doctor.specialization = doctor_info.specialization
        db_doctor.hospital_name = doctor_info.hospital_name
        db_doctor.experience_years = doctor_info.experience_years
        db_doctor.consultation_fee = doctor_info.consultation_fee
        db_doctor.about = doctor_info.about
        db_doctor.available = doctor_info.available
        db.commit()
        db.refresh(db_doctor)
        return db_doctor
    else:
        return crud.create_doctor_profile(db, current_user.user_id, doctor_info)

# --- APPOINTMENTS ---
@router.get("/appointments", response_model=List[schemas.AppointmentResponse])
def list_appointments(
    current_user: models.User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    if current_user.role == "mother":
        return crud.get_appointments_for_mother(db, current_user.user_id)
    elif current_user.role == "doctor":
        db_doctor = crud.get_doctor_by_user_id(db, current_user.user_id)
        if not db_doctor:
            return []
        return crud.get_appointments_for_doctor(db, db_doctor.doctor_id)
    else:
        # Companion sees linked mother's appointments if they are linked
        # For simplicity, returning empty if other roles
        return []

@router.post("/appointments", response_model=schemas.AppointmentResponse)
def book_appointment(
    appt: schemas.AppointmentCreate,
    current_user: models.User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    if current_user.role != "mother":
        raise HTTPException(status_code=400, detail="Only mothers can book appointments")
    return crud.create_appointment(db, current_user.user_id, appt)

@router.put("/appointments/{appointment_id}/status", response_model=schemas.AppointmentResponse)
def change_appointment_status(
    appointment_id: UUID,
    status_update: schemas.AppointmentUpdateStatus,
    current_user: models.User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    # Only doctors (or potentially mothers cancelling) can update status
    db_appt = db.query(models.Appointment).filter(models.Appointment.appointment_id == appointment_id).first()
    if not db_appt:
        raise HTTPException(status_code=404, detail="Appointment not found")
        
    if current_user.role == "doctor":
        db_doctor = crud.get_doctor_by_user_id(db, current_user.user_id)
        if not db_doctor or db_appt.doctor_id != db_doctor.doctor_id:
            raise HTTPException(status_code=403, detail="Not authorized to modify this appointment")
    elif current_user.role == "mother":
        if db_appt.mother_id != current_user.user_id:
            raise HTTPException(status_code=403, detail="Not authorized to modify this appointment")
        if status_update.status != "Cancelled":
            raise HTTPException(status_code=400, detail="Mothers can only set status to 'Cancelled'")
    else:
        raise HTTPException(status_code=403, detail="Not authorized")
        
    updated = crud.update_appointment_status(db, appointment_id, status_update.status)
    
    # Trigger notification
    recipient_id = db_appt.mother_id if current_user.role == "doctor" else db_appt.doctor_id
    # Get doctor details to mention in notification
    doc_msg = f"Your appointment status has been updated to '{status_update.status}'."
    crud.create_notification(db, recipient_id, "Appointment Update", doc_msg, "Appointment")
    
    return updated
