# Public Distribution System (PDS) – Ration Shop Stock & Entitlement System
## Project Overview & Executive Summary

### 1. Executive Summary
The **Public Distribution System (PDS) – Ration Shop Stock & Entitlement System** is an enterprise-grade digital governance platform built for state civil supplies departments and fair-price shop (FPS) networks. Designed to operate at the scale of a taluk supplying **120 fair-price shops** serving approximately **1,00,000 ration cardholders**, the system brings complete transparency, real-time inventory visibility, mathematical quota enforcement, and artificial intelligence capabilities to the public distribution ecosystem.

The solution directly addresses historical vulnerabilities in public distribution networks:
- **Leakage & Stock Discrepancies:** By introducing end-to-end stock movement auditing from regional godowns down to shop-level distribution.
- **Entitlement Calculation Errors:** Eliminating manual calculation errors via a centralized, rule-driven PL/SQL entitlement engine that calculates monthly family quotas based on card category and family size.
- **Shop-Level Overselling & Ghost Distribution:** Utilizing atomic transactional locking (`SELECT FOR UPDATE`), preventing cardholders from drawing beyond their authorized monthly quota or shops from dispensing non-existent stock.
- **Grievance Redressal Delays:** Providing a multi-tier complaint management system with audit history, automated classification, and resolution tracking.
- **Decision Support Shortcomings:** Integrating an AI-powered intelligence engine for predictive demand forecasting, anomaly detection, multilingual complaint triage, and officer query assistance.

---

### 2. Operational Scale & Key Metrics
- **Geographic Unit:** 1 Taluk Administrative Zone.
- **Fair-Price Shops (FPS):** 120 FPS locations (urban, semi-urban, and rural distribution points).
- **Ration Cards Served:** ~1,00,000 active ration cards.
- **Card Categories:** 
  - **AAY (Antyodaya Anna Yojana):** Poorest of the poor; fixed high-quota grains at highly subsidized/zero rates.
  - **PHH (Priority Household):** Per-member quota calculation ensuring nutritional security.
  - **NPHH (Non-Priority Household):** Standard subsidized food grains based on government availability.
  - **AY (Annapurna Yojana):** Senior citizen welfare allocations.
- **Key Commodities Handled:**
  - Common Rice (PDS Rice)
  - Raw / Boiled Rice
  - Whole Wheat
  - Fortified Atta
  - Refined Cane Sugar
  - Fortified Edible Palm Oil
  - Toor / Chana Dal (Pulses)
  - Subsidized Kerosene (Domestic Fuel)

---

### 3. Core Functional Pillars

```
+---------------------------------------------------------------------------------+
|                         CENTRAL PDS CORE PLATFORM                               |
+---------------------------------------------------------------------------------+
|                                                                                 |
|  [ 1. Card & Beneficiary ]    -->    [ 2. Entitlement Engine ]                  |
|  - Head of Family & Aadhaar          - Card Type & Family Size Logic            |
|  - Family Members & Age Status       - Rule-Driven PL/SQL Calculation           |
|                                                                                 |
|  [ 3. Supply Chain & Godowns ] -->    [ 4. Fair-Price Shop POS ]                |
|  - Central Godown Stock               - Real-Time Balance Verification          |
|  - Dispatches & Receipts              - Atomic Lock & Sales Ledger              |
|                                                                                 |
|  [ 5. Stock Reconciliation ]   -->    [ 6. AI Intelligence Engine ]             |
|  - Expected vs. Actual Counts         - Stock Anomaly Detection                 |
|  - Audit Logs & Discrepancies         - Demand Forecasting & NLP Triage         |
|                                                                                 |
+---------------------------------------------------------------------------------+
```

1. **Beneficiary & Card Lifecycle Management:**
   Digitizes ration cards, household members, active/inactive statuses, and biometric/Aadhaar references.
2. **Deterministic Entitlement Generation:**
   PL/SQL stored functions calculate permitted quotas per commodity and card monthly, preventing duplicate entitlements and preserving historical records when welfare policies evolve.
3. **Automated Shop Allotments & Godown Dispatch:**
   Aggregates active cardholder requirements per shop into formal monthly allotment orders. Tracks dispatches from taluk godowns and requires verified shop receipt confirmation before shop inventory increases.
4. **Transaction-Safe Ration Sales (e-POS):**
   Ensures cardholders cannot draw more than their monthly entitlement (`e_entitlement_exhausted` custom exception) and guarantees inventory is decremented atomically with an immutable stock movement audit entry.
5. **Multi-Stage Physical Stock Reconciliation:**
   Provides taluk supply officers with variance analysis comparing opening balances, receipts, distributed quantities, physical counts, and recorded stock to highlight discrepancies.
6. **Citizen Grievance Redressal:**
   Records, tracks, and escalates complaints across categories (quantity shortage, unavailable stock, delayed shop opening, card errors) with an immutable history log.
7. **AI Decision Support:**
   Provides civil supplies officers with explainable anomaly alerts, statistical demand forecasting, multilingual sentiment & classification (Tamil/English), and a read-only natural language assistant.

---

### 4. Stakeholder Roles & Access Control
- **State Administrative Officer (State Admin):** Global oversight, commodity policy configuration, system-wide reports.
- **Taluk Supply Officer (TSO / Officer):** Shop oversight, allotment approvals, reconciliation inspections, complaint escalation, AI investigation tools.
- **Godown In-Charge (Godown Manager):** Inward bulk receipts, warehouse stock management, outward dispatch generation.
- **Fair-Price Shop Operator (Shopkeeper):** Inward dispatch receipt confirmation, Point of Sale (POS) ration distribution, local stock view.
- **Auditor / Enforcement Inspector:** Read-only access to immutable audit logs, transaction histories, and discrepancy reports.
