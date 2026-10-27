# PDS-Ration-Intelligence: Comprehensive Testing Framework
## Verification, Test Suites & Quality Assurance Specification

### 1. Test Architecture & Scope
Testing is executed across four distinct tiers:
1. **Database Tier (Oracle PL/SQL):** Validates constraint enforcement, unique indexes, stored function math, transaction rollbacks, row-locking concurrency, and custom exceptions (`e_entitlement_exhausted`, `e_insufficient_stock`).
2. **Backend Service Tier (Spring Boot / JUnit 5 / Mockito):** Unit and integration testing of REST endpoints, security filter chains, transactional semantics (`@Transactional`), and exception mappers.
3. **AI Service Tier (Python / Pytest):** Validates anomaly detection algorithms, demand forecast boundaries, multilingual NLP classification, and officer assistant authorization sandboxes.
4. **End-to-End & Integration Tier:** Validates full multi-tier flows from frontend user interactions through backend services and database commits.

---

### 2. Traceability Matrix: 24 Core Test Scenarios

| Test ID | Test Scenario | Execution Target | Expected Result | Pass/Fail Criteria |
|---|---|---|---|---|
| **TC-01** | Ration Card Registration | Backend / DB | Card created with unique card number, head of family, shop linkage. | HTTP 201; row created in `CARD_HOLDERS`. |
| **TC-02** | Family Member Registration | Backend / DB | Member attached to card; `total_family_members` updated. | Foreign key verified; count incremented. |
| **TC-03** | Entitlement Calculation | PL/SQL `monthly_entitlement` | Correct quota returned based on card type rule and family size. | Exact math matches policy formula. |
| **TC-04** | Missing Entitlement Rules | PL/SQL | Procedure handles missing commodity rules without crashing. | Returns 0.00 or raises controlled exception. |
| **TC-05** | Invalid Entitlement Inputs | PL/SQL / Backend | Negative family size or null card type rejected. | Error code returned; validation blocks insert. |
| **TC-06** | Duplicate Monthly Entitlement Prevention | DB Unique Index | Preventing second entitlement generation for same card & month. | ORA-00001 unique constraint caught gracefully. |
| **TC-07** | Shop Allotment Generation | PL/SQL `pkg_pds_allotment` | Aggregates all active card quotas for a shop into draft allotment. | Correct sum of beneficiary quotas created. |
| **TC-08** | Duplicate Allotment Prevention | DB Unique Index | Recalculation updates or skips rather than creating duplicate rows. | Unique constraint on `(shop, commodity, YYYY, MM)`. |
| **TC-09** | Godown Dispatch | Backend / DB | Godown stock decreased; dispatch status set to `IN_TRANSIT`. | Stock movement recorded (`GODOWN_DISPATCH`). |
| **TC-10** | Shop Receipt Confirmation | Backend / DB | Shop stock incremented; dispatch status updated to `RECEIVED`. | Stock movement recorded (`SHOP_RECEIPT`). |
| **TC-11** | Successful Ration Sale | PL/SQL `pkg_pds_sales` | Bill created; shop stock decremented; remaining quota reduced. | Atomic commit; stock movement (`SALE`) recorded. |
| **TC-12** | Exhausted Entitlement Prevention | PL/SQL Custom Exception | Cardholder attempting to draw beyond monthly limit is blocked. | `e_entitlement_exhausted` raised; HTTP 409 returned. |
| **TC-13** | Insufficient Shop Stock Prevention | PL/SQL Custom Exception | Sale requested when shop inventory is below requested quantity. | `e_insufficient_stock` raised; sale aborted. |
| **TC-14** | Concurrent Sales Protection | PL/SQL Row-Locking | Concurrent transactions on same shop stock are serialized. | `SELECT FOR UPDATE` prevents race condition overselling. |
| **TC-15** | Transaction Rollback Integrity | DB Transaction | Any failure during sale rolls back header, items, and movements. | No partial records left in any table. |
| **TC-16** | Stock Reconciliation | DB / Backend | System calculated stock compared with physical count; variance logged. | Variance = Count - Expected; discrepancy % calculated. |
| **TC-17** | Complaint Registration | Backend / DB | Grievance created with unique reference and initial status `REGISTERED`. | Entry inserted into `COMPLAINTS`. |
| **TC-18** | Complaint History Tracking | DB Trigger / Procedure | Status change creates immutable audit trail in `COMPLAINT_HISTORY`. | History table records previous & new status + user. |
| **TC-19** | Role-Based Authorization | Spring Security | Shopkeeper cannot approve allotments or modify rules. | HTTP 403 Forbidden on unauthorized endpoints. |
| **TC-20** | Report Accuracy & Date Boundaries | Oracle SQL | 3-month non-drawal query strictly excludes incomplete current month. | Exact 90-day/3-completed-month boundary verified. |
| **TC-21** | AI Anomaly Detection | Python FastAPI | Identifies shops with high variance or rapid unexplained turnover. | Anomaly score computed with explainable reason. |
| **TC-22** | AI Demand Forecasting | Python FastAPI | Calculates projected 30-day commodity requirement with stockout alert. | Forecast returned with buffer threshold check. |
| **TC-23** | Complaint Classification | Python NLP | Classifies Tamil/English grievance into correct category & priority. | Accurate classification tag and confidence score. |
| **TC-24** | Officer Assistant Sandboxing | Python FastAPI | Answers operational questions; refuses arbitrary SQL or write calls. | Read-only access enforced; no unauthorized mutation. |

---

### 3. Execution Commands & Test Run Instructions
- **Oracle PL/SQL Verification:**
  Run `10_test_queries.sql` and `11_test_plsql.sql` in SQL*Plus / SQL Developer.
- **Spring Boot Backend Unit & Integration Tests:**
  `mvn clean test`
- **Python AI Microservice Tests:**
  `pytest tests/`
