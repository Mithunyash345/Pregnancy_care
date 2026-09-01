from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from typing import List
from .. import crud, schemas, models
from .auth import get_current_user
from ..database import get_db

router = APIRouter(
    prefix="/chat",
    tags=["AI Assistant Chat"]
)

@router.get("/history", response_model=List[schemas.AIChatHistoryResponse])
def get_chat_messages(
    current_user: models.User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    if current_user.role != "mother":
        raise HTTPException(status_code=400, detail="Only mothers can chat with the assistant")
    return crud.get_chat_history(db, current_user.user_id)

@router.post("/message", response_model=schemas.AIChatHistoryResponse)
def send_chat_message(
    chat_msg: schemas.AIChatHistoryBase,
    current_user: models.User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    if current_user.role != "mother":
        raise HTTPException(status_code=400, detail="Only mothers can chat with the assistant")
        
    user_query = chat_msg.user_message
    lower_query = user_query.lower()
    
    # --- AI ASSISTANT PLACEHOLDER ---
    # Here you will integrate with Gemini API, OpenAI, or a custom LLM model in the future.
    # We use a rule-based mock engine matching the frontend's expected outputs for now:
    
    if "heartburn" in lower_query:
        ai_reply = "Heartburn is common in week 24 because progesterone relaxes the valve between your stomach and esophagus. Try eating smaller meals and drinking ginger tea. *Remember: AI assists, doctors decide.*"
    elif "kick" in lower_query or "movement" in lower_query:
        ai_reply = "Around week 24, babies are active and sleep in cycles. Feeling 10 movements within a 2-hour window is a healthy baseline. *Remember: AI assists, doctors decide.*"
    elif "cramp" in lower_query or "pain" in lower_query:
        ai_reply = "Mild cramping can be due to ligament stretching. If severe or rhythmic, consult emergency support immediately. *Remember: AI assists, doctors decide.*"
    else:
        ai_reply = "Based on your clinical record, this is common. Rest, stay hydrated, and follow up with your doctor if symptoms persist. *Remember: AI assists, doctors decide.*"
        
    # Save user message and AI response to db
    return crud.log_chat_message(db, current_user.user_id, user_query, ai_reply)
