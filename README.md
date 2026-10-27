# Public Distribution System (PDS) – Ration Shop Stock & Entitlement System
### Enterprise Taluk-Scale Civil Supplies Management & AI-Assisted Stock Intelligence Platform

---

## 1. Project Overview & Operational Scope

The **Public Distribution System (PDS) – Ration Shop Stock & Entitlement System** is a mission-critical, enterprise-grade government welfare administration platform designed for the **Department of Civil Supplies and Consumer Protection**. 

This system was architected and built to solve critical operational bottlenecks within public food grain logistics:
- **Stock Discrepancies & Leakage:** Elimination of ghost stock and unauthorized diversions through immutable stock movement ledgers and weighment scale audits.
- **Incorrect Entitlements:** Automated multi-rule quota calculation engine based on welfare card category (`AAY`, `PHH`, `NPHH`, `AY`), household size, and statutory caps.
- **Supply Chain Delays:** Controlled monthly requirement allotment, godown dispatch tracking, and shop receipt confirmation workflows.
- **Concurrency & Overselling:** High-concurrency row locking (`SELECT FOR UPDATE`) ensuring zero overselling beyond shop stock or cardholder entitlement limits.
- **Grievance Triage:** Multilingual citizen complaint analysis supporting Tamil and English with AI urgency classification.
- **AI-Assisted Oversight:** Read-only statistical anomaly detection, 30-day demand run-rate forecasting, and sandboxed officer decision support.

### Operational Scale
- **Taluk Jurisdiction:** 1 Taluk Administrative Zone (Tambaram Taluk, Chengalpattu District, Tamil Nadu).
- **Fair-Price Shops (FPS):** **120 Registered Fair-Price Outlets**.
- **Beneficiary Coverage:** **~1,00,000 Active Ration Cardholders** (~3.5 lakh family members).
- **Supply Chain Topology:** 2 Central Buffer Godowns (Depots) supplying 120 FPS via registered truck consignments.

---

## 2. Technology Stack Architecture

```
                       [ BENEFICIARY / CITIZEN / FIELD INSPECTOR ]
                                            |
                                            v
  +-----------------------------------------------------------------------------------+
  |               PRESENTATION TIER: REACT 18 + TYPESCRIPT + VITE                     |
  |  - Responsive Navy/White/Teal Civic Design System (#0B2545, #007A78)              |
  |  - 21 Purpose-Built Portal Views (e-POS Terminal, Allotment, AI Radar, Reports)    |
  |  - Recharts Visual Analytics (Off-Take Bar Charts, Cardholder Pie, KPI Cards)     |
  +-----------------------------------------------------------------------------------+
                                            |
                                            v REST / JSON (Stateless JWT)
  +-----------------------------------------------------------------------------------+
  |               BACKEND API TIER: JAVA 17 + SPRING BOOT 3.2.5                       |
  |  - Spring Web REST Controllers with Bean Validation (JSR-380)                     |
  |  - Spring Security with Stateless JWT Auth & Role-Based Access Control (RBAC)    |
  |  - Spring Data JPA with Pessimistic Row Locking (@Lock(PESSIMISTIC_WRITE))        |
  |  - Global Exception Handling translating Oracle SQL Codes into standard HTTP      |
  |  - Resilient AI Gateway Service with Embedded Analytical Fallback Engine          |
  +-----------------------------------------------------------------------------------+
                       |                                            |
                       v Oracle JDBC Driver (ojdbc11)               v HTTP REST / JSON
  +---------------------------------------+   +---------------------------------------+
  |    DATABASE TIER: ORACLE DATABASE     |   |   AI SERVICE: PYTHON 3.11 + FASTAPI   |
  |  (19c / 21c / 23c / XE / Free ATP)    |   |  - Anomaly Detector (Isolation Forest)|
  |  - 24 Normalized Business Tables      |   |  - Demand Forecaster (Moving Average) |
  |  - PL/SQL Packages (pkg_pds_sales)    |   |  - Bilingual Grievance NLP (Ta/En)    |
  |  - Custom Exceptions (ORA-20001/02)   |   |  - Sandboxed Officer Assistant (Q&A)  |
  |  - Sequences, Indexes, Triggers       |   +---------------------------------------+
  +---------------------------------------+
```

