-- ====================================================================
-- PDS RATION SHOP STOCK & ENTITLEMENT SYSTEM
-- SCRIPT 04: CREATE ORACLE SEQUENCES
-- Target: Oracle Database 19c / 21c / 23c
-- ====================================================================

SET SERVEROUTPUT ON;
PROMPT Creating Oracle Sequences for Primary Keys (Offset for Sample Data)...;

-- Drop existing sequences if re-running
BEGIN
    FOR s IN (SELECT sequence_name FROM user_sequences WHERE sequence_name LIKE 'SEQ_%') LOOP
        EXECUTE IMMEDIATE 'DROP SEQUENCE ' || s.sequence_name;
    END LOOP;
EXCEPTION
    WHEN OTHERS THEN NULL;
END;
/

-- Sequences configured with safe starting offsets to prevent ORA-00001 collisions with sample data
CREATE SEQUENCE seq_card_types           START WITH 10    INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_commodities          START WITH 10    INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_godowns              START WITH 10    INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_godown_stock         START WITH 100   INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_shops                START WITH 100   INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_card_holders         START WITH 1100  INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_family_members       START WITH 5100  INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_entitlement_rules    START WITH 100   INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_entitlements         START WITH 100   INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_allotments           START WITH 100   INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_shop_stock           START WITH 100   INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_dispatches           START WITH 200   INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_dispatch_items       START WITH 600   INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_sales                START WITH 11000 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_sale_items           START WITH 51000 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_stock_movements      START WITH 100   INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_stock_reconciliation START WITH 100   INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_complaints           START WITH 200   INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_complaint_history   START WITH 600   INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_users                START WITH 100   INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_roles                START WITH 100   INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_audit_logs           START WITH 100   INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_erp_staging          START WITH 100   INCREMENT BY 1 NOCACHE;

PROMPT Sequences created successfully with collision-safe offsets.
