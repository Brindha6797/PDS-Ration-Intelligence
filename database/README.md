# PDS Ration Shop Stock & Entitlement System
## Oracle Database Architecture, Schema DDL & PL/SQL Deployment Manual
### Target Environment: Oracle Database 19c / 21c / 23c (Enterprise, Express Edition XE, Free ATP)

---

## 1. Script Execution Order & Deployment Guide

To guarantee that relational constraints, foreign keys, sequences, stored functions, stored procedures, packages, and database triggers compile cleanly without dependency errors, execute the scripts in the **exact sequential order** below using `sqlplus`, `sqlcl`, or Oracle SQL Developer.

| Step | Script File | Purpose & Objects Compiled |
| :---: | :--- | :--- |
| **01** | `01_create_schema_objects.sql` | SQL*Plus environment parameters (`LINESIZE`, `SERVEROUTPUT`), optional DBA schema creation, and idempotent cleanup routine dropping existing views and tables (`CASCADE CONSTRAINTS PURGE`). |
| **02** | `02_create_tables.sql` | Creates all **24 normalized relational tables** using pure Oracle DDL (`VARCHAR2`, `NUMBER`, `TIMESTAMP`, `CLOB`). |
| **03** | `03_create_constraints_indexes.sql` | Applies **Primary Keys**, **Foreign Keys** (with `ON DELETE CASCADE` where applicable, including `fk_erp_movement`), **Unique Constraints** (`uq_card_aadhaar`, `uq_card_number`), **Check Constraints** (stock floors, commodity subsidy/buffer thresholds, active flags), and **B-Tree Performance Indexes** (`idx_entitlement_rules_lookup`). |
| **04** | `04_create_sequences.sql` | Creates Oracle sequences (`seq_*`) with safe starting offsets above sample data IDs to prevent `ORA-00001` PK collisions. |
| **05** | `05_create_functions.sql` | Compiles stored functions: `monthly_entitlement` (with ceiling enforcement across `PER_MEMBER` and `PER_MEMBER_WITH_CEILING`), `fn_get_remaining_entitlement`, and `fn_calculate_expected_stock`. |
| **06** | `06_create_procedures.sql` | Compiles stored procedures: `prc_generate_entitlements_for_month` (with year/month boundary checks), `prc_generate_shop_allotments` (with self-healing quota check and updated count metrics), `prc_approve_allotment`, `prc_confirm_shop_receipt`, and `prc_record_stock_reconciliation`. |
| **07** | `07_create_packages.sql` | Compiles PL/SQL packages: `pkg_pds_sales` (with row-level card locking, shop stock locking, deterministic commodity lock ordering to prevent `ORA-00060`, empty payload validation, and custom exceptions `e_entitlement_exhausted`, `e_insufficient_stock`) and `pkg_pds_allotment` (in-transit status tracking). |
| **08** | `08_create_triggers.sql` | Compiles database triggers: `trg_family_member_count_sync` (non-mutating delta update with cascade delete exception guard), `trg_complaint_status_history` (fires strictly on actual status transitions), and audit triggers on card/shop status updates. |
| **09** | `09_sample_data.sql` | Loads representative taluk demonstration dataset: 120 shops, commodities, godowns, cardholders across `AAY`, `PHH`, `NPHH`, `AY`, dispatches, and sales. |
| **10** | `10_test_queries.sql` | Executes 12 statutory management reports (including fractional-second strict 3-month non-drawal) and compiles 5 Oracle Reports & OTBI integration views (`v_rep_shop_monthly_matrix`, `v_rep_godown_stock_register`, `v_otbi_leakage_indicators`, `v_otbi_non_drawal_3months`, `v_otbi_fps_performance`). |
| **11** | `11_test_plsql.sql` | Runs automated PL/SQL test harness validating entitlement rules, exception trapping (`e_entitlement_exhausted`, `e_insufficient_stock`, `e_empty_payload`), floor/ceiling enforcement, and transaction rollbacks. |

