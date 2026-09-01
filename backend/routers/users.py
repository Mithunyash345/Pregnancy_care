from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from .. import crud, schemas, models
from .auth import get_current_user
from ..database import get_db

router = APIRouter(
    prefix="/users",
    tags=["Users & Profiles"]
)

@router.get("/me", response_model=schemas.UserResponse)
def read_user_me(current_user: models.User = Depends(get_current_user)):
    return current_user

@router.get("/settings", response_model=schemas.UserSettingsResponse)
def read_settings(
    current_user: models.User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    settings = crud.get_user_settings(db, current_user.user_id)
    if not settings:
        settings = crud.update_user_settings(db, current_user.user_id, schemas.UserSettingsBase())
    return settings

@router.put("/settings", response_model=schemas.UserSettingsResponse)
def update_settings(
    settings_update: schemas.UserSettingsBase,
    current_user: models.User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    return crud.update_user_settings(db, current_user.user_id, settings_update)

@router.get("/profile", response_model=schemas.PregnancyProfileResponse)
def read_pregnancy_profile(
    current_user: models.User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    if current_user.role != "mother":
        raise HTTPException(status_code=400, detail="Only users with role 'mother' have a pregnancy profile")
    
    profile = crud.get_pregnancy_profile(db, current_user.user_id)
    if not profile:
        raise HTTPException(status_code=404, detail="Pregnancy profile not found. Please create one.")
    return profile

@router.put("/profile", response_model=schemas.PregnancyProfileResponse)
def update_pregnancy_profile(
    profile_update: schemas.PregnancyProfileCreate,
    current_user: models.User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    if current_user.role != "mother":
        raise HTTPException(status_code=400, detail="Only users with role 'mother' have a pregnancy profile")
    return crud.update_pregnancy_profile(db, current_user.user_id, profile_update)
