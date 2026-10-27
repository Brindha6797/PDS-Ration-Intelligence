# PDS-Ration-Intelligence: System Architecture & Design
## Enterprise Multi-Tier Technical Architecture

### 1. Architectural Overview

The PDS Ration Shop Stock & Entitlement System follows a clean, decoupled, multi-tier enterprise architecture engineered for high concurrency, strict transactional integrity, and auditable governance.

```
+-----------------------------------------------------------------------------------+
|                              PRESENTATION LAYER                                   |
|   React 18 (TypeScript + Vite) Single Page Application                            |
|   - Government-grade Navy/Teal CSS Design System                                  |
|   - Officer Dashboards, POS Sales Terminal, Reconciliation & Grievance Modules    |
|   - Recharts Visual Analytics, Responsive Grid, Token-Based Auth Interceptors    |
+-----------------------------------------------------------------------------------+
                                        |  HTTPS / REST JSON
                                        v
+-----------------------------------------------------------------------------------+
|                               APPLICATION LAYER                                   |
|   Java 17 Spring Boot 3.x Enterprise REST API Server                              |
|   - Spring Security 6 & Stateless JWT Authentication                              |
|   - Role-Based Access Control (RBAC: ADMIN, OFFICER, GODOWN_MGR, SHOPKEEPER)     |
|   - Spring Data JPA / Hibernate ORM with Oracle Dialect                           |
|   - Declarative Transaction Management (@Transactional with ACID isolation)       |
|   - Global Exception Handling & Oracle Error Code Translation                     |
|   - Microservice Connector to AI Intelligence Engine (WebClient / REST)           |
+-----------------------------------------------------------------------------------+
                  |                                              |
     Oracle JDBC  |                                  HTTP REST   |
       (ojdbc11)  |                                              |
                  v                                              v
+-------------------------------------+        +------------------------------------+
|            DATABASE LAYER           |        |         AI SERVICE LAYER           |
|  Oracle Database (19c/21c/23c)      |        |  Python 3.11 FastAPI Microservice  |
|  - 22 Normalized Relational Tables  |        |  - Anomaly Detection (IsoForest)   |
|  - Sequences, Indexes, Constraints  |        |  - Demand Forecasting (Timeseries) |
|  - PL/SQL Packages & Procedures     |        |  - Multilingual NLP (En / Ta)      |
|  - `monthly_entitlement` Function   |        |  - Guarded Officer Assistant (NLP) |
|  - Custom Exception:                |        +------------------------------------+
|    `e_entitlement_exhausted`        |
|  - Concurrency Locking:             |
|    `SELECT FOR UPDATE`              |
|  - Immutable Movement & Audit Logs  |
+-------------------------------------+
```

---

### 2. Layer-by-Layer Responsibilities

#### A. Database Layer (Oracle Database)
- **Authoritative Single Source of Truth:** Inventory quantities and remaining entitlement balances are exclusively calculated and validated in the database.
- **ACID Transaction Isolation:** Every sale transaction acquires row-level locks (`SELECT FOR UPDATE`) on the respective `SHOP_STOCK` record to prevent concurrent overselling across POS counters.
- **PL/SQL Stored Logic:** 
  - `monthly_entitlement` function executes policy rules dynamically.
  - `pkg_pds_allotment` computes taluk shop quotas and validates against godown availability.
  - `pkg_pds_sales` executes atomic distribution: deducts cardholder balance, deducts shop stock, logs `STOCK_MOVEMENTS`, records `SALES` and `SALE_ITEMS`.
- **Auditability:** DB Triggers capture state changes into `AUDIT_LOGS` with timestamps, modifying user, old values, and new values.

#### B. Application Layer (Spring Boot)
- **Stateless REST Services:** Exposes clean JSON endpoints under `/api/v1/*`.
- **Security & Authorization:** Enforces JWT token verification and route-level authorization via Spring Security filter chains (`hasRole('OFFICER')`, `hasRole('SHOPKEEPER')`, etc.).
- **Data Access:** Spring Data JPA entities mapped to Oracle tables using Oracle naming strategies, with native procedure calls for complex transactional procedures.
- **Exception Normalization:** Translates Oracle-specific error codes (ORA-20001: Entitlement exhausted, ORA-20002: Insufficient shop stock, ORA-00001: Unique constraint violation) into human-readable, actionable HTTP 400/409/422 responses.
- **AI Gateway Client:** Bridges secure officer requests to the Python AI service while validating parameters and stripping sensitive PII.