---

## 2. Command Line Execution (SQL*Plus / SQLcl)

```bash
# Connect to the Oracle Pluggable Database (PDB)
sqlplus c##pds_admin/PdsAdmin2026#Secure@localhost:1521/XEPDB1

# Run the 11 scripts in exact sequence:
SQL> @01_create_schema_objects.sql
SQL> @02_create_tables.sql
SQL> @03_create_constraints_indexes.sql
SQL> @04_create_sequences.sql
SQL> @05_create_functions.sql
SQL> @06_create_procedures.sql
SQL> @07_create_packages.sql
SQL> @08_create_triggers.sql
SQL> @09_sample_data.sql
SQL> @10_test_queries.sql
SQL> @11_test_plsql.sql
```

---

## 3. Database Architecture & Integrity Guarantees

### A. 24 Normalized Relational Tables
1. `CARD_TYPES`: Welfare card categories (`AAY`, `PHH`, `NPHH`, `AY`).
2. `COMMODITIES`: Subsidized grain and provisions catalog (`RICE_COMMON`, `WHEAT`, `SUGAR`, `PALM_OIL`, `TOOR_DAL`, `KEROSENE`).
3. `GODOWNS`: Taluk central and sub-depots.
4. `GODOWN_STOCK`: Bulk metric ton inventory per godown and commodity.
5. `SHOPS`: 120 Fair-Price Shops (FPS) across the taluk.
6. `CARD_HOLDERS`: Beneficiary master registry with SHA-256 Aadhaar hash and masked last-4 digits.
7. `FAMILY_MEMBERS`: Individual household members linked to ration cards.
8. `ENTITLEMENT_RULES`: Configurable monthly quota calculation rules.
9. `ENTITLEMENTS`: Materialized monthly commodity quota ledger per cardholder.
10. `ALLOTMENTS`: Fair-price shop commodity requirement allotments per month.
11. `SHOP_STOCK`: Live shelf inventory at fair-price shops.
12. `DISPATCHES`: Consignment waybills from godown to shops.
13. `DISPATCH_ITEMS`: Line items for each truck consignment.
14. `SALES`: Counter sales headers with biometric authentication records.
15. `SALE_ITEMS`: Itemized sales transactions with subsidy amount tracking.
16. `STOCK_MOVEMENTS`: Immutable ledger of every physical stock addition, deduction, and transfer.
17. `STOCK_RECONCILIATION`: Weighment scale audits comparing recorded stock vs. physical counts.
18. `COMPLAINTS`: Citizen grievance registry with bilingual triage metadata.
19. `COMPLAINT_HISTORY`: Audit trail of grievance status transitions and officer notes.
20. `USERS`: System user accounts (Tahsildars, Inspectors, Shopkeepers, Godown Managers).
21. `ROLES`: RBAC security roles (`ROLE_ADMIN`, `ROLE_OFFICER`, `ROLE_GODOWN_MGR`, `ROLE_SHOPKEEPER`).
22. `USER_ROLES`: Many-to-many user-role assignments.
23. `AUDIT_LOGS`: System security and lifecycle modification journal.
24. `ERP_STAGING_MOVEMENTS`: Integration interface staging table for Oracle EBS / Fusion SCM with foreign key to `stock_movements`.

### B. Uniqueness & Data Integrity Guarantees
- **Aadhaar Fraud Prevention:** `uq_card_aadhaar UNIQUE (aadhaar_hash)` prevents duplicate card registration under the same citizen Aadhaar.
- **Duplicate Entitlement Prevention:** `uq_card_monthly_entitlement UNIQUE (card_id, commodity_id, allotment_year, allotment_month)` guarantees a cardholder can never receive duplicate monthly quota allocations.
- **Duplicate Allotment Prevention:** `uq_shop_monthly_allotment UNIQUE (shop_id, commodity_id, allotment_year, allotment_month)` ensures no duplicate shop requirements are created.
- **Shop Inventory Uniqueness:** `uq_shop_commodity UNIQUE (shop_id, commodity_id)` prevents conflicting duplicate stock records for the same commodity at a shop.
- **Physical Count Floor:** `chk_shop_stock_qty CHECK (current_quantity >= 0)` and `chk_ent_qty CHECK (remaining_quantity >= 0)` enforce at the database level that inventory and entitlements can never become negative.
- **Allotment Metric Bounds:** `chk_allot_qty CHECK (allocated_quantity >= 0 AND dispatched_quantity >= 0 AND received_quantity >= 0)` and `chk_shop_stock_totals` ensure nonnegative inventory balances.

