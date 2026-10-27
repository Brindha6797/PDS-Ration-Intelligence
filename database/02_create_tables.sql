-- ====================================================================
-- PDS RATION SHOP STOCK & ENTITLEMENT SYSTEM
-- SCRIPT 02: CREATE NORMALIZED RELATIONAL TABLES (ORACLE DDL)
-- Target: Oracle Database 19c / 21c / 23c
-- ====================================================================

SET SERVEROUTPUT ON;
PROMPT Creating Core Relational Tables...;

-- 1. CARD_TYPES
CREATE TABLE card_types (
    card_type_id        NUMBER,
    type_code           VARCHAR2(20) NOT NULL,
    type_name           VARCHAR2(100) NOT NULL,
    description         VARCHAR2(255),
    color_code          VARCHAR2(20),
    is_active           CHAR(1) DEFAULT 'Y' NOT NULL,
    created_at          TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL
);

-- 2. COMMODITIES
CREATE TABLE commodities (
    commodity_id                NUMBER,
    commodity_code              VARCHAR2(20) NOT NULL,
    commodity_name              VARCHAR2(100) NOT NULL,
    measurement_unit            VARCHAR2(20) NOT NULL,
    consumer_rate_per_unit      NUMBER(8,2) DEFAULT 0.00 NOT NULL,
    government_subsidy_per_unit NUMBER(8,2) DEFAULT 0.00 NOT NULL,
    buffer_stock_threshold      NUMBER(10,2) DEFAULT 100.00 NOT NULL,
    is_active                   CHAR(1) DEFAULT 'Y' NOT NULL,
    created_at                  TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL
);

-- 3. GODOWNS
CREATE TABLE godowns (
    godown_id           NUMBER,
    godown_code         VARCHAR2(20) NOT NULL,
    godown_name         VARCHAR2(150) NOT NULL,
    taluk_name          VARCHAR2(100) NOT NULL,
    storage_capacity_mt NUMBER(10,2) NOT NULL,
    manager_name        VARCHAR2(150) NOT NULL,
    contact_number      VARCHAR2(15) NOT NULL,
    address             VARCHAR2(255) NOT NULL,
    is_active           CHAR(1) DEFAULT 'Y' NOT NULL,
    created_at          TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL
);

-- 4. GODOWN_STOCK
CREATE TABLE godown_stock (
    godown_stock_id       NUMBER,
    godown_id             NUMBER NOT NULL,
    commodity_id          NUMBER NOT NULL,
    current_quantity      NUMBER(12,2) DEFAULT 0.00 NOT NULL,
    reserved_quantity     NUMBER(12,2) DEFAULT 0.00 NOT NULL,
    last_replenished_date DATE,
    updated_at            TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL
);

-- 5. SHOPS
CREATE TABLE shops (
    shop_id             NUMBER,
    shop_code           VARCHAR2(20) NOT NULL,
    shop_name           VARCHAR2(150) NOT NULL,
    license_number      VARCHAR2(50) NOT NULL,
    operator_name       VARCHAR2(150) NOT NULL,
    contact_number      VARCHAR2(15) NOT NULL,
    taluk_name          VARCHAR2(100) NOT NULL,
    ward_village        VARCHAR2(100) NOT NULL,
    address             VARCHAR2(255) NOT NULL,
    latitude            NUMBER(10,7),
    longitude           NUMBER(10,7),
    shop_status         VARCHAR2(20) DEFAULT 'ACTIVE' NOT NULL,
    created_at          TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    updated_at          TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL
);

