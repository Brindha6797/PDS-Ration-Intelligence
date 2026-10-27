# pyrefly: ignore[missing-import]
from fastapi import FastAPI, HTTPException, status
# pyrefly: ignore[missing-import]
from fastapi.middleware.cors import CORSMiddleware
# pyrefly: ignore[missing-import]
from pydantic import BaseModel
from typing import Optional, List, Dict, Any

from app.anomaly_detector import StockAnomalyDetector
from app.demand_forecaster import DemandForecaster
from app.complaint_classifier import MultilingualComplaintClassifier
from app.officer_assistant import OfficerAssistant

app = FastAPI(
    title="PDS Ration Intelligence AI Microservice",
    description="AI-assisted stock anomaly detection, demand forecasting, multilingual grievance triage, and officer assistant.",
    version="1.0.0"
)

# CORS configuration
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Initialize Intelligence Engines
anomaly_detector = StockAnomalyDetector()
demand_forecaster = DemandForecaster()
complaint_classifier = MultilingualComplaintClassifier()
officer_assistant = OfficerAssistant()

# Pydantic Schemas
class ComplaintClassifyRequest(BaseModel):
    text: str
    language: Optional[str] = "en"

class OfficerAssistantRequest(BaseModel):
    query: str

@app.get("/health")
def health_check():
    return {
        "status": "UP",
        "service": "PDS-AI-Intelligence-Engine",
        "modules": ["StockAnomalyDetector", "DemandForecaster", "MultilingualClassifier", "OfficerAssistant"]
    }

@app.get("/api/v1/ai/anomalies")
def detect_anomalies():
    # Representative taluk reconciliation sample dataset
    sample_records = [
        {"shop_code": "FPS-TLK-001", "shop_name": "West Tambaram #1", "commodity_name": "Common Rice", "recorded_stock": 4850.0, "physical_stock": 4850.0, "variance_quantity": 0.0},
        {"shop_code": "FPS-TLK-002", "shop_name": "East Tambaram #2", "commodity_name": "Common Rice", "recorded_stock": 5200.0, "physical_stock": 5185.0, "variance_quantity": -15.0},
        {"shop_code": "FPS-TLK-003", "shop_name": "Chromepet Main #3", "commodity_name": "Common Rice", "recorded_stock": 3200.0, "physical_stock": 3100.0, "variance_quantity": -100.0},
        {"shop_code": "FPS-TLK-005", "shop_name": "Mudichur Rural #5", "commodity_name": "Cane Sugar", "recorded_stock": 380.0, "physical_stock": 360.0, "variance_quantity": -20.0},
        {"shop_code": "FPS-TLK-007", "shop_name": "Chitlapakkam #7", "commodity_name": "Edible Oil", "recorded_stock": 150.0, "physical_stock": 150.0, "variance_quantity": 0.0}
    ]
    return anomaly_detector.analyze_reconciliations(sample_records)

@app.get("/api/v1/ai/forecast")
def forecast_demand(shopId: Optional[int] = None, commodityId: Optional[int] = None):
    sample_stock = [
        {"shop_code": "FPS-TLK-001", "shop_name": "West Tambaram #1", "commodity_name": "Common Rice", "current_quantity": 4850.0, "total_distributed_month": 1350.0, "buffer_stock_threshold": 500.0, "days_elapsed": 20},
        {"shop_code": "FPS-TLK-001", "shop_name": "West Tambaram #1", "commodity_name": "Whole Wheat", "current_quantity": 1200.0, "total_distributed_month": 300.0, "buffer_stock_threshold": 300.0, "days_elapsed": 20},
        {"shop_code": "FPS-TLK-003", "shop_name": "Chromepet Main #3", "commodity_name": "Common Rice", "current_quantity": 310.0, "total_distributed_month": 1800.0, "buffer_stock_threshold": 500.0, "days_elapsed": 20},
        {"shop_code": "FPS-TLK-007", "shop_name": "Chitlapakkam #7", "commodity_name": "Refined Cane Sugar", "current_quantity": 95.0, "total_distributed_month": 450.0, "buffer_stock_threshold": 150.0, "days_elapsed": 20}
    ]
    return demand_forecaster.forecast_shop_commodity(sample_stock)

@app.post("/api/v1/ai/classify-complaint")
def classify_complaint(req: ComplaintClassifyRequest):
    return complaint_classifier.classify(req.text, req.language)

@app.post("/api/v1/ai/officer-assistant")
def ask_officer_assistant(req: OfficerAssistantRequest):
    return officer_assistant.answer_question(req.query)

if __name__ == "__main__":
    # pyrefly: ignore[missing-import]
    import uvicorn
    uvicorn.run("app.main:app", host="0.0.0.0", port=8000, reload=True)
