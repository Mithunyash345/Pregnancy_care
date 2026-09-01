from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from typing import List
from uuid import UUID
from .. import crud, schemas, models
from .auth import get_current_user
from ..database import get_db

router = APIRouter(
    prefix="/medications",
    tags=["Medications"]
)

@router.get("", response_model=List[schemas.MedicationReminderResponse])
def get_all_medications(
    current_user: models.User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    if current_user.role != "mother":
        raise HTTPException(status_code=400, detail="Only mothers have medication reminders")
    return crud.get_medications(db, current_user.user_id)

@router.post("", response_model=schemas.MedicationReminderResponse)
def add_medication(
    med: schemas.MedicationReminderCreate,
    current_user: models.User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    if current_user.role != "mother":
        raise HTTPException(status_code=400, detail="Only mothers can configure medication reminders")
    return crud.create_medication(db, current_user.user_id, med)

@router.delete("/{medication_id}")
def remove_medication(
    medication_id: UUID,
    current_user: models.User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    # Verify owner
    db_med = db.query(models.MedicationReminder).filter(models.MedicationReminder.medication_id == medication_id).first()
    if not db_med:
        raise HTTPException(status_code=404, detail="Medication reminder not found")
    if db_med.mother_id != current_user.user_id:
        raise HTTPException(status_code=403, detail="Not authorized to delete this reminder")
        
    success = crud.delete_medication(db, medication_id)
    if success:
        return {"status": "success", "message": "Medication reminder deleted"}
    raise HTTPException(status_code=500, detail="Could not delete reminder")
