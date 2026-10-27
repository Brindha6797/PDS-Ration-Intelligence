# PROJECT REVIEW & RECONCILIATION PLAN
**Project Title:** Public Distribution System – Ration Shop Stock & Entitlement System  
**Organization / Domain:** Wipro Project / Government Services  
**Target Architecture:** React (Vite + TypeScript) + Spring Boot (Java 17, JPA, Oracle JDBC) + Oracle Database (SQL & PL/SQL) + AI Intelligence Engine (Python FastAPI)  
**Inspection Date:** October 27, 2026  
**Status:** Comprehensive Workspace Baseline & Project Reconciliation  

---

## 1. Existing Project Inspection & Findings

A comprehensive inspection of the workspace (`d:\PDS-Ration-Intelligence`) was conducted using system exploration and PowerShell environment probes.

### Findings Summary:
1. **Workspace Directory Status:** The workspace root is clean and empty. No preexisting legacy files, corrupted branches, or conflicting code trees were found. 
2. **Prior Technologies & Legacy Artifacts:**
   - **MySQL Scripts:** None found in the current workspace. Any past references to MySQL or MariaDB syntax are formally decommissioned in accordance with the Master Specification.
   - **Oracle Scripts:** No legacy SQL files are present. A clean, strictly normalized Oracle 19c/21c/23c schema will be created from scratch following the exact required structure.
   - **Frontend Artifacts:** No conflicting React or Vite folders exist.
   - **Backend Artifacts:** No conflicting Maven or Gradle projects exist.
   - **AI Service Artifacts:** No conflicting Python virtual environments or Flask/FastAPI projects exist.
3. **Environment & Runtimes Detected:**
   - **Java:** OpenJDK 17.0.18 (Eclipse Adoptium) — fully compatible with Spring Boot 3.x and Oracle JDBC (`ojdbc11`).
   - **Build Tool:** Apache Maven 3.9.11 — available and configured.
   - **Node.js & npm:** Node.js v24.16.0, npm 11.13.0 — fully compatible with Vite, React 18/19, TypeScript, Recharts, Lucide React.
   - **Python:** Python 3.11.1 — compatible with FastAPI, Pandas, Scikit-learn, Uvicorn, and NLP packages.
   - **Operating System:** Windows 11 64-bit.

---

## 2. Technology & Architectural Reconciliation

| Domain | Legacy / Obsolete Approach (Discarded) | Standardized Target Architecture (Preserved & Implemented) | Rationale |
|---|---|---|---|
| **Database** | MySQL / MariaDB syntax (AUTO_INCREMENT, backticks, LIMIT) | Pure **Oracle SQL & PL/SQL** (Sequences, Triggers, Stored Procedures, Functions, Packages, Custom Exceptions `e_entitlement_exhausted`, `SELECT FOR UPDATE`) | Strict compliance with Wipro enterprise specs and Oracle ACID transactional guarantees. |
| **Backend** | Ad-hoc PHP/Node or standalone scripts | **Java 17 Spring Boot 3.x** (Spring Web, Spring Data JPA, Spring Security, Spring Validation, Oracle JDBC Driver `ojdbc11`) | Enterprise-grade reliability, secure RESTful APIs, declarative transaction management (`@Transactional`), and clean domain separation. |
| **Frontend** | Vanilla multi-page or plain HTML templates | **React 18 + TypeScript + Vite + React Router + Recharts + Lucide Icons + Professional Navy/Teal CSS Design System** | Modern, responsive, interactive government portal with real-time analytics and validation. |
| **AI Intelligence** | Static mock rules or hardcoded heuristics | **Python 3.11 FastAPI microservice** with Pandas, Scikit-learn, Isolation Forest / Statistical Z-score anomaly detection, Holt-Winters / Linear Trend demand forecasting, bilingual (English & Tamil) NLP classifier, and governed Read-Only Officer Assistant | Explainable, human-in-the-loop decision support with strict safety guardrails preventing automated alteration of official records. |
| **Forms / Reports / ERP** | Mock assumptions of live Oracle Forms/Reports | **Dedicated Architectural Bridge & Specifications** (`docs/ORACLE_ECOSYSTEM_INTEGRATION.md`) | Formal design showing how Oracle database objects interface with Oracle Forms, Oracle Reports, EBS/Fusion Inventory, and OTBI analytics without unverified live environment claims. |

---

## 3. Inventory of Core Components to Implement

### A. Database (Oracle 19c/21c/23c Compatible)
The required 22 relational entities and PL/SQL objects:
1. `CARD_TYPES` (AAY, PHH, NPHH, etc.)
2. `CARD_HOLDERS` (Family head, Aadhaar reference, address, taluk, shop linkage, status)
3. `FAMILY_MEMBERS` (Member details, relation, age, Aadhaar reference)
4. `SHOPS` (Fair-price shop details, license, taluk, contact, status)
5. `COMMODITIES` (Rice, Wheat, Sugar, Kerosene, Tur Dal, Edible Oil, etc. with units and rates)
6. `ENTITLEMENT_RULES` (Card type, commodity, monthly base quantity, per-member quantity, min/max limits, validity)
7. `ENTITLEMENTS` (Monthly ledger per cardholder and commodity)
8. `ALLOTMENTS` (Monthly quota per fair-price shop and commodity, draft/approved status)
9. `SHOP_STOCK` (Authoritative current inventory per shop and commodity)
10. `SALES` (Transaction header: shop, cardholder, sale date, bill no, status)
11. `SALE_ITEMS` (Transaction details: commodity, quantity, unit rate, total)
12. `GODOWNS` (Central storage warehouses with capacity and location)
13. `GODOWN_STOCK` (Inventory per godown and commodity)
14. `DISPATCHES` (Dispatches from godown to fair-price shops)
15. `DISPATCH_ITEMS` (Commodities and quantities dispatched)
16. `STOCK_MOVEMENTS` (Immutable audit ledger of all physical inflows and outflows)
17. `STOCK_RECONCILIATION` (Comparison between physical count, calculated expected stock, and recorded stock)
18. `COMPLAINTS` (Grievances registered by cardholders)
19. `COMPLAINT_HISTORY` (Status progression, officer assignment, resolution audit)
20. `USERS` (System users: Shopkeeper, Taluk Officer, Godown Manager, State Admin)
21. `ROLES` & `USER_ROLES` (Role-based access control)
22. `AUDIT_LOGS` (System-wide transaction and modification logging)

