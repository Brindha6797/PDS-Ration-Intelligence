import pandas as pd
# pyrefly: ignore[missing-import]
import numpy as np
from typing import List, Dict, Any

class DemandForecaster:
    """
    Predictive demand forecasting engine for fair-price shops.
    Calculates moving daily run-rates, seasonality, stockout horizons,
    and recommended replenishment quantities for officer approval.
    """

    def forecast_shop_commodity(self, stock_data: List[Dict[str, Any]]) -> List[Dict[str, Any]]:
        results = []
        for item in stock_data:
            shop_code = str(item.get("shop_code", "UNKNOWN"))
            shop_name = str(item.get("shop_name", "Fair Price Shop"))
            commodity_name = str(item.get("commodity_name", "Commodity"))
            available_stock = float(item.get("current_quantity", 0.0))
            mtd_distributed = float(item.get("total_distributed_month", 0.0))
            buffer_threshold = float(item.get("buffer_stock_threshold", 100.0))
            days_elapsed = max(1, int(item.get("days_elapsed", 15)))

            # Daily consumption velocity (run-rate)
            daily_run_rate = round(mtd_distributed / days_elapsed, 2)
            if daily_run_rate <= 0.0:
                daily_run_rate = round(buffer_threshold / 30.0, 2)

            # Projected 30-day requirement
            estimated_demand = round(daily_run_rate * 30.0, 2)

            # Days of stock remaining
            if daily_run_rate > 0:
                days_remaining = round(available_stock / daily_run_rate, 1)
            else:
                days_remaining = 30.0

            # Shortage Risk Level & Replenishment
            if days_remaining <= 3.0 or available_stock < (buffer_threshold * 0.3):
                risk = "CRITICAL"
                replenishment = round(max(0.0, (estimated_demand * 1.2) - available_stock), 2)
                explanation = f"Critical shortage alert: Current inventory ({available_stock:.1f}) will exhaust in {days_remaining:.1f} days at daily velocity {daily_run_rate:.1f}."
            elif days_remaining <= 7.0 or available_stock < buffer_threshold:
                risk = "HIGH"
                replenishment = round(max(0.0, estimated_demand - available_stock), 2)
                explanation = f"High replenishment priority: Inventory represents {days_remaining:.1f} days buffer. Indent truckload dispatch."
            elif days_remaining <= 14.0:
                risk = "MODERATE"
                replenishment = round(max(0.0, (estimated_demand * 0.8) - available_stock), 2)
                explanation = f"Moderate stock horizon: Adequate for immediate cycle; schedule regular monthly quota delivery."
            else:
                risk = "SAFE"
                replenishment = 0.0
                explanation = f"Safe buffer: Current inventory covers {days_remaining:.1f} operational days."

            results.append({
                "shopCode": shop_code,
                "shopName": shop_name,
                "commodityName": commodity_name,
                "forecastPeriod": "Next 30 Days",
                "estimatedDemand": estimated_demand,
                "availableStock": round(available_stock, 2),
                "dailyRunRate": daily_run_rate,
                "estimatedDaysRemaining": days_remaining,
                "shortageRiskLevel": risk,
                "recommendedReplenishment": replenishment,
                "explanation": explanation
            })

        return results
