-- ====================================================================
-- PDS RATION SHOP STOCK & ENTITLEMENT SYSTEM
-- SCRIPT 10: ANALYTICAL & REPORTING TEST QUERIES (ORACLE SQL)
-- Target: Oracle Database 19c / 21c / 23c
-- ====================================================================

SET LINESIZE 250;
SET PAGESIZE 100;

PROMPT ================================================================;
PROMPT REPORT 1: SHOP-WISE MONTHLY COMMODITY SALES MATRIX;
PROMPT ================================================================;
SELECT 
    s.shop_code,
    s.shop_name,
    TO_CHAR(sl.sale_date, 'YYYY-MM') AS sale_period,
    COUNT(DISTINCT sl.sale_id) AS total_bills,
    NVL(SUM(CASE WHEN c.commodity_code = 'RICE_COMMON' THEN si.quantity ELSE 0 END), 0) AS rice_kg,
    NVL(SUM(CASE WHEN c.commodity_code = 'WHEAT' THEN si.quantity ELSE 0 END), 0) AS wheat_kg,
    NVL(SUM(CASE WHEN c.commodity_code = 'SUGAR' THEN si.quantity ELSE 0 END), 0) AS sugar_kg,
    NVL(SUM(CASE WHEN c.commodity_code = 'PALM_OIL' THEN si.quantity ELSE 0 END), 0) AS oil_pkts,
    NVL(SUM(CASE WHEN c.commodity_code = 'TOOR_DAL' THEN si.quantity ELSE 0 END), 0) AS dal_kg,
    SUM(si.total_price) AS total_revenue
FROM shops s
JOIN sales sl ON s.shop_id = sl.shop_id AND sl.sale_status = 'COMPLETED'
JOIN sale_items si ON sl.sale_id = si.sale_id
JOIN commodities c ON si.commodity_id = c.commodity_id
GROUP BY s.shop_code, s.shop_name, TO_CHAR(sl.sale_date, 'YYYY-MM')
ORDER BY s.shop_code;

PROMPT ================================================================;
PROMPT REPORT 2: GODOWN-WISE STOCK REGISTER;
PROMPT ================================================================;
SELECT 
    g.godown_code,
    g.godown_name,
    c.commodity_name,
    c.measurement_unit,
    gs.current_quantity AS available_stock,
    gs.reserved_quantity AS committed_in_transit,
    gs.last_replenished_date
FROM godowns g
JOIN godown_stock gs ON g.godown_id = gs.godown_id
JOIN commodities c ON gs.commodity_id = c.commodity_id
ORDER BY g.godown_code, c.commodity_name;

PROMPT ================================================================;
PROMPT REPORT 3: CARDHOLDER DISTRIBUTION HISTORY;
PROMPT ================================================================;
SELECT 
    ch.card_number,
    ch.head_of_family_name,
    ct.type_code AS card_category,
    s.bill_number,
    s.sale_date,
    c.commodity_name,
    si.quantity,
    c.measurement_unit,
    si.total_price,
    s.payment_mode
FROM card_holders ch
JOIN card_types ct ON ch.card_type_id = ct.card_type_id
JOIN sales s ON ch.card_id = s.card_id
JOIN sale_items si ON s.sale_id = si.sale_id
JOIN commodities c ON si.commodity_id = c.commodity_id
ORDER BY s.sale_date DESC;

PROMPT ================================================================;
PROMPT REPORT 4: REMAINING ENTITLEMENT REPORT;
PROMPT ================================================================;
SELECT 
    ch.card_number,
    ch.head_of_family_name,
    ct.type_code,
    sh.shop_code,
    e.allotment_year,
    e.allotment_month,
    c.commodity_name,
    e.entitled_quantity,
    e.distributed_quantity,
    e.remaining_quantity,
    e.status
FROM entitlements e
JOIN card_holders ch ON e.card_id = ch.card_id
JOIN card_types ct ON ch.card_type_id = ct.card_type_id
JOIN shops sh ON ch.assigned_shop_id = sh.shop_id
JOIN commodities c ON e.commodity_id = c.commodity_id
WHERE e.remaining_quantity > 0
ORDER BY ch.card_number, c.commodity_name;

