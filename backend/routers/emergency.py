from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from typing import List
from uuid import UUID
from .. import crud, schemas, models
from .auth import get_current_user
from ..database import get_db

router = APIRouter(
    prefix="/emergency",
    tags=["Emergency Support"]
)

@router.get("/contacts", response_model=List[schemas.EmergencyContactResponse])
def get_emergency_contacts(
    current_user: models.User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    if current_user.role != "mother":
        raise HTTPException(status_code=400, detail="Only mothers configure emergency contacts")
    return crud.get_emergency_contacts(db, current_user.user_id)

@router.post("/contacts", response_model=schemas.EmergencyContactResponse)
def add_emergency_contact(
    contact: schemas.EmergencyContactCreate,
    current_user: models.User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    if current_user.role != "mother":
        raise HTTPException(status_code=400, detail="Only mothers can add emergency contacts")
    return crud.create_emergency_contact(db, current_user.user_id, contact)

@router.delete("/contacts/{contact_id}")
def delete_emergency_contact(
    contact_id: UUID,
    current_user: models.User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    db_contact = db.query(models.EmergencyContact).filter(models.EmergencyContact.contact_id == contact_id).first()
    if not db_contact:
        raise HTTPException(status_code=404, detail="Emergency contact not found")
    if db_contact.mother_id != current_user.user_id:
        raise HTTPException(status_code=403, detail="Not authorized")
        
    success = crud.delete_emergency_contact(db, contact_id)
    if success:
        return {"status": "success", "message": "Emergency contact removed"}
    raise HTTPException(status_code=500, detail="Could not remove contact")