#### C. Presentation Layer (React + TypeScript + Vite)
- **Role-Centric Workspaces:**
  - *Shopkeeper View:* Quick POS sales terminal with ration card lookup, remaining balance display, commodity basket, and instant bill generation.
  - *Officer View:* Taluk overview dashboard, allotment approvals, stock reconciliation, anomaly radar, and NLP assistant.
  - *Godown Manager View:* Central stock register, pending shop indents, dispatch order generation.
  - *Grievance Officer View:* Complaint triage board with AI category recommendations and priority tags.
- **Resilient State & UX:** Real-time field validation, offline indicator warnings, loading states, empty state views, confirmation modals for irreversible stock dispatches, and exportable reports (CSV / Print).

#### D. AI Intelligence Service (Python FastAPI)
- **Asynchronous Microservice:** Runs independently on port 8000, exposing `/api/v1/ai/*`.
- **Stateless Prediction Engines:**
  - `anomaly_detector.py`: Scans recent sales vs. stock movement patterns, identifying outlier variance.
  - `demand_forecaster.py`: Calculates moving average and seasonality to project next month's shop commodity indents and estimated days of stock remaining.
  - `complaint_classifier.py`: Preprocesses Tamil and English grievance text, extracting intent, sentiment, and urgency score.
  - `officer_assistant.py`: Natural language query parser restricted to read-only summarized metrics (no arbitrary SQL generation).
- **Strict Guardrail Principle:** AI outputs are advisory flags for human officer review; AI is strictly barred from modifying inventory, entitlements, or disciplinary statuses.

---

### 3. Data Flow & Transaction Boundaries

#### Scenario 1: Monthly Entitlement Generation
1. Scheduled batch or Officer triggers `POST /api/v1/entitlements/generate?year=2026&month=10`.
2. Spring Boot service invokes Oracle PL/SQL batch procedure `pkg_pds_allotment.generate_monthly_entitlements`.
3. For each active card in `CARD_HOLDERS`, the procedure evaluates active rules in `ENTITLEMENT_RULES` through `monthly_entitlement()`.
4. Records are inserted into `ENTITLEMENTS`. Duplicate generation for the same card, commodity, and month is strictly rejected via composite unique index `(card_id, commodity_id, allotment_month, allotment_year)`.

#### Scenario 2: Transaction-Safe Ration Distribution (e-POS Sale)
1. Beneficiary presents Ration Card (`RC-TN-04-100234`) at Shop `#42`.
2. Shopkeeper inputs card number into POS UI. Frontend fetches card details, active members, and remaining entitlement balance (`GET /api/v1/entitlements/card/{id}/remaining`).
3. Shopkeeper selects commodities: Rice (20 kg), Sugar (2 kg).
4. Submits sale: `POST /api/v1/sales`.
5. Spring Boot begins an atomic `@Transactional` boundary and calls `pkg_pds_sales.process_sale(...)`.
6. PL/SQL procedure executes:
   - Locks `SHOP_STOCK` rows for Shop `#42` and commodities using `SELECT ... FOR UPDATE`.
   - Computes already distributed quantity for current month.
   - If `(already_distributed + requested_qty) > monthly_entitlement`, raises `e_entitlement_exhausted` (ORA-20001) and aborts.
   - If `requested_qty > current_shop_stock`, raises `e_insufficient_stock` (ORA-20002) and aborts.
   - Inserts header into `SALES` and line items into `SALE_ITEMS`.
   - Updates `SHOP_STOCK` current quantity.
   - Appends records to immutable `STOCK_MOVEMENTS` (`MOVEMENT_TYPE = 'SALE'`).
   - Commits transaction.
7. Backend returns HTTP 201 with generated Bill Receipt No.
