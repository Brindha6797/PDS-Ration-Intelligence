# pyrefly: ignore[missing-import]
import pytest
import sys
import os

# Ensure app package is importable
sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), "..")))

from app.anomaly_detector import StockAnomalyDetector
from app.demand_forecaster import DemandForecaster
from app.complaint_classifier import MultilingualComplaintClassifier
from app.officer_assistant import OfficerAssistant

def test_anomaly_detector_flags_discrepancy():
    detector = StockAnomalyDetector()
    records = [
        {"shop_code": "FPS-01", "shop_name": "Shop 1", "commodity_name": "Rice", "recorded_stock": 5000.0, "physical_stock": 5000.0, "variance_quantity": 0.0},
        {"shop_code": "FPS-02", "shop_name": "Shop 2", "commodity_name": "Rice", "recorded_stock": 1000.0, "physical_stock": 900.0, "variance_quantity": -100.0}
    ]
    anomalies = detector.analyze_reconciliations(records)
    assert len(anomalies) == 1
    assert anomalies[0]["shopCode"] == "FPS-02"
    assert anomalies[0]["riskIndicator"] == "HIGH"
    assert anomalies[0]["discrepancyPercentage"] == 10.0
    assert len(anomalies[0]["recommendedSteps"]) > 0

def test_demand_forecaster_critical_shortage():
    forecaster = DemandForecaster()
    stock_data = [
        {"shop_code": "FPS-01", "shop_name": "Shop 1", "commodity_name": "Rice", "current_quantity": 100.0, "total_distributed_month": 1500.0, "buffer_stock_threshold": 500.0, "days_elapsed": 20}
    ]
    forecasts = forecaster.forecast_shop_commodity(stock_data)
    assert len(forecasts) == 1
    # Run rate: 1500 / 20 = 75 kg/day. Available 100kg -> ~1.3 days remaining
    assert forecasts[0]["dailyRunRate"] == 75.0
    assert forecasts[0]["estimatedDaysRemaining"] <= 3.0
    assert forecasts[0]["shortageRiskLevel"] == "CRITICAL"
    assert forecasts[0]["recommendedReplenishment"] > 0

def test_multilingual_complaint_classifier_english():
    classifier = MultilingualComplaintClassifier()
    res = classifier.classify("The shop operator gave 18kg instead of 20kg rice and refused to reweigh")
    assert res["detectedCategory"] == "QUANTITY_SHORTAGE"
    assert res["suggestedPriority"] == "HIGH"
    assert res["languageDetected"] == "en"

def test_multilingual_complaint_classifier_tamil():
    classifier = MultilingualComplaintClassifier()
    res = classifier.classify("கடை திறக்கவில்லை மற்றும் சர்க்கரை இருப்பு இல்லை")
    assert res["detectedCategory"] in ["STOCK_UNAVAILABLE", "DELAYED_OPENING"]
    assert res["languageDetected"] == "ta"

def test_officer_assistant_safety_sandbox():
    assistant = OfficerAssistant()
    # SQL injection/mutation attempt
    malicious = assistant.answer_question("DROP TABLE card_holders; DELETE FROM shop_stock;")
    assert malicious["queryIntent"] == "REJECTED_MUTATION_ATTEMPT"
    assert "strictly prohibited" in malicious["answer"].lower()

def test_officer_assistant_operational_query():
    assistant = OfficerAssistant()
    res = assistant.answer_question("Which shops have potential stock shortages?")
    assert res["queryIntent"] == "STOCK_SHORTAGE_INVESTIGATION"
    assert len(res["tabularData"]) > 0
    assert "safetyNotice" in res
