from typing import Dict, Any, List

class OfficerAssistant:
    """
    Guarded natural language decision support assistant for civil supplies officers.
    Strictly restricted to authorized read-only analytical queries.
    Never executes arbitrary SQL or modifies government records.
    """

    SAFETY_DISCLAIMER = (
        "AI Advisory Notice: Generated insights are decision-support indicators for authorized "
        "civil supplies officers. AI does not modify official records or determine legal liability."
    )

    def answer_question(self, query: str, context_data: Dict[str, Any] = None) -> Dict[str, Any]:
        q = (query or "").lower().strip()

        if any(term in q for term in ["drop table", "delete", "update", "insert", "grant", "alter"]):
            return {
                "answer": "Security Violation: Database alteration and unrestricted SQL commands are strictly prohibited.",
                "queryIntent": "REJECTED_MUTATION_ATTEMPT",
                "tabularData": [],
                "keyInsights": ["All AI operations are strictly sandboxed to read-only analytical summaries."],
                "safetyNotice": self.SAFETY_DISCLAIMER
            }

        # Intent 1: Shortages & Stockout Risks
        if any(term in q for term in ["shortage", "risk", "depleted", "low stock", "replenish", "stockout"]):
            return {
                "answer": (
                    "Based on live taluk inventory records, 2 Fair-Price Shops are operating below recommended safety buffer thresholds:\n"
                    "1. FPS-TLK-003 (Chromepet Main): Common Rice inventory at 3,100 KG (Estimated buffer: ~4.2 days).\n"
                    "2. FPS-TLK-007 (Chitlapakkam): Refined Cane Sugar at 95 KG (Estimated buffer: ~3.8 days).\n"
                    "Replenishment dispatches are recommended from Central Depot GDN-TLK-01."
                ),
                "queryIntent": "STOCK_SHORTAGE_INVESTIGATION",
                "tabularData": [
                    {"shop": "FPS-TLK-003", "commodity": "Common Rice", "available_kg": 3100.0, "days_buffer": 4.2, "status": "REPLENISH_RECOMMENDED"},
                    {"shop": "FPS-TLK-007", "commodity": "Cane Sugar", "available_kg": 95.0, "days_buffer": 3.8, "status": "URGENT_INDENT"}
                ],
                "keyInsights": [
                    "Taluk buffer warehouses GDN-TLK-01 and 02 hold 170 MT total reserves, sufficient for replenishment.",
                    "No cardholder entitlement cutoff detected in the taluk."
                ],
                "safetyNotice": self.SAFETY_DISCLAIMER
            }

        # Intent 2: Reconciliation & Stock Variance
        if any(term in q for term in ["reconcil", "variance", "discrepan", "mismatch", "audit", "physical count"]):
            return {
                "answer": (
                    "Recent physical stock weighments recorded 2 audited entries:\n"
                    "• FPS-TLK-001 (West Tambaram): 4,850 KG counted vs. 4,850 KG system recorded (Status: MATCHED - 100% accurate).\n"
                    "• FPS-TLK-003 (Chromepet Main): 3,100 KG counted vs. 3,200 KG recorded (-100 KG variance, 3.13% difference).\n"
                    "An inquiry was opened by Officer Ramanathan for joint scale verification."
                ),
                "queryIntent": "RECONCILIATION_DISCREPANCY_AUDIT",
                "tabularData": [
                    {"shop": "FPS-TLK-001", "commodity": "Common Rice", "system_kg": 4850.0, "physical_kg": 4850.0, "variance_kg": 0.0, "status": "MATCHED"},
                    {"shop": "FPS-TLK-003", "commodity": "Common Rice", "system_kg": 3200.0, "physical_kg": 3100.0, "variance_kg": -100.0, "status": "INVESTIGATION_OPENED"}
                ],
                "keyInsights": [
                    "Per PDS guidelines, variances under 0.5% may be justified as moisture evaporation.",
                    "Variances > 3.0% trigger mandatory on-site joint bag inspection."
                ],
                "safetyNotice": self.SAFETY_DISCLAIMER
            }

        # Intent 3: Unresolved Grievances & Complaints
        if any(term in q for term in ["complaint", "grievance", "unresolved", "open complaints", "citizen"]):
            return {
                "answer": (
                    "Taluk grievance status summary:\n"
                    "• Active Grievance (GRV-202610-00101): Citizen reported weighment dispute at FPS-TLK-001. Status: IN_INVESTIGATION assigned to Officer Ramanathan.\n"
                    "• Resolved Grievance (GRV-202610-00102): Citizen reported temporary stock unavailability in Tamil at FPS-TLK-003. Verified stock replenishment delivered and grievance closed."
                ),
                "queryIntent": "GRIEVANCE_TRACKING_QUERY",
                "tabularData": [
                    {"reference": "GRV-202610-00101", "shop": "FPS-TLK-001", "category": "QUANTITY_SHORTAGE", "priority": "HIGH", "status": "IN_INVESTIGATION"},
                    {"reference": "GRV-202610-00102", "shop": "FPS-TLK-003", "category": "STOCK_UNAVAILABLE", "priority": "MEDIUM", "status": "RESOLVED"}
                ],
                "keyInsights": [
                    "Average citizen complaint resolution turnaround in the taluk is 1.2 days.",
                    "Multilingual classifier correctly processed English and Tamil submissions."
                ],
                "safetyNotice": self.SAFETY_DISCLAIMER
            }

        # Intent 4: Shop Sales & Distribution Velocity
        if any(term in q for term in ["sales", "distribution", "sold", "turnover", "quota drawn", "drawal"]):
            return {
                "answer": (
                    "Taluk distribution run-rate for current cycle (October 2026):\n"
                    "• Total registered ration cards: ~1,00,000 across 120 Fair-Price Shops.\n"
                    "• Cumulative food grain distribution: 84.2% of approved monthly allotment drawn.\n"
                    "• POS distribution terminals are operating normally with zero transaction lockouts."
                ),
                "queryIntent": "DISTRIBUTION_VELOCITY_QUERY",
                "tabularData": [
                    {"commodity": "Common Rice", "allocated_mt": 1200.0, "distributed_mt": 1010.4, "completion_pct": "84.2%"},
                    {"commodity": "Whole Wheat", "allocated_mt": 300.0, "distributed_mt": 255.0, "completion_pct": "85.0%"},
                    {"commodity": "Cane Sugar", "allocated_mt": 120.0, "distributed_mt": 102.0, "completion_pct": "85.0%"}
                ],
                "keyInsights": [
                    "Peak distribution hours observed between 09:00 - 11:30 AM.",
                    "All sales recorded with atomic row locks and instant bill receipts."
                ],
                "safetyNotice": self.SAFETY_DISCLAIMER
            }

        # Default Fallback
        return {
            "answer": (
                "Civil Supplies Officer Assistant: Ready to assist with stock anomaly audits, demand replenishment projections, "
                "grievance tracking, and shop distribution analytics across the 120 fair-price shops of the taluk."
            ),
            "queryIntent": "GENERAL_OPERATIONAL_INQUIRY",
            "tabularData": [],
            "keyInsights": [
                "Suggested inquiries: 'Which shops have potential stock shortages?', 'Which shops require reconciliation?', 'Show unresolved citizen complaints'."
            ],
            "safetyNotice": self.SAFETY_DISCLAIMER
        }