| Component | Technology | Version | Purpose |
| :--- | :--- | :--- | :--- |
| **Database** | Oracle Database (SQL & PL/SQL) | 19c / 21c / 23c | Authoritative Single Source of Truth, ACID transactions, row locking |
| **Backend** | Java / Spring Boot / Maven | Java 17, Boot 3.2.5 | Enterprise REST API, JWT auth, business validation, JPA persistence |
| **Frontend** | React / TypeScript / Vite | React 18, Vite 5.2 | Civic portal UI, e-POS sales terminal, 21 functional operational pages |
| **AI Engine** | Python / FastAPI / Scikit-Learn | Python 3.11, FastAPI 0.111 | Stock anomaly radar, demand forecasting, bilingual NLP triage |
| **Testing** | JUnit 5, Mockito, Python unittest | Standard | Automated validation of quota calculations, concurrency, and models |

---

## 3. Software Prerequisites

Before running the application, ensure the following developer environments are installed:

1. **Java Development Kit (JDK):** Version 17 or higher (Eclipse Temurin / OpenJDK 17).
2. **Apache Maven:** Version 3.8.0 or higher.
3. **Node.js & npm:** Node.js 18.0.0 or higher (v20+ recommended) and npm 9+.
4. **Python:** Version 3.10 or 3.11 with `pip`.
5. **Oracle Database Client / Database:**
   - Oracle Database 19c, 21c XE, 23c Free, or Oracle Cloud ATP instance.
   - SQL*Plus or Oracle SQL Developer for executing compilation scripts.
   - *Note:* If Oracle Database is not installed locally, Spring Boot includes an automated mock test harness (`application-test.properties` using H2 in Oracle syntax mode for automated test suites).

---

## 4. Oracle Database Setup & Script Execution Order

All database scripts are located in the `database/` directory. They must be executed sequentially using SQL*Plus, SQL Developer, or SQLcl connected as a privileged DBA user (e.g., `SYSTEM` or `SYS AS SYSDBA`).

### Step 1: Create Schema and Objects
Connect to your Oracle PDB (e.g., `XEPDB1` or `FREEPDB1`):
```sql
sqlplus system/YourPassword@localhost:1521/XEPDB1
```

Execute the 11 scripts in the strict numbered order:

```sql
-- 1. Session environment settings and drop obsolete views
@database/01_create_schema_objects.sql

-- 2. Create the 24 relational business tables
@database/02_create_tables.sql

-- 3. Apply Primary Keys, Foreign Keys, Unique & Check constraints, and B-Tree indexes
@database/03_create_constraints_indexes.sql

-- 4. Create database sequences for all primary keys
@database/04_create_sequences.sql

-- 5. Compile stored functions (monthly_entitlement, fn_get_remaining_entitlement, fn_calculate_expected_stock)
@database/05_create_functions.sql

-- 6. Compile stored procedures (allotments, approvals, shop receipts, stock audits)
@database/06_create_procedures.sql

-- 7. Compile PL/SQL packages (pkg_pds_sales with row locks and custom exceptions)
@database/07_create_packages.sql

-- 8. Compile triggers (family member count sync, grievance history, audit trails)
@database/08_create_triggers.sql

-- 9. Seed representative operational dataset (120 shops, cardholders, inventory, dispatches)
@database/09_sample_data.sql

-- 10. Run analytical management queries (sales matrix, 3-month non-drawal report)
@database/10_test_queries.sql

-- 11. Execute automated PL/SQL unit test harness
@database/11_test_plsql.sql
```

### Key PL/SQL Objects Summary
- **`monthly_entitlement` Function:** Calculates card quota based on category and family size. Supports `FIXED_PER_CARD`, `PER_MEMBER_WITH_MIN`, and `PER_MEMBER_SCALED`.
- **`pkg_pds_sales` Package:** Encapsulates atomic e-POS sales transactions. Uses `SELECT current_quantity FROM SHOP_STOCK ... FOR UPDATE`, raises `e_entitlement_exhausted` (ORA-20001) or `e_insufficient_stock` (ORA-20002) if limits are breached, and performs atomic rollbacks.

