# PDS-Ration-Intelligence: AI Intelligence Engine Microservice
## Technology: Python 3.11, FastAPI, Pandas, Scikit-learn, Uvicorn

### 1. Overview
The AI Intelligence Service provides civil supplies enforcement officers and administrators with four key decision-support modules:
1. **Stock Anomaly Detection (`GET /api/v1/ai/anomalies`):** Scans historical physical counts, stock movements, and reconciliations to flag shops with unexplained losses or rapid negative variances with explainable investigation recommendations.
2. **Predictive Demand Forecasting (`GET /api/v1/ai/forecast`):** Calculates 30-day commodity requirements, daily run-rates, and stockout horizons (SAFE, MODERATE, HIGH, CRITICAL) for proactive replenishment planning.
3. **Multilingual Grievance Classifier (`POST /api/v1/ai/classify-complaint`):** Analyzes citizen complaints submitted in English or Tamil, extracting intent, sentiment, urgency score, and suggested officer actions.
4. **Guarded Officer Assistant (`POST /api/v1/ai/officer-assistant`):** Natural language Q&A interface for authorized officers sandboxed to read-only analytical summaries.

---

### 2. Setup & Execution

```bash
cd ai-service

# Create virtual environment (optional)
python -m venv venv
# On Windows:
.\venv\Scripts\activate
# On Linux/macOS:
source venv/bin/activate

# Install dependencies
pip install -r requirements.txt

# Run automated tests
pytest tests/

# Start FastAPI development server
uvicorn app.main:app --host 0.0.0.0 --port 8000 --reload
```

The interactive OpenAPI documentation will be accessible at `http://localhost:8000/docs`.
