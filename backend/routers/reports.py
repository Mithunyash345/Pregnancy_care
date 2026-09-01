from fastapi import APIRouter, Depends, HTTPException, UploadFile, File
from sqlalchemy.orm import Session
from typing import List
from uuid import UUID
from .. import crud, schemas, models
from .auth import get_current_user
from ..database import get_db

router = APIRouter(
    prefix="/reports",
    tags=["Medical Reports & OCR"]
)

@router.get("", response_model=List[schemas.MedicalReportResponse])
def get_all_reports(
    current_user: models.User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    if current_user.role != "mother":
        raise HTTPException(status_code=400, detail="Only mothers can access medical reports")
    return crud.get_reports(db, current_user.user_id)

@router.post("/upload", response_model=schemas.MedicalReportResponse)
def upload_report(
    report_name: str,
    report_type: str,
    file_url: str = "http://example.com/mock_report.pdf",
    current_user: models.User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    if current_user.role != "mother":
        raise HTTPException(status_code=400, detail="Only mothers can upload reports")
    return crud.create_report(db, current_user.user_id, report_name, report_type, file_url)

@router.post("/{report_id}/scan", response_model=schemas.OCRResultResponse)
def scan_report_ocr(
    report_id: UUID,
    current_user: models.User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    # Verify owner of the report
    db_report = db.query(models.MedicalReport).filter(models.MedicalReport.report_id == report_id).first()
    if not db_report:
        raise HTTPException(status_code=404, detail="Medical report not found")
    if db_report.mother_id != current_user.user_id:
        raise HTTPException(status_code=403, detail="Not authorized to access this report")
        
    # --- OCR AND AI PLACEHOLDER IMPLEMENTATION ---
    # In a future phase, integrate an actual OCR engine (like Tesseract or Google Cloud OCR)
    # and a LLM API (like OpenAI, Anthropic, or Gemini) here.
    
    mock_extracted_text = (
        "Routine Second Trimester CBC Panel\n"
        "Date: 2026-06-26\n"
        "Patient: Elena Rostova\n"
        "Hemoglobin: 11.2 g/dL (Reference Range: 12.0 - 15.0 g/dL) [LOW]\n"
        "Fasting Glucose: 92 mg/dL (Reference Range: <95 mg/dL) [NORMAL]\n"
    )
    
    db_ocr = crud.create_ocr_result(db, report_id, mock_extracted_text, processed=True)
    
    # Generate an automated system notification
    tip_msg = (
        f"Your report '{db_report.report_name}' has been processed. "
        f"Hemoglobin is slightly low (11.2 g/dL). Recommend scheduling a follow-up consultation."
    )
    crud.create_notification(db, current_user.user_id, "Report Processed", tip_msg, "Tip")
    
    return db_ocr