---

## 4. PL/SQL Logic, Concurrency & Deadlock Prevention

### A. Quota Engine (`monthly_entitlement`)
Calculates the exact permissible quantity based on card category and household size:
- **`FIXED_PER_CARD`:** Flat quota per card (e.g., AAY receives 35.00 kg rice regardless of family size).
- **`PER_MEMBER`:** Linear scaling per active family member (e.g., Sugar: 0.5kg base + 0.25kg per member) capped at `max_quantity_ceiling` if configured.
- **`PER_MEMBER_WITH_CEILING`:** Scales per member (5 kg/member for PHH), enforces statutory minimum floor (12.00 kg base for single/dual-member households in Tamil Nadu), and caps at the statutory maximum ceiling (20.00 kg).
- **`SLAB_BASED`:** Incremental allocation for additional members beyond the household head.
- **Missing Rule Fallback:** Safely returns `0.00` if no active policy rule covers the commodity/card combination.

### B. Concurrency Control & Row-Level Locking (`pkg_pds_sales`)
To prevent concurrent POS overselling and race conditions across 120 shops:
1. **Pessimistic Cardholder Lock:**
   ```sql
   SELECT card_status, assigned_shop_id, card_number
   INTO v_card_status, v_card_shop_id, v_card_num
   FROM card_holders
   WHERE card_id = p_card_id
   FOR UPDATE;
   ```
   Serializes concurrent swipes of the same card across any counter/terminal, eliminating double-spending race conditions.
2. **Deterministic Commodity Lock Ordering (Deadlock Avoidance):**
   ```sql
   FOR item IN (
       SELECT jt.commodity_id, jt.quantity
       FROM JSON_TABLE(p_items_json, '$[*]'
           COLUMNS (
               commodity_id NUMBER PATH '$.commodityId',
               quantity     NUMBER PATH '$.quantity'
           )
       ) jt
       ORDER BY jt.commodity_id
   ) LOOP
   ```
   Sorting items by `commodity_id` ensures all concurrent transactions acquire row locks in identical sequential order, mathematically eliminating cross-commodity deadlocks (`ORA-00060`).
3. **Pessimistic Inventory & Quota Locks:**
   - Locks `shop_stock` row (`FOR UPDATE`) and validates `current_quantity >= requested_quantity`.
   - Locks `entitlements` row (`FOR UPDATE`) and validates `remaining_quantity >= requested_quantity`.
4. **Custom Exception Trapping:**
   - Quota exceeded: `RAISE_APPLICATION_ERROR(-20001, 'Entitlement exhausted...')` (maps to `e_entitlement_exhausted`).
   - Shop stock exceeded: `RAISE_APPLICATION_ERROR(-20002, 'Insufficient stock...')` (maps to `e_insufficient_stock`).
   - Card inactive: `RAISE_APPLICATION_ERROR(-20003, ...)` (maps to `e_card_inactive`).
   - Shop suspended: `RAISE_APPLICATION_ERROR(-20004, ...)` (maps to `e_shop_inactive`).
   - Empty items payload: `RAISE_APPLICATION_ERROR(-20019, 'Cannot process sale: No valid sale items...')`.
5. **Atomic Rollback Guarantee:**
   If any line item, stock check, or payload validation fails, an automatic `ROLLBACK` executes in `WHEN OTHERS`, ensuring zero partial updates, zero ghost stock decrements, and consistent audit trails.

