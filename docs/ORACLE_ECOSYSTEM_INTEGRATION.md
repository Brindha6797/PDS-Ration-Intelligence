# PDS-Ration-Intelligence: Oracle Ecosystem Integration Architecture
## Technical Specifications for Oracle Forms, Oracle Reports, EBS/Fusion & OTBI

### 1. Context & Purpose
In enterprise public distribution projects (such as those evaluated by Wipro and state governments), the centralized Oracle Database schema acts as the unifying foundation for both modern web interfaces (React/Spring Boot) and legacy/enterprise Oracle products.

This specification documents the database views, stored packages, and interface contracts that enable seamless operation with:
1. **Oracle Forms (12c/19c)**
2. **Oracle Reports / BI Publisher**
3. **Oracle E-Business Suite (EBS R12) / Oracle Fusion Cloud ERP**
4. **Oracle Transactional Business Intelligence (OTBI)**

> [!NOTE]
> Per Master Project Specifications, the core operational application is delivered using React + Spring Boot + Oracle Database. This document details the database objects and architecture designed to integrate directly with Oracle Forms, Reports, EBS, and OTBI without assuming live licensed installations in the local development environment.

---

### 2. Oracle Forms Integration (Sales Entry & Inwarding)

#### A. Architecture
Oracle Forms communicates directly with the database using PL/SQL block calls and block-level triggers (`PRE-INSERT`, `POST-UPDATE`, `ON-COMMIT`).

```
+-----------------------------------------------------------+
|                   ORACLE FORMS 12c RUNTIME                |
|  - FRM-SALES-ENTRY.fmb (POS Distribution Terminal)        |
|  - Block: B_SALES_HEADER (Mapped to SALES table)          |
|  - Block: B_SALE_ITEMS (Mapped to SALE_ITEMS table)       |
|  - LOV: LOV_RATION_CARDS (SELECT ... FROM V_ACTIVE_CARDS) |
+-----------------------------------------------------------+
                             |
                   SQL*Net / PL/SQL
                             v
+-----------------------------------------------------------+
|                   ORACLE DATABASE 19c                     |
|  - Package: pkg_pds_forms_bridge                          |
|  - Procedure: process_forms_sale                          |
|  - Trigger: TRG_SALES_AUDIT                               |
+-----------------------------------------------------------+
```

#### B. Forms List of Values (LOV) Queries
- **`LOV_RATION_CARDS`:**
  ```sql
  SELECT c.card_id, c.card_number, c.head_of_family_name, ct.type_code, c.total_family_members
  FROM card_holders c
  JOIN card_types ct ON c.card_type_id = ct.card_type_id
  WHERE c.assigned_shop_id = :GLOBAL.current_shop_id
    AND c.card_status = 'ACTIVE'
  ORDER BY c.card_number;
  ```
- **`LOV_COMMODITIES` with Live Entitlement Display:**
  ```sql
  SELECT comm.commodity_id, comm.commodity_name, e.remaining_quantity, comm.consumer_rate_per_unit
  FROM entitlements e
  JOIN commodities comm ON e.commodity_id = comm.commodity_id
  WHERE e.card_id = :B_SALES_HEADER.card_id
    AND e.allotment_year = TO_NUMBER(TO_CHAR(SYSDATE, 'YYYY'))
    AND e.allotment_month = TO_NUMBER(TO_CHAR(SYSDATE, 'MM'))
    AND e.remaining_quantity > 0;
  ```

#### C. Forms Custom Error Handling (`ON-ERROR` Trigger)
When `pkg_pds_sales.process_sale` raises custom exceptions `e_entitlement_exhausted` (ORA-20001) or `e_insufficient_stock` (ORA-20002), the Forms `ON-ERROR` trigger captures the Oracle error and alerts the operator:
```plsql
-- Forms Block-level Trigger: ON-ERROR
DECLARE
  err_code NUMBER := ERROR_CODE;
  err_txt  VARCHAR2(255) := ERROR_TEXT;
BEGIN
  IF DBMS_ERROR_CODE = -20001 THEN
    MESSAGE('PDS-ALERT: Cardholder entitlement exhausted for this commodity!');
  ELSIF DBMS_ERROR_CODE = -20002 THEN
    MESSAGE('PDS-ALERT: Insufficient inventory available in shop stock!');
  ELSE
    MESSAGE('Database Error: ' || DBMS_ERROR_TEXT);
  END IF;
END;
```

---

### 3. Oracle Reports & BI Publisher Data Models