PROMPT ================================================================;
PROMPT REPORT 5: MONTHLY ALLOTMENT VERSUS DISTRIBUTION;
PROMPT ================================================================;
SELECT 
    s.shop_code,
    s.shop_name,
    a.allotment_year,
    a.allotment_month,
    c.commodity_name,
    a.allocated_quantity AS quota_allocated,
    a.dispatched_quantity AS godown_dispatched,
    a.received_quantity AS shop_received,
    NVL(ss.total_distributed_month, 0) AS shop_distributed,
    ROUND((NVL(ss.total_distributed_month, 0) / NULLIF(a.allocated_quantity, 0)) * 100, 2) AS distribution_pct
FROM allotments a
JOIN shops s ON a.shop_id = s.shop_id
JOIN commodities c ON a.commodity_id = c.commodity_id
LEFT JOIN shop_stock ss ON a.shop_id = ss.shop_id AND a.commodity_id = ss.commodity_id
ORDER BY s.shop_code, c.commodity_name;

PROMPT ================================================================;
PROMPT REPORT 6: STOCK RECONCILIATION & VARIANCE REPORT;
PROMPT ================================================================;
SELECT 
    sr.recon_id,
    s.shop_code,
    s.shop_name,
    c.commodity_name,
    sr.recon_date,
    sr.recorded_system_stock,
    sr.calculated_expected_stock,
    sr.physical_counted_stock,
    sr.variance_quantity,
    sr.discrepancy_percentage,
    sr.investigation_status,
    sr.officer_remarks
FROM stock_reconciliation sr
JOIN shops s ON sr.shop_id = s.shop_id
JOIN commodities c ON sr.commodity_id = c.commodity_id
ORDER BY sr.recon_date DESC;

PROMPT ================================================================;
PROMPT REPORT 7: POTENTIAL STOCK ANOMALY AUDIT (DISCREPANCY > 2%);
PROMPT ================================================================;
SELECT 
    s.shop_code,
    s.shop_name,
    c.commodity_name,
    sr.recon_date,
    sr.variance_quantity,
    sr.discrepancy_percentage,
    sr.investigation_status,
    'Physical inventory differs by ' || sr.discrepancy_percentage || '% from recorded inventory' AS anomaly_reason
FROM stock_reconciliation sr
JOIN shops s ON sr.shop_id = s.shop_id
JOIN commodities c ON sr.commodity_id = c.commodity_id
WHERE ABS(sr.discrepancy_percentage) > 2.0
ORDER BY ABS(sr.discrepancy_percentage) DESC;

PROMPT ================================================================;
PROMPT REPORT 8: COMMODITY DEMAND FORECAST & AVERAGE RUN RATE;
PROMPT ================================================================;
SELECT 
    s.shop_code,
    c.commodity_name,
    ss.current_quantity AS available_stock,
    NVL(ss.total_distributed_month, 0) AS mtd_sales,
    ROUND(NVL(ss.total_distributed_month, 0) / GREATEST(TO_NUMBER(TO_CHAR(SYSDATE, 'DD')), 1), 2) AS daily_run_rate,
    ROUND(ss.current_quantity / NULLIF(ROUND(NVL(ss.total_distributed_month, 0) / GREATEST(TO_NUMBER(TO_CHAR(SYSDATE, 'DD')), 1), 2), 0), 1) AS estimated_days_stock_remaining
FROM shop_stock ss
JOIN shops s ON ss.shop_id = s.shop_id
JOIN commodities c ON ss.commodity_id = c.commodity_id
ORDER BY s.shop_code, c.commodity_name;

PROMPT ================================================================;
PROMPT REPORT 9: SHOPS AT RISK OF STOCK SHORTAGE (< 5 DAYS REMAINING);
PROMPT ================================================================;
SELECT 
    s.shop_code,
    s.shop_name,
    c.commodity_name,
    ss.current_quantity AS available_kg,
    ROUND(ss.current_quantity / NULLIF(ROUND(NVL(ss.total_distributed_month, 0) / GREATEST(TO_NUMBER(TO_CHAR(SYSDATE, 'DD')), 1), 2), 0), 1) AS days_remaining,
    'CRITICAL LOW INVENTORY' AS alert_level