**PL/SQL Stored Routines & Packages:**
- `monthly_entitlement` function (computes quota using configurable rules)
- `pkg_pds_allotment` (calculates shop monthly requirements and validates godown allocation)
- `pkg_pds_sales` (transaction-safe sales processing with concurrency locking and `e_entitlement_exhausted` exception)
- `pkg_pds_reconciliation` (auditable discrepancy flagging and expected stock recalculation)
- Triggers for automatic audit logging and immutable movement tracking.

### B. Spring Boot Backend
- Maven `pom.xml` with Spring Boot 3.2+, Oracle JDBC (`com.oracle.database.jdbc:ojdbc11`), Spring Security, JWT authentication, Spring Data JPA, Hibernate with Oracle dialect.
- Layered architecture: Controllers -> Services -> Repositories -> Entities -> DTOs -> Mappers.
- Exception Handling: Global Controller Advice translating Oracle exceptions (`e_entitlement_exhausted`, unique constraints) into clear HTTP 4xx/5xx responses.
- Profiles: `application.properties` and `.env` support for flexible deployment.

### C. React Frontend
- Modern Vite + TypeScript application with a government-grade navy (`#0B2545`), teal (`#134074`, `#007A78`), slate, and clean white palette.
- 21 professional views (Dashboards, Cardholders, Family Members, Entitlements, Allotments, Godowns, Dispatches, Sales Terminal with POS-style verification, Stock Reconciliation, Grievance Redressal, AI Stock Alerts, Demand Forecasts, AI Complaint Classifier, Officer Assistant, Reports, RBAC, Audit Logs).
- Interactive Recharts for distribution analytics and discrepancy trends.

### D. AI Intelligence Service (FastAPI)
- Modular FastAPI application running on Python 3.11 with CORS and JWT header verification.
- **Module A: Stock Anomaly Detector:** Multi-factor analysis (Discrepancy ratio, rapid turnover, unexpected sales velocity) flagging high-risk shops with explainable reasons.
- **Module B: Demand Forecaster:** Time-series and seasonal requirement estimation per commodity and shop with stockout warning indicators.
- **Module C: Multilingual Complaint Classifier:** Tamil & English text classification into categories with priority scoring.
- **Module D: AI Officer Assistant:** Guarded read-only natural language query interface answering operational questions based on live backend data.

### E. Documentation & Enterprise Ecosystem Alignment
- Complete documentation suite in `docs/` (`PROJECT_OVERVIEW.md`, `ARCHITECTURE.md`, `DATABASE_DESIGN.md`, `WORKFLOW.md`, `API_DOCUMENTATION.md`, `TESTING.md`, `USER_GUIDE.md`).
- Explicit mapping for Oracle Forms, Oracle Reports, Oracle EBS/Fusion, and OTBI reporting.

---

## 4. Phase Execution Plan

| Phase | Description | Deliverables |
|---|---|---|
| **Phase 1** | Inspect & Reconcile Project | `PROJECT_REVIEW.md` (completed baseline) |
| **Phase 2** | Finalize Architecture & Design | Documentation suite in `docs/`, schema specifications |
| **Phase 3** | Database Implementation | `database/01` to `11` SQL scripts: tables, constraints, sequences, functions, procedures, packages, triggers, sample data, and verification tests |
| **Phase 4** | Card Management & Entitlement Calculation | PL/SQL `monthly_entitlement`, card lifecycle tests, rules engine |
| **Phase 5** | Allotments, Dispatches & Godown Inventory | PL/SQL allocation engine, dispatch workflows, stock verification |
| **Phase 6** | Ration Sales & Safe Concurrency | Sales package, `SELECT FOR UPDATE`, `e_entitlement_exhausted`, stock movements |
| **Phase 7** | Complaints, Audit Logs & Reporting | Grievance lifecycle, audit tables, report queries |
| **Phase 8** | Spring Boot REST Backend | Full Java 17 backend with JWT, controllers, services, repositories, JUnit tests |
| **Phase 9** | React Modern Frontend | Vite + TS frontend with 21 full pages, charts, clean responsive styling |
| **Phase 10** | AI Intelligence Engine | FastAPI microservice with 4 AI features and human-in-the-loop safety |
| **Phase 11** | Integration, Security & Automated Testing | End-to-end testing, validation scripts, security audits |
| **Phase 12** | Final Documentation & Presentation | Root README, `.env.example`, user manual, presentation slides structure |

---

## 5. Next Immediate Action
Proceed directly to **Phase 2: Architecture and Database Design Documentation**, followed by **Phase 3: Oracle Database SQL & PL/SQL Implementation** adhering strictly to Oracle 19c/21c/23c standards.