#### A. Report 1: Shop-Wise Monthly Commodity Sales Matrix
Provides taluk-wide distribution performance in a cross-tabulated matrix.
```sql
CREATE OR REPLACE VIEW v_rep_shop_monthly_matrix AS
SELECT 
    s.shop_code,
    s.shop_name,
    s.ward_village,
    TO_CHAR(sl.sale_date, 'YYYY-MM') AS sale_period,
    NVL(SUM(CASE WHEN c.commodity_code = 'RICE_COMMON' THEN si.quantity ELSE 0 END), 0) AS rice_distributed_kg,
    NVL(SUM(CASE WHEN c.commodity_code = 'WHEAT' THEN si.quantity ELSE 0 END), 0) AS wheat_distributed_kg,
    NVL(SUM(CASE WHEN c.commodity_code = 'SUGAR' THEN si.quantity ELSE 0 END), 0) AS sugar_distributed_kg,
    NVL(SUM(CASE WHEN c.commodity_code = 'PALM_OIL' THEN si.quantity ELSE 0 END), 0) AS oil_distributed_pkts,
    NVL(SUM(CASE WHEN c.commodity_code = 'KEROSENE' THEN si.quantity ELSE 0 END), 0) AS kerosene_distributed_ltr,
    COUNT(DISTINCT sl.sale_id) AS total_bills_issued,
    SUM(si.total_price) AS total_revenue_collected
FROM shops s
JOIN sales sl ON s.shop_id = sl.shop_id AND sl.sale_status = 'COMPLETED'
JOIN sale_items si ON sl.sale_id = si.sale_id
JOIN commodities c ON si.commodity_id = c.commodity_id
GROUP BY s.shop_code, s.shop_name, s.ward_village, TO_CHAR(sl.sale_date, 'YYYY-MM');
```

#### B. Report 2: Godown-Wise Stock Register
Official periodic inventory log for state auditing.
```sql
CREATE OR REPLACE VIEW v_rep_godown_stock_register AS
SELECT 
    g.godown_code,
    g.godown_name,
    c.commodity_name,
    c.measurement_unit,
    gs.current_quantity AS closing_physical_stock,
    gs.reserved_quantity AS committed_dispatches,
    gs.last_replenished_date,
    (SELECT NVL(SUM(di.dispatched_quantity), 0)
     FROM dispatches d
     JOIN dispatch_items di ON d.dispatch_id = di.dispatch_id
     WHERE d.godown_id = g.godown_id AND di.commodity_id = c.commodity_id
       AND d.dispatched_at >= TRUNC(SYSDATE, 'MM')) AS mtd_total_dispatched
FROM godowns g
JOIN godown_stock gs ON g.godown_id = gs.godown_id
JOIN commodities c ON gs.commodity_id = c.commodity_id;
```

---

### 4. Oracle E-Business Suite (EBS) & Fusion ERP Integration

#### A. Outbound State Interface (Inter-Depot Transfers)
For state-wide grain distribution, Oracle Inventory (INV) in EBS/Fusion requires standard interface tables:
- `MTL_TRANSACTIONS_INTERFACE`
- `MTL_SYSTEM_ITEMS_B`

#### B. PDS Staging Table for ERP Ingestion: `ERP_STAGING_MOVEMENTS`
```sql
CREATE TABLE erp_staging_movements (
    staging_id NUMBER PRIMARY KEY,
    movement_id NUMBER NOT NULL,
    erp_organization_code VARCHAR2(10) DEFAULT 'PDS_TLK',
    erp_item_number VARCHAR2(40) NOT NULL,
    transaction_quantity NUMBER(12,2) NOT NULL,
    transaction_uom VARCHAR2(3) NOT NULL,
    source_warehouse VARCHAR2(20),
    destination_subinventory VARCHAR2(20),
    process_status VARCHAR2(20) DEFAULT 'PENDING',
    erp_transaction_id NUMBER,
    error_message VARCHAR2(1000),
    created_at TIMESTAMP DEFAULT SYSTIMESTAMP
);
```

---

### 5. Oracle Transactional Business Intelligence (OTBI)

OTBI connects directly to Oracle Database Subject Areas. The following analytical views are defined for OTBI Subject Area models:
1. `V_OTBI_LEAKAGE_INDICATORS`: Compares expected vs. physical stock variance to flag potential supply chain losses.
2. `V_OTBI_NON_DRAWAL_3MONTHS`: Tracks ration cards with zero transactions over 3 completed consecutive months for welfare audit.
3. `V_OTBI_FPS_PERFORMANCE`: Benchmarks shop operational metrics, opening hours, transaction counts, and grievance frequencies.