### C. Non-Mutating Trigger Architecture (`trg_family_member_count_sync`)
To prevent `ORA-04091: table is mutating` when inserting, updating, or deleting family members:
- Uses direct mathematical delta updates on `card_holders.total_family_members` for non-SELF dependent members.
- Wrapped with an exception guard on `DELETING` to prevent mutating table errors during cascade deletes of the parent cardholder.

---

## 5. Verification Checklist & Live Oracle Test Requirements

| Test Scenario | Verification Script | Static Code Review Result | Live Oracle Test Requirement |
| :--- | :--- | :--- | :--- |
| **AAY Rice Quota (35 kg)** | `11_test_plsql.sql` (Test 1.1) | Verified logic: `FIXED_PER_CARD` returns `base_quantity` (35.00). | Run script in SQL*Plus; verify output equals `35.00`. |
| **PHH Rice Scaling (5 kg/member)** | `11_test_plsql.sql` (Test 1.2) | Verified logic: 3 members * 5kg = `15.00` kg. | Run script in SQL*Plus; verify output equals `15.00`. |
| **PHH Rice Ceiling (20 kg)** | `11_test_plsql.sql` (Test 1.3) | Verified logic: 6 members * 5kg = 30kg, capped at `20.00` kg. | Run script in SQL*Plus; verify output capped at `20.00`. |
| **Missing Rule Handling** | `11_test_plsql.sql` (Test 1.4) | Verified logic: Returns `0.00` when no active rule matches. | Run script in SQL*Plus; verify returns `0.00`. |
| **PHH Rice Minimum Floor** | `11_test_plsql.sql` (Test 1.5) | Verified logic: 1 member * 5kg = 5kg, floor lifts to `12.00` kg. | Run script in SQL*Plus; verify output equals `12.00`. |
| **PER_MEMBER Ceiling** | `11_test_plsql.sql` (Test 1.6) | Verified logic: 10 members * 0.25kg + 0.5kg = 3kg, capped at `2.00` kg ceiling. | Run script in SQL*Plus; verify output capped at `2.00`. |
| **Null Input Rejection** | `11_test_plsql.sql` (Test 2) | Verified logic: Raises `ORA-20010` on null parameters. | Run script in SQL*Plus; verify exception trapping. |
| **Allotment Approval Workflow** | `11_test_plsql.sql` (Test 3) | Verified logic: Generates entitlements and approves draft allotment. | Run script in SQL*Plus; verify status transitions to `APPROVED`. |
| **Overselling Beyond Quota** | `11_test_plsql.sql` (Test 4) | Verified logic: Raises `ORA-20001` (`e_entitlement_exhausted`). | Run script in SQL*Plus; verify rollback and error code. |
| **Overselling Beyond Stock** | `11_test_plsql.sql` (Test 5) | Verified logic: Raises `ORA-20002` (`e_insufficient_stock`). | Run script in SQL*Plus; verify rollback and error code. |
| **Atomic POS Sale & Ledger** | `11_test_plsql.sql` (Test 6) | Verified logic: Quota decrements to 3kg, `STOCK_MOVEMENTS` records bill. | Run script in SQL*Plus; verify database state and ledger. |
| **Stock Reconciliation** | `11_test_plsql.sql` (Test 7) | Verified logic: Physical count variance computed (-30kg), `PENDING_REVIEW`. | Run script in SQL*Plus; verify variance and status. |
| **Empty JSON Sale Rejection** | `11_test_plsql.sql` (Test 8) | Verified logic: `[]` rejected with `ORA-20019`, no empty sale committed. | Run script in SQL*Plus; verify rollback and error code. |
| **3-Month Non-Drawal Query** | `10_test_queries.sql` (Report 10) | Verified logic: Timestamp boundary `< TRUNC(SYSDATE, 'MM')` excludes current month. | Run script in SQL*Plus; verify flagged beneficiary output. |