-- 6. CARD_HOLDERS
CREATE TABLE card_holders (
    card_id               NUMBER,
    card_number           VARCHAR2(30) NOT NULL,
    card_type_id          NUMBER NOT NULL,
    head_of_family_name   VARCHAR2(150) NOT NULL,
    aadhaar_hash          VARCHAR2(64) NOT NULL,
    aadhaar_last4         VARCHAR2(4) NOT NULL,
    mobile_number         VARCHAR2(15),
    address_line1         VARCHAR2(255) NOT NULL,
    taluk_name            VARCHAR2(100) NOT NULL,
    district_name         VARCHAR2(100) NOT NULL,
    pincode               VARCHAR2(10) NOT NULL,
    assigned_shop_id      NUMBER NOT NULL,
    total_family_members  NUMBER(2) DEFAULT 1 NOT NULL,
    annual_income         NUMBER(10,2),
    card_status           VARCHAR2(20) DEFAULT 'ACTIVE' NOT NULL,
    effective_date        DATE DEFAULT SYSDATE NOT NULL,
    created_at            TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    updated_at            TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL
);

-- 7. FAMILY_MEMBERS
CREATE TABLE family_members (
    member_id            NUMBER,
    card_id              NUMBER NOT NULL,
    member_name          VARCHAR2(150) NOT NULL,
    relationship_to_head VARCHAR2(50) NOT NULL,
    gender               CHAR(1) NOT NULL,
    date_of_birth        DATE NOT NULL,
    age                  NUMBER(3),
    aadhaar_hash         VARCHAR2(64) NOT NULL,
    aadhaar_last4        VARCHAR2(4) NOT NULL,
    is_active            CHAR(1) DEFAULT 'Y' NOT NULL,
    created_at           TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    updated_at           TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL
);

-- 8. ENTITLEMENT_RULES
CREATE TABLE entitlement_rules (
    rule_id               NUMBER,
    card_type_id          NUMBER NOT NULL,
    commodity_id          NUMBER NOT NULL,
    calc_method           VARCHAR2(30) NOT NULL,
    base_quantity         NUMBER(8,2) DEFAULT 0.00 NOT NULL,
    per_member_quantity   NUMBER(8,2) DEFAULT 0.00 NOT NULL,
    min_family_size       NUMBER(2) DEFAULT 1,
    max_family_size       NUMBER(2),
    max_quantity_ceiling  NUMBER(8,2),
    effective_start_date  DATE NOT NULL,
    effective_end_date    DATE,
    is_active             CHAR(1) DEFAULT 'Y' NOT NULL,
    created_at            TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    updated_at            TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL
);

-- 9. ENTITLEMENTS
CREATE TABLE entitlements (
    entitlement_id        NUMBER,
    card_id               NUMBER NOT NULL,
    commodity_id          NUMBER NOT NULL,
    allotment_year        NUMBER(4) NOT NULL,
    allotment_month       NUMBER(2) NOT NULL,
    entitled_quantity     NUMBER(8,2) NOT NULL,
    distributed_quantity  NUMBER(8,2) DEFAULT 0.00 NOT NULL,
    remaining_quantity    NUMBER(8,2) NOT NULL,
    last_distributed_at   TIMESTAMP,
    status                VARCHAR2(20) DEFAULT 'AVAILABLE' NOT NULL,
    created_at            TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    updated_at            TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL
);

-- 10. ALLOTMENTS
CREATE TABLE allotments (
    allotment_id          NUMBER,
    shop_id               NUMBER NOT NULL,
    commodity_id          NUMBER NOT NULL,
    allotment_year        NUMBER(4) NOT NULL,
    allotment_month       NUMBER(2) NOT NULL,
    allocated_quantity    NUMBER(10,2) NOT NULL,
    dispatched_quantity   NUMBER(10,2) DEFAULT 0.00 NOT NULL,
    received_quantity     NUMBER(10,2) DEFAULT 0.00 NOT NULL,
    allotment_status      VARCHAR2(25) DEFAULT 'DRAFT' NOT NULL,
    approved_by_user_id   NUMBER,
    approved_at           TIMESTAMP,
    created_at            TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    updated_at            TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL
);

-- 11. SHOP_STOCK
CREATE TABLE shop_stock (
    stock_id                 NUMBER,
    shop_id                  NUMBER NOT NULL,
    commodity_id             NUMBER NOT NULL,
    current_quantity         NUMBER(10,2) DEFAULT 0.00 NOT NULL,
    opening_month_balance    NUMBER(10,2) DEFAULT 0.00 NOT NULL,
    total_received_month     NUMBER(10,2) DEFAULT 0.00 NOT NULL,
    total_distributed_month  NUMBER(10,2) DEFAULT 0.00 NOT NULL,
    last_sale_date           TIMESTAMP,
    last_reconciliation_date DATE,
    updated_at               TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL
);

