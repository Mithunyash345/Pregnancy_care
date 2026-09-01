from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from typing import List
from uuid import UUID
from .. import crud, schemas, models
from .auth import get_current_user
from ..database import get_db

router = APIRouter(
    prefix="/notifications",
    tags=["Notifications & Milestones"]
)

@router.get("", response_model=List[schemas.NotificationResponse])
def get_user_notifications(
    current_user: models.User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    return crud.get_notifications(db, current_user.user_id)

@router.put("/{notification_id}/read", response_model=schemas.NotificationResponse)
def read_notification(
    notification_id: UUID,
    current_user: models.User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    db_notif = db.query(models.Notification).filter(models.Notification.notification_id == notification_id).first()
    if not db_notif:
        raise HTTPException(status_code=404, detail="Notification not found")
    if db_notif.user_id != current_user.user_id:
        raise HTTPException(status_code=403, detail="Not authorized")
        
    return crud.mark_notification_read(db, notification_id)

@router.get("/milestones", response_model=List[schemas.PregnancyMilestoneResponse])
def get_pregnancy_milestones(
    current_user: models.User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    # Fetch milestones
    milestones = db.query(models.PregnancyMilestone).order_by(models.PregnancyMilestone.week_number.asc()).all()
    
    # If empty, pre-populate default milestones for week 24
    if not milestones:
        m1 = models.PregnancyMilestone(
            week_number=24,
            title="Senses Developing",
            description="Your baby's taste buds are forming, and they can hear external sounds including voices and heartbeat."
        )
        m2 = models.PregnancyMilestone(
            week_number=25,
            title="Lungs Mature",
            description="The lungs are developing surfactant, preparing for breathing air after birth."
        )
        db.add_all([m1, m2])
        db.commit()
        milestones = db.query(models.PregnancyMilestone).order_by(models.PregnancyMilestone.week_number.asc()).all()
        
    return milestones
