from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from typing import List
from .. import crud, schemas, models
from .auth import get_current_user
from ..database import get_db

router = APIRouter(
    prefix="/companions",
    tags=["Companion Linkage & Alerts"]
)

# --- GET LINKAGE CODE ---
@router.get("/code")
def get_link_code(current_user: models.User = Depends(get_current_user)):
    if current_user.role != "mother":
        raise HTTPException(status_code=400, detail="Only mothers can generate linkage codes")
    
    # We can use a substring of their user ID for a simple linked code
    short_code = f"BLOOM-CO-{str(current_user.user_id)[:4].upper()}"
    return {"link_code": short_code, "mother_email": current_user.email}

# --- LINK COMPANION ---
@router.post("/link", response_model=schemas.FamilyCompanionResponse)
def link_with_mother(
    link_data: schemas.FamilyCompanionLinkCode,
    current_user: models.User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    if current_user.role != "companion":
        raise HTTPException(status_code=400, detail="Only companions can link to a mother's account")
    
    # Extract mother's short code or look up by mother's email/short code
    # For a prototype, let's search all users with role 'mother' and find one matching the last 4 characters of the code
    code_suffix = link_data.link_code.split("-")[-1].lower()
    
    mothers = db.query(models.User).filter(models.User.role == "mother").all()
    mother_user = None
    for m in mothers:
        if str(m.user_id).startswith(code_suffix):
            mother_user = m
            break
            
    if not mother_user:
        raise HTTPException(status_code=404, detail="Invalid link code. No matching mother found.")
        
    # Check if already linked
    existing = db.query(models.FamilyCompanion).filter(
        models.FamilyCompanion.mother_id == mother_user.user_id,
        models.FamilyCompanion.companion_user_id == current_user.user_id
    ).first()
    
    if existing:
        # Return existing link
        existing.companion_name = current_user.full_name
        return existing
        
    new_link = crud.link_companion(db, mother_user.user_id, current_user.user_id, link_data.relationship)
    
    # Notify the mother
    crud.create_notification(
        db, 
        mother_user.user_id, 
        "Companion Linked", 
        f"{current_user.full_name} has linked as your companion.", 
        "Alert"
    )
    
    new_link.companion_name = current_user.full_name
    return new_link

# --- LIST LINKED MEMBERS ---
@router.get("/linked", response_model=List[schemas.FamilyCompanionResponse])
def get_linked_accounts(
    current_user: models.User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    if current_user.role == "mother":
        return crud.get_linked_companions(db, current_user.user_id)
    elif current_user.role == "companion":
        links = db.query(models.FamilyCompanion).filter(
            models.FamilyCompanion.companion_user_id == current_user.user_id
        ).all()
        # Add mother name helper
        for link in links:
            m_user = db.query(models.User).filter(models.User.user_id == link.mother_id).first()
            link.companion_name = m_user.full_name if m_user else "Mother"
        return links
    else:
        return []

# --- PANIC / EMERGENCY ALERT ---
@router.post("/alert")
def trigger_alert(
    message: str = "Emergency: Mother requires immediate assistance!",
    current_user: models.User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    if current_user.role != "mother":
        raise HTTPException(status_code=400, detail="Only mothers can trigger panic alerts")
        
    # Find all companions linked to this mother
    companions = db.query(models.FamilyCompanion).filter(
        models.FamilyCompanion.mother_id == current_user.user_id
    ).all()
    
    if not companions:
        return {"status": "warning", "message": "Alert triggered, but no companions are linked to your profile."}
        
    for comp in companions:
        crud.create_notification(
            db,
            comp.companion_user_id,
            "EMERGENCY ALERT",
            f"{current_user.full_name} has triggered a panic alert: {message}",
            "Alert"
        )
        
    return {"status": "success", "message": f"Alert sent to {len(companions)} linked companions."}