FROM shop_stock ss
JOIN shops s ON ss.shop_id = s.shop_id
JOIN commodities c ON ss.commodity_id = c.commodity_id
WHERE ss.current_quantity < c.buffer_stock_threshold
   OR (ss.current_quantity / NULLIF(ROUND(NVL(ss.total_distributed_month, 0) / GREATEST(TO_NUMBER(TO_CHAR(SYSDATE, 'DD')), 1), 2), 0)) < 5.0
ORDER BY ss.current_quantity ASC;

PROMPT ================================================================;
PROMPT REPORT 10: CARDS WITH NO DRAWAL FOR 3 COMPLETED CONSECUTIVE MONTHS;
PROMPT Strict boundary: Excludes current incomplete month;
PROMPT ================================================================;
WITH completed_months AS (
    SELECT 
        ADD_MONTHS(TRUNC(SYSDATE, 'MM'), -3) AS start_window,
        TRUNC(SYSDATE, 'MM') AS end_window
    FROM DUAL
)
SELECT 
    ch.card_id,
    ch.card_number,
    ch.head_of_family_name,
    ct.type_code,
    s.shop_code,
    ch.mobile_number,
    'NO_TRANSACTION_LAST_3_COMPLETED_MONTHS' AS audit_flag
FROM card_holders ch
JOIN card_types ct ON ch.card_type_id = ct.card_type_id
JOIN shops s ON ch.assigned_shop_id = s.shop_id
CROSS JOIN completed_months cm
WHERE ch.card_status = 'ACTIVE'
  AND NOT EXISTS (
      SELECT 1 
      FROM sales sl
      WHERE sl.card_id = ch.card_id
        AND sl.sale_date >= cm.start_window
        AND sl.sale_date < cm.end_window
        AND sl.sale_status = 'COMPLETED'
  )
ORDER BY s.shop_code, ch.card_number;

PROMPT ================================================================;
PROMPT REPORT 11: COMPLAINT STATUS AND RESOLUTION REPORT;
PROMPT ================================================================;
SELECT 
    cmp.complaint_ref,
    cmp.category,
    s.shop_code,
    ch.card_number,
    cmp.priority,
    cmp.status,
    cmp.created_at,
    cmp.resolved_at,
    ROUND((CAST(NVL(cmp.resolved_at, SYSTIMESTAMP) AS DATE) - CAST(cmp.created_at AS DATE)) * 24, 1) AS hours_to_resolve,
    cmp.description
FROM complaints cmp
LEFT JOIN shops s ON cmp.shop_id = s.shop_id
LEFT JOIN card_holders ch ON cmp.card_id = ch.card_id
ORDER BY cmp.created_at DESC;

PROMPT ================================================================;
PROMPT REPORT 12: SYSTEM AUDIT HISTORY REPORT;
PROMPT ================================================================;
SELECT 
    log_id,
    entity_name,
    entity_id,
    action_type,
    performed_by,
    action_timestamp,
    status_result
FROM audit_logs
ORDER BY action_timestamp DESC;

PROMPT ================================================================;
PROMPT COMPILING ENTERPRISE INTEGRATION VIEWS (REPORTS / OTBI / EBS);
PROMPT ================================================================;

-- 1. Oracle Reports / BI Publisher: Shop-Wise Monthly Matrix View
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
    NVL(SUM(CASE WHEN c.commodity_code = 'TOOR_DAL' THEN si.quantity ELSE 0 END), 0) AS dal_distributed_kg,
    COUNT(DISTINCT sl.sale_id) AS total_bills_issued,
    SUM(si.total_price) AS total_revenue_collected
