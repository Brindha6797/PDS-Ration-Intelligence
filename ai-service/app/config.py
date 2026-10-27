import os

class Config:
    PORT: int = int(os.getenv("AI_SERVICE_PORT", "8000"))
    HOST: str = os.getenv("AI_SERVICE_HOST", "0.0.0.0")
    BACKEND_API_URL: str = os.getenv("BACKEND_API_URL", "http://localhost:8080/api/v1")
    ANOMALY_THRESHOLD_PERCENT: float = float(os.getenv("ANOMALY_THRESHOLD_PERCENT", "2.0"))
    DEFAULT_BUFFER_DAYS: int = int(os.getenv("DEFAULT_BUFFER_DAYS", "7"))

settings = Config()