---

## 5. Configuration via Environment Variables

Copy the provided root template `.env.example` to `.env`:

```bash
cp .env.example .env
```

Ensure the connection details correspond to your local services:

| Variable | Default Value | Description |
| :--- | :--- | :--- |
| `ORACLE_DB_HOST` | `localhost` | Oracle Database server host |
| `ORACLE_DB_PORT` | `1521` | Oracle Net Listener port |
| `ORACLE_DB_SERVICE_NAME` | `XEPDB1` | Pluggable Database (PDB) service name |
| `ORACLE_DB_USERNAME` | `C##PDS_ADMIN` | Dedicated application schema user |
| `ORACLE_DB_PASSWORD` | `YourPassword` | Application schema password |
| `SERVER_PORT` | `8080` | Spring Boot HTTP listening port |
| `JWT_SECRET` | `404E6352...` | 256-bit HMAC secret for token encryption |
| `AI_SERVICE_URL` | `http://localhost:8000/api/v1/ai` | Python FastAPI endpoint bridge |
| `VITE_PORT` | `5173` | React frontend development port |

---

## 6. How to Start the Services

Open three separate terminal windows to launch the full platform.

### Terminal 1: Python AI Microservice
```bash
cd ai-service

# Create virtual environment (recommended)
python -m venv venv
venv\Scripts\activate       # On Windows
# source venv/bin/activate  # On Linux/macOS

# Install dependencies
pip install -r requirements.txt

# Start FastAPI server
uvicorn app.main:app --host 0.0.0.0 --port 8000 --reload
```
*Verification:* Navigate to `http://localhost:8000/docs` to view the interactive Swagger/OpenAPI documentation.

### Terminal 2: Java Spring Boot Backend
```bash
cd backend

# Compile and run automated test suite
mvn test

# Start the Spring Boot application
mvn spring-boot:run
```
*Verification:* Spring Boot will start on `http://localhost:8080/api/v1`. Test health via `http://localhost:8080/api/v1/shops`.

### Terminal 3: React Frontend Portal
```bash
cd frontend

# Install packages
npm install

# Build production assets (validates TypeScript types)
npm run build

# Start local development server
npm run dev
```
*Verification:* Access the portal at `http://localhost:5173`.

---

## 7. Automated Testing Suite

The repository includes comprehensive automated tests covering all critical business rules:

### A. Spring Boot Backend Tests
Execute from `backend/`:
```bash
mvn test
```
**Test Coverage:**
1. `EntitlementCalculationTest`:
   - `testAayEntitlementCalculation`: Verifies flat 35 kg rice quota for Antyodaya households.
   - `testPhhScaledEntitlementCalculation`: Verifies 5 kg/member scaling with statutory minimums.
   - `testMissingRuleThrowsException`: Ensures unconfigured commodities throw `ResourceNotFoundException`.
2. `SalesSecurityAndValidationTest`:
   - `testExhaustedEntitlementThrowsException`: Validates ORA-20001 translation into HTTP 409 Conflict.
   - `testInsufficientStockThrowsException`: Validates ORA-20002 translation when shop counter lacks physical stock.
   - `testSuccessfulAtomicSale`: Validates quota decrement, shop stock reduction, and receipt creation.

### B. Python AI Microservice Tests
Execute from `ai-service/`:
```bash
python -m unittest tests/test_ai_unittest.py
```
**Test Coverage:**
- Isolation Forest anomaly detection scoring on inventory variances.
- Moving-average demand forecasting and days-to-stockout horizon categorization.
- Multilingual Tamil & English grievance category and urgency triage.
- Sandboxed read-only officer assistant question matching.

### C. Oracle PL/SQL Test Harness
Execute inside SQL*Plus / SQL Developer:
```sql
@database/11_test_plsql.sql
```
Verifies package procedure execution, custom exception trapping, and transaction rollback integrity directly within the Oracle engine.

---

## 8. Interactive Application Walkthrough

The React frontend includes pre-configured demo user sessions for rapid role evaluation:

### 1. Taluk Civil Supplies Officer (`officer_pds`)
- **Main Dashboard:** Live KPI cards for active cards (1 Lakh), low-stock shops, pending allotments, and open complaints.
- **Shop Allotments:** Review monthly requirements, approve pending quotas, and inspect depot stock allocations.
- **Stock Anomaly Radar:** Inspect Isolation Forest flags highlighting physical weighment variances across shops.
- **Officer Assistant Q&A:** Natural-language query interface for immediate shortage summaries and grievance status.
- **Analytical Reports:** View and export shop-wise monthly commodity sales matrix and 3-month non-drawal registers.

### 2. Fair-Price Shopkeeper (`shop_001` - FPS #101)
- **POS Distribution Terminal:** Real-time counter sales checkout.
  1. Enter card number (e.g., `TN-04-AAY-10001` or `TN-04-PHH-20001`).
  2. Terminal displays remaining permitted quota for the current month.
  3. Select dispensing quantities (prevented from exceeding available quota or shop stock).
  4. Select authentication mode (`Aadhaar Mobile OTP`, `Biometric Fingerprint`, `Senior Iris`).
  5. Click **Authorize & Print Bill** to record atomic sale and print government e-POS receipt.
- **Dispatches & Inward:** Confirm arrival of truck consignments from Central Godown, updating shop inventory.
- **Stock Reconciliation:** Conduct daily weighment counts and log physical scale readings.

### 3. Godown Manager (`godown_01` - Central Depot)
- **Godowns & Warehouses:** Monitor bulk metric ton (MT) grain reserves.
- **Consignment Dispatches:** Create new truck waybills assigned to transport grain to designated fair-price shops.

---

## 9. Oracle Ecosystem Integration Architecture