-- 12. DISPATCHES
CREATE TABLE dispatches (
    dispatch_id           NUMBER,
    dispatch_number       VARCHAR2(30) NOT NULL,
    godown_id             NUMBER NOT NULL,
    shop_id               NUMBER NOT NULL,
    vehicle_number        VARCHAR2(30) NOT NULL,
    driver_name           VARCHAR2(100) NOT NULL,
    driver_phone          VARCHAR2(15),
    dispatch_status       VARCHAR2(20) DEFAULT 'IN_TRANSIT' NOT NULL,
    dispatched_at         TIMESTAMP NOT NULL,
    received_at           TIMESTAMP,
    dispatched_by         VARCHAR2(50) NOT NULL,
    received_by           VARCHAR2(50),
    remarks               VARCHAR2(255),
    created_at            TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL
);

-- 13. DISPATCH_ITEMS
CREATE TABLE dispatch_items (
    dispatch_item_id      NUMBER,
    dispatch_id           NUMBER NOT NULL,
    commodity_id          NUMBER NOT NULL,
    dispatched_quantity   NUMBER(10,2) NOT NULL,
    received_quantity     NUMBER(10,2),
    discrepancy_quantity  NUMBER(10,2) DEFAULT 0.00,
    condition_notes       VARCHAR2(255)
);

-- 14. SALES
CREATE TABLE sales (
    sale_id               NUMBER,
    bill_number           VARCHAR2(35) NOT NULL,
    shop_id               NUMBER NOT NULL,
    card_id               NUMBER NOT NULL,
    sale_date             TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    sale_month            NUMBER(2) NOT NULL,
    sale_year             NUMBER(4) NOT NULL,
    total_amount          NUMBER(8,2) DEFAULT 0.00 NOT NULL,
    payment_mode          VARCHAR2(20) DEFAULT 'CASH' NOT NULL,
    auth_mode             VARCHAR2(20) DEFAULT 'AADHAAR_OTP' NOT NULL,
    operator_user_id      NUMBER NOT NULL,
    sale_status           VARCHAR2(20) DEFAULT 'COMPLETED' NOT NULL,
    created_at            TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL
);

-- 15. SALE_ITEMS
CREATE TABLE sale_items (
    sale_item_id          NUMBER,
    sale_id               NUMBER NOT NULL,
    commodity_id          NUMBER NOT NULL,
    quantity              NUMBER(8,2) NOT NULL,
    unit_rate             NUMBER(8,2) NOT NULL,
    total_price           NUMBER(8,2) NOT NULL,
    subsidy_amount        NUMBER(8,2) DEFAULT 0.00
);

-- 16. STOCK_MOVEMENTS
CREATE TABLE stock_movements (
    movement_id           NUMBER,
    movement_type         VARCHAR2(30) NOT NULL,
    source_type           VARCHAR2(20),
    source_id             NUMBER,
    dest_type             VARCHAR2(20),
    dest_id               NUMBER,
    commodity_id          NUMBER NOT NULL,
    quantity              NUMBER(10,2) NOT NULL,
    reference_doc_type    VARCHAR2(30),
    reference_doc_id      VARCHAR2(50),
    created_by            VARCHAR2(50) NOT NULL,
    movement_timestamp    TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL
);

-- 17. STOCK_RECONCILIATION
CREATE TABLE stock_reconciliation (
    recon_id                 NUMBER,
    shop_id                  NUMBER NOT NULL,
    commodity_id             NUMBER NOT NULL,
    recon_date               DATE NOT NULL,
    recorded_system_stock    NUMBER(10,2) NOT NULL,
    calculated_expected_stock NUMBER(10,2) NOT NULL,
    physical_counted_stock   NUMBER(10,2) NOT NULL,
    variance_quantity        NUMBER(10,2) NOT NULL,
    discrepancy_percentage   NUMBER(5,2),
    investigation_status     VARCHAR2(25) DEFAULT 'PENDING_REVIEW' NOT NULL,
    inspecting_officer_id    NUMBER NOT NULL,
    officer_remarks          VARCHAR2(500),
    adjustment_movement_id   NUMBER,
    created_at               TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL
);

