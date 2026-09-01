from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from datetime import date
from typing import List
from .. import crud, schemas, models
from .auth import get_current_user
from ..database import get_db

router = APIRouter(
    prefix="/wellness",
    tags=["Wellness Tracking"]
)

# --- WATER TRACKING ---
@router.get("/water", response_model=schemas.WaterTrackingResponse)
def read_water_log(
    log_date: date,
    current_user: models.User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    log = crud.get_water_logs(db, current_user.user_id, log_date)
    if not log:
        # Return a zero log if none exists yet for the date
        return {"water_id": "00000000-0000-0000-0000-000000000000", "mother_id": current_user.user_id, "intake_date": log_date, "glasses": 0}
    return log

@router.post("/water", response_model=schemas.WaterTrackingResponse)
def update_water_log(
    glasses: int,
    log_date: date,
    current_user: models.User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    return crud.log_water(db, current_user.user_id, log_date, glasses)

# --- STEPS TRACKING ---
@router.get("/steps", response_model=schemas.StepTrackingResponse)
def read_step_log(
    log_date: date,
    current_user: models.User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    log = crud.get_step_logs(db, current_user.user_id, log_date)
    if not log:
        return {"step_id": "00000000-0000-0000-0000-000000000000", "mother_id": current_user.user_id, "tracking_date": log_date, "steps": 0}
    return log

@router.post("/steps", response_model=schemas.StepTrackingResponse)
def update_step_log(
    steps: int,
    log_date: date,
    current_user: models.User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    return crud.log_steps(db, current_user.user_id, log_date, steps)

# --- SLEEP TRACKING ---
@router.get("/sleep", response_model=schemas.SleepTrackingResponse)
def read_sleep_log(
    log_date: date,
    current_user: models.User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    log = crud.get_sleep_logs(db, current_user.user_id, log_date)
    if not log:
        return {"sleep_id": "00000000-0000-0000-0000-000000000000", "mother_id": current_user.user_id, "tracking_date": log_date, "sleep_hours": 0.0}
    return log

@router.post("/sleep", response_model=schemas.SleepTrackingResponse)
def update_sleep_log(
    sleep_hours: float,
    log_date: date,
    current_user: models.User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    return crud.log_sleep(db, current_user.user_id, log_date, sleep_hours)

# --- CLINICAL HEALTH METRICS ---
@router.get("/metrics", response_model=List[schemas.HealthMetricResponse])
def read_health_metrics(
    current_user: models.User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    return crud.get_health_metrics(db, current_user.user_id)

@router.post("/metrics", response_model=schemas.HealthMetricResponse)
def create_health_metric(
    metric: schemas.HealthMetricCreate,
    current_user: models.User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    return crud.create_health_metric(db, current_user.user_id, metric)
