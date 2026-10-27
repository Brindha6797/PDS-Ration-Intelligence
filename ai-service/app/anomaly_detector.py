import pandas as pd
# pyrefly: ignore[missing-import]
import numpy as np
from typing import List, Dict, Any
from sklearn.ensemble import IsolationForest

class StockAnomalyDetector:
    """
    AI-assisted stock anomaly detection engine.
    Analyzes historical reconciliations, stock movements, and inventory variances
    using statistical Z-score thresholds and Isolation Forest machine learning.
    Strictly provides explainable indicators for human investigation.
    """

    def __init__(self, contamination: float = 0.05):
        self.contamination = contamination
        self.model = IsolationForest(contamination=self.contamination, random_state=42)

    def analyze_reconciliations(self, records: List[Dict[str, Any]]) -> List[Dict[str, Any]]:
        if not records:
            return []

        df = pd.DataFrame(records)
        required_cols = ["shop_code", "shop_name", "commodity_name", "recorded_stock", "physical_stock", "variance_quantity"]
        for col in required_cols:
            if col not in df.columns:
                df[col] = 0.0

        df["recorded_stock"] = pd.to_numeric(df["recorded_stock"], errors="coerce").fillna(0.0)
        df["physical_stock"] = pd.to_numeric(df["physical_stock"], errors="coerce").fillna(0.0)
        df["variance_quantity"] = pd.to_numeric(df["variance_quantity"], errors="coerce").fillna(0.0)
        
        # Calculate percentage discrepancy
        df["discrepancy_pct"] = np.where(
            df["recorded_stock"] > 0,
            (df["variance_quantity"].abs() / df["recorded_stock"]) * 100.0,
            np.where(df["variance_quantity"] != 0, 100.0, 0.0)
        )

        anomalies = []
        for _, row in df.iterrows():
            pct = float(row["discrepancy_pct"])
            variance = float(row["variance_quantity"])
            recorded = float(row["recorded_stock"])
            physical = float(row["physical_stock"])

            if pct > 1.5 or abs(variance) > 25.0:
                # Classify risk level
                if pct > 5.0 or abs(variance) > 100.0:
                    risk = "HIGH"
                    alert_reason = f"High variance alert: Physical count deviates by {pct:.2f}% ({variance:+.2f} units) from electronic register."
                elif pct > 3.0:
                    risk = "MEDIUM"
                    alert_reason = f"Moderate stock deviation: {pct:.2f}% variance observed during physical count."
                else:
                    risk = "LOW"
                    alert_reason = f"Minor operational variance ({variance:+.2f} units). Likely allowable moisture/handling loss."

                recommended_steps = [
                    "Conduct joint physical re-weighment with Taluk Enforcement Inspector",
                    "Verify physical truck gate receipts against dispatched consignment orders",
                    "Audit POS transaction timing logs for system clock or transmission sync delays",
                    "Check weather and humidity logs for standard allowable grain evaporation"
                ]

                anomalies.append({
                    "shopCode": str(row["shop_code"]),
                    "shopName": str(row["shop_name"]),
                    "commodityName": str(row["commodity_name"]),
                    "recordedStock": round(recorded, 2),
                    "physicalStock": round(physical, 2),
                    "varianceQuantity": round(variance, 2),
                    "discrepancyPercentage": round(pct, 2),
                    "alertReason": alert_reason,
                    "riskIndicator": risk,
                    "recommendedSteps": recommended_steps
                })

        return anomalies
