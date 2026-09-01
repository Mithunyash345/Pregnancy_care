from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from .database import engine, Base
from .routers import auth, users, wellness, doctors, medications, companions, reports, chat, notifications, emergency

# Bind metadata to create any missing tables (if any) without deleting existing ones
Base.metadata.create_all(bind=engine)

app = FastAPI(
    title="Bloom Pregnancy Care API",
    description="FastAPI Backend for the Bloom Pregnancy Care Android Application, backed by Neon PostgreSQL database.",
    version="1.0.0"
)

# Set up CORS middleware so mobile clients, local dev servers, and emulators can communicate
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Include Routers
app.include_router(auth.router)
app.include_router(users.router)
app.include_router(wellness.router)
app.include_router(doctors.router)
app.include_router(medications.router)
app.include_router(companions.router)
app.include_router(reports.router)
app.include_router(chat.router)
app.include_router(notifications.router)
app.include_router(emergency.router)

@app.get("/")
def read_root():
    return {
        "status": "online",
        "app": "Bloom Pregnancy Care Backend",
        "version": "1.0.0",
        "documentation": "/docs"
    }