-- 18. COMPLAINTS
CREATE TABLE complaints (
    complaint_id          NUMBER,
    complaint_ref         VARCHAR2(30) NOT NULL,
    card_id               NUMBER,
    shop_id               NUMBER,
    category              VARCHAR2(50) NOT NULL,
    description           VARCHAR2(2000) NOT NULL,
    language              VARCHAR2(10) DEFAULT 'en' NOT NULL,
    priority              VARCHAR2(20) DEFAULT 'MEDIUM' NOT NULL,
    status                VARCHAR2(25) DEFAULT 'REGISTERED' NOT NULL,
    assigned_officer_id   NUMBER,
    resolution_notes      VARCHAR2(2000),
    resolved_at           TIMESTAMP,
    created_at            TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    updated_at            TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL
);

-- 19. COMPLAINT_HISTORY
CREATE TABLE complaint_history (
    history_id            NUMBER,
    complaint_id          NUMBER NOT NULL,
    previous_status       VARCHAR2(25),
    new_status            VARCHAR2(25) NOT NULL,
    changed_by_user       VARCHAR2(50) NOT NULL,
    action_taken          VARCHAR2(255) NOT NULL,
    action_timestamp      TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    comments              VARCHAR2(1000)
);

-- 20. USERS
CREATE TABLE users (
    user_id               NUMBER,
    username              VARCHAR2(50) NOT NULL,
    password_hash         VARCHAR2(255) NOT NULL,
    full_name             VARCHAR2(150) NOT NULL,
    email                 VARCHAR2(100),
    phone                 VARCHAR2(15),
    taluk_name            VARCHAR2(100),
    assigned_shop_id      NUMBER,
    assigned_godown_id    NUMBER,
    is_active             CHAR(1) DEFAULT 'Y' NOT NULL,
    last_login_at         TIMESTAMP,
    created_at            TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    updated_at            TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL
);

-- 21. ROLES
CREATE TABLE roles (
    role_id               NUMBER,
    role_name             VARCHAR2(50) NOT NULL,
    description           VARCHAR2(255)
);

-- 22. USER_ROLES
CREATE TABLE user_roles (
    user_id               NUMBER NOT NULL,
    role_id               NUMBER NOT NULL
);

-- 23. AUDIT_LOGS
CREATE TABLE audit_logs (
    log_id                NUMBER,
    entity_name           VARCHAR2(50) NOT NULL,
    entity_id             VARCHAR2(50) NOT NULL,
    action_type           VARCHAR2(20) NOT NULL,
    performed_by          VARCHAR2(50) NOT NULL,
    action_timestamp      TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    old_values            CLOB,
    new_values            CLOB,
    client_ip             VARCHAR2(45),
    status_result         VARCHAR2(20) DEFAULT 'SUCCESS' NOT NULL
);

-- 24. ERP_STAGING_MOVEMENTS (EBS / Fusion Interface)
CREATE TABLE erp_staging_movements (
    staging_id            NUMBER,
    movement_id           NUMBER NOT NULL,
    erp_organization_code VARCHAR2(10) DEFAULT 'PDS_TLK',
    erp_item_number       VARCHAR2(40) NOT NULL,
    transaction_quantity  NUMBER(12,2) NOT NULL,
    transaction_uom       VARCHAR2(3) NOT NULL,
    source_warehouse      VARCHAR2(20),
    destination_subinventory VARCHAR2(20),
    process_status        VARCHAR2(20) DEFAULT 'PENDING' NOT NULL,
    erp_transaction_id    NUMBER,
    error_message         VARCHAR2(1000),
    created_at            TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL
);

PROMPT Tables successfully created.