FROM shops s
JOIN sales sl ON s.shop_id = sl.shop_id AND sl.sale_status = 'COMPLETED'
JOIN sale_items si ON sl.sale_id = si.sale_id
JOIN commodities c ON si.commodity_id = c.commodity_id
GROUP BY s.shop_code, s.shop_name, s.ward_village, TO_CHAR(sl.sale_date, 'YYYY-MM');

-- 2. Oracle Reports / BI Publisher: Godown Stock Register View
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

-- 3. OTBI Subject Area: Stock Leakage & Anomaly Indicators
CREATE OR REPLACE VIEW v_otbi_leakage_indicators AS
SELECT 
    sr.recon_id,
    s.shop_code,
    s.shop_name,
    s.taluk_name,
    c.commodity_code,
    c.commodity_name,
    sr.recon_date,
    sr.recorded_system_stock,
    sr.calculated_expected_stock,
    sr.physical_counted_stock,
    sr.variance_quantity,
    sr.discrepancy_percentage,
    sr.investigation_status,
    u.full_name AS inspecting_officer_name,
    sr.officer_remarks,
    CASE 
        WHEN ABS(sr.discrepancy_percentage) > 2.0 THEN 'CRITICAL_DISCREPANCY'
        WHEN ABS(sr.discrepancy_percentage) > 0.0 THEN 'MODERATE_VARIANCE'
        ELSE 'MATCHED'
    END AS leakage_severity_band
FROM stock_reconciliation sr
JOIN shops s ON sr.shop_id = s.shop_id
JOIN commodities c ON sr.commodity_id = c.commodity_id
JOIN users u ON sr.inspecting_officer_id = u.user_id;

-- 4. OTBI Subject Area: 3-Month Non-Drawal Beneficiary Welfare Audit
CREATE OR REPLACE VIEW v_otbi_non_drawal_3months AS
WITH completed_months AS (
    SELECT 
        ADD_MONTHS(TRUNC(SYSDATE, 'MM'), -3) AS start_window,
        TRUNC(SYSDATE, 'MM') AS end_window
    FROM DUAL
)
SELECT 
    ch.card_id,
    ch.card_number,
    ch.head_of_family_name,
    ct.type_code AS card_type,
    s.shop_code,
    s.shop_name,
    ch.mobile_number,
    ch.total_family_members,
    cm.start_window AS audit_window_start,
    cm.end_window AS audit_window_end,
    'NO_TRANSACTION_LAST_3_COMPLETED_MONTHS' AS audit_flag
FROM card_holders ch
JOIN card_types ct ON ch.card_type_id = ct.card_type_id
JOIN shops s ON ch.assigned_shop_id = s.shop_id
CROSS JOIN completed_months cm
WHERE ch.card_status = 'ACTIVE'
  AND NOT EXISTS (
      SELECT 1 
      FROM sales sl
      WHERE sl.card_id = ch.card_id
        AND sl.sale_date >= cm.start_window
        AND sl.sale_date < cm.end_window
        AND sl.sale_status = 'COMPLETED'
  );

-- 5. OTBI Subject Area: FPS Operational Performance Benchmark
CREATE OR REPLACE VIEW v_otbi_fps_performance AS
SELECT 
    s.shop_id,
    s.shop_code,
    s.shop_name,
    s.taluk_name,
    s.shop_status,
    COUNT(DISTINCT ch.card_id) AS total_assigned_cards,
    (SELECT COUNT(DISTINCT sl.sale_id) FROM sales sl WHERE sl.shop_id = s.shop_id AND sl.sale_date >= TRUNC(SYSDATE, 'MM') AND sl.sale_status = 'COMPLETED') AS mtd_sales_count,
    (SELECT COUNT(*) FROM complaints cmp WHERE cmp.shop_id = s.shop_id AND cmp.created_at >= TRUNC(SYSDATE, 'MM')) AS mtd_complaints_count
FROM shops s
LEFT JOIN card_holders ch ON s.shop_id = ch.assigned_shop_id AND ch.card_status = 'ACTIVE'
GROUP BY s.shop_id, s.shop_code, s.shop_name, s.taluk_name, s.shop_status;

PROMPT Enterprise integration views created successfully.