Per Section 15 of the specification, the database schema and views are designed to support native enterprise Oracle platforms. Complete technical specifications are detailed in [`docs/ORACLE_ECOSYSTEM_INTEGRATION.md`](file:///d:/PDS-Ration-Intelligence/docs/ORACLE_ECOSYSTEM_INTEGRATION.md):

1. **Oracle Forms 12c:**
   - Blocks: `BLK_SALES_HEADER` and `BLK_SALES_ITEMS`.
   - LOV: `LOV_CARD_NUMBERS` mapped to `CARD_HOLDERS` with real-time remaining quota display.
   - Error Handling: `ON-ERROR` trigger traps ORA-20001 (`e_entitlement_exhausted`) and ORA-20002 (`e_insufficient_stock`).
2. **Oracle Reports 12c / BI Publisher:**
   - Multi-commodity crosstab matrix query: `V_MONTHLY_COMMODITY_MATRIX`.
   - Godown stock register report: `V_GODOWN_STOCK_REGISTER`.
3. **Oracle E-Business Suite (EBS) & Fusion SCM:**
   - Staging table `EBS_PDS_INTF_DISPATCHES` for automated inter-organization inventory transfers (`MTL_TRANSACTIONS_INTERFACE`).
4. **Oracle Transactional Business Intelligence (OTBI):**
   - Pre-built Subject Area definitions for leak detection, off-take velocity, and grievance resolution times.

---

## 10. Repository Directory Structure

```
PDS-Ration-Intelligence/
|-- frontend/                       # React 18 + TypeScript + Vite UI Portal
|   |-- src/
|   |   |-- components/             # Navbar, Sidebar, Reusable UI widgets
|   |   |-- pages/                  # 21 Required Civil Supplies Portal Pages
|   |   |-- services/api.ts         # Axios/Fetch REST client with fallback support
|   |   |-- types/index.ts          # TypeScript domain interfaces & DTOs
|   |   |-- App.tsx                 # Main application shell with tab router
|   |   |-- main.tsx                # React DOM entry point
|   |   |-- index.css               # Navy/Teal civic design system
|   |-- package.json
|   |-- vite.config.ts
|
|-- backend/                        # Java 17 + Spring Boot 3.2.5 REST Service
|   |-- src/main/java/com/wipro/pds/
|   |   |-- controller/             # 13 REST Controllers
|   |   |-- service/                # Business logic, POS checkout, AI gateway
|   |   |-- repository/             # Spring Data JPA repositories with row locks
|   |   |-- entity/                 # 22 JPA entity classes
|   |   |-- security/               # JWT token provider & Spring Security config
|   |   |-- exception/              # GlobalExceptionHandler (Oracle error translation)
|   |-- src/test/java/              # Automated JUnit 5 unit & integration tests
|   |-- pom.xml                     # Maven build configuration with Oracle ojdbc11
|
|-- database/                       # Pure Oracle Database (SQL & PL/SQL)
|   |-- 01_create_schema_objects.sql
|   |-- 02_create_tables.sql
|   |-- 03_create_constraints_indexes.sql
|   |-- 04_create_sequences.sql
|   |-- 05_create_functions.sql
|   |-- 06_create_procedures.sql
|   |-- 07_create_packages.sql
|   |-- 08_create_triggers.sql
|   |-- 09_sample_data.sql
|   |-- 10_test_queries.sql
|   |-- 11_test_plsql.sql
|   |-- README.md
|
|-- ai-service/                     # Python 3.11 + FastAPI Machine Learning Engine
|   |-- app/
|   |   |-- anomaly_detector.py     # Isolation Forest & Z-Score anomaly engine
|   |   |-- demand_forecaster.py    # Run-rate forecasting & shortage alert engine
|   |   |-- complaint_classifier.py # Bilingual Tamil/English NLP grievance triage
|   |   |-- officer_assistant.py    # Sandboxed read-only Q&A parser
|   |   |-- main.py                 # FastAPI server & route handlers
|   |-- tests/                      # Python automated test suite
|   |-- requirements.txt
|
|-- docs/                           # Comprehensive Engineering Documentation
|   |-- PROJECT_OVERVIEW.md         # Operational background & welfare beneficiaries
|   |-- ARCHITECTURE.md             # Multi-tier system architecture diagrams
|   |-- DATABASE_DESIGN.md          # Complete data dictionary for 24 tables
|   |-- WORKFLOW.md                 # 6 End-to-end operational workflows
|   |-- API_DOCUMENTATION.md        # REST API endpoints & payload specifications
|   |-- TESTING.md                  # Test traceability matrix (24 test scenarios)
|   |-- USER_GUIDE.md               # Operating manual for Tahsildars & Dealers
|   |-- ORACLE_ECOSYSTEM_INTEGRATION.md # Forms, Reports, EBS, and OTBI specifications
|
|-- PROJECT_REVIEW.md               # Initial workspace inspection & reconciliation report
|-- .env.example                    # Clean environment configuration template
|-- .gitignore                      # Multi-technology git exclusions
|-- README.md                       # Master project documentation (this file)
```

---

## 11. Known Limitations & Future Roadmap

### Current Version (v1.0.0)
- **Local Database Scope:** Sample data contains representative records for 120 shops and core beneficiary categories (`AAY`, `PHH`, `NPHH`, `AY`). Production ingestion of 1,00,000 live citizen biometric records requires integration with State UIDAI Aadhaar Vault servers.
- **AI Sandboxing:** The AI microservice operates in strict read-only advisory mode to ensure compliance with civil supplies regulations; it cannot mutate inventory or accuse individuals of criminal wrongdoing.

### Planned Enhancements (v2.0.0)
1. **IoT Weighing Scale Integration:** Direct Bluetooth/RS-232 telemetry from digital weighing scales to the POS terminal to prevent manual weight entry tampering.
2. **GPS Truck Geofencing:** Integration with vehicle tracking sensors to confirm godown-to-shop truck transit routes before authorizing shop inward receipt.
3. **Voice AI in Tamil:** Speech-to-text grievance lodge hotline allowing rural beneficiaries to register verbal complaints over phone calls.

---

## 12. Author & Project Metadata
- **Project Name:** Public Distribution System – Ration Shop Stock & Entitlement System
- **Developed for:** Wipro College Project Submission
- **Domain:** Government Digital Services / Civil Supplies & Logistics
- **Target OS:** Cross-Platform (Windows / Linux / Oracle Enterprise Linux)
