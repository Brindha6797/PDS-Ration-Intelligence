-- ====================================================================
-- PDS RATION SHOP STOCK & ENTITLEMENT SYSTEM
-- SCRIPT 03: CREATE CONSTRAINTS & PERFORMANCE INDEXES
-- Target: Oracle Database 19c / 21c / 23c
-- ====================================================================

SET SERVEROUTPUT ON;
PROMPT Creating Primary Keys...;

ALTER TABLE card_types ADD CONSTRAINT pk_card_types PRIMARY KEY (card_type_id);
ALTER TABLE commodities ADD CONSTRAINT pk_commodities PRIMARY KEY (commodity_id);
ALTER TABLE godowns ADD CONSTRAINT pk_godowns PRIMARY KEY (godown_id);
ALTER TABLE godown_stock ADD CONSTRAINT pk_godown_stock PRIMARY KEY (godown_stock_id);
ALTER TABLE shops ADD CONSTRAINT pk_shops PRIMARY KEY (shop_id);
ALTER TABLE card_holders ADD CONSTRAINT pk_card_holders PRIMARY KEY (card_id);
ALTER TABLE family_members ADD CONSTRAINT pk_family_members PRIMARY KEY (member_id);
ALTER TABLE entitlement_rules ADD CONSTRAINT pk_entitlement_rules PRIMARY KEY (rule_id);
ALTER TABLE entitlements ADD CONSTRAINT pk_entitlements PRIMARY KEY (entitlement_id);
ALTER TABLE allotments ADD CONSTRAINT pk_allotments PRIMARY KEY (allotment_id);
ALTER TABLE shop_stock ADD CONSTRAINT pk_shop_stock PRIMARY KEY (stock_id);
ALTER TABLE dispatches ADD CONSTRAINT pk_dispatches PRIMARY KEY (dispatch_id);
ALTER TABLE dispatch_items ADD CONSTRAINT pk_dispatch_items PRIMARY KEY (dispatch_item_id);
ALTER TABLE sales ADD CONSTRAINT pk_sales PRIMARY KEY (sale_id);
ALTER TABLE sale_items ADD CONSTRAINT pk_sale_items PRIMARY KEY (sale_item_id);
ALTER TABLE stock_movements ADD CONSTRAINT pk_stock_movements PRIMARY KEY (movement_id);
ALTER TABLE stock_reconciliation ADD CONSTRAINT pk_stock_reconciliation PRIMARY KEY (recon_id);
ALTER TABLE complaints ADD CONSTRAINT pk_complaints PRIMARY KEY (complaint_id);
ALTER TABLE complaint_history ADD CONSTRAINT pk_complaint_history PRIMARY KEY (history_id);
ALTER TABLE users ADD CONSTRAINT pk_users PRIMARY KEY (user_id);
ALTER TABLE roles ADD CONSTRAINT pk_roles PRIMARY KEY (role_id);
ALTER TABLE user_roles ADD CONSTRAINT pk_user_roles PRIMARY KEY (user_id, role_id);
ALTER TABLE audit_logs ADD CONSTRAINT pk_audit_logs PRIMARY KEY (log_id);
ALTER TABLE erp_staging_movements ADD CONSTRAINT pk_erp_staging PRIMARY KEY (staging_id);

PROMPT Creating Unique Constraints...;

ALTER TABLE card_types ADD CONSTRAINT uq_card_types_code UNIQUE (type_code);
ALTER TABLE commodities ADD CONSTRAINT uq_commodities_code UNIQUE (commodity_code);
ALTER TABLE godowns ADD CONSTRAINT uq_godowns_code UNIQUE (godown_code);
ALTER TABLE godown_stock ADD CONSTRAINT uq_godown_commodity UNIQUE (godown_id, commodity_id);
ALTER TABLE shops ADD CONSTRAINT uq_shops_code UNIQUE (shop_code);
ALTER TABLE shops ADD CONSTRAINT uq_shops_license UNIQUE (license_number);
ALTER TABLE card_holders ADD CONSTRAINT uq_card_number UNIQUE (card_number);
ALTER TABLE card_holders ADD CONSTRAINT uq_card_aadhaar UNIQUE (aadhaar_hash);
ALTER TABLE entitlements ADD CONSTRAINT uq_card_monthly_entitlement UNIQUE (card_id, commodity_id, allotment_year, allotment_month);
ALTER TABLE allotments ADD CONSTRAINT uq_shop_monthly_allotment UNIQUE (shop_id, commodity_id, allotment_year, allotment_month);
ALTER TABLE shop_stock ADD CONSTRAINT uq_shop_commodity UNIQUE (shop_id, commodity_id);
ALTER TABLE dispatches ADD CONSTRAINT uq_dispatch_number UNIQUE (dispatch_number);
ALTER TABLE dispatch_items ADD CONSTRAINT uq_dispatch_commodity UNIQUE (dispatch_id, commodity_id);
ALTER TABLE sales ADD CONSTRAINT uq_sales_bill_number UNIQUE (bill_number);
ALTER TABLE sale_items ADD CONSTRAINT uq_sale_commodity UNIQUE (sale_id, commodity_id);
ALTER TABLE complaints ADD CONSTRAINT uq_complaint_ref UNIQUE (complaint_ref);
ALTER TABLE users ADD CONSTRAINT uq_users_username UNIQUE (username);
ALTER TABLE users ADD CONSTRAINT uq_users_email UNIQUE (email);
ALTER TABLE roles ADD CONSTRAINT uq_roles_name UNIQUE (role_name);

PROMPT Creating Check Constraints...;

ALTER TABLE card_types ADD CONSTRAINT chk_card_types_active CHECK (is_active IN ('Y', 'N'));
ALTER TABLE commodities ADD CONSTRAINT chk_commodities_active CHECK (is_active IN ('Y', 'N'));
ALTER TABLE commodities ADD CONSTRAINT chk_commodities_rate CHECK (consumer_rate_per_unit >= 0);
ALTER TABLE commodities ADD CONSTRAINT chk_commodities_subsidy CHECK (government_subsidy_per_unit >= 0);
ALTER TABLE commodities ADD CONSTRAINT chk_commodities_buffer CHECK (buffer_stock_threshold >= 0);
ALTER TABLE godowns ADD CONSTRAINT chk_godowns_active CHECK (is_active IN ('Y', 'N'));
ALTER TABLE godown_stock ADD CONSTRAINT chk_godown_stock_qty CHECK (current_quantity >= 0);
ALTER TABLE godown_stock ADD CONSTRAINT chk_godown_stock_res CHECK (reserved_quantity >= 0);
ALTER TABLE shops ADD CONSTRAINT chk_shops_status CHECK (shop_status IN ('ACTIVE', 'SUSPENDED', 'REVOKED'));
ALTER TABLE card_holders ADD CONSTRAINT chk_card_status CHECK (card_status IN ('ACTIVE', 'SUSPENDED', 'CANCELLED', 'SURRENDERED'));
ALTER TABLE card_holders ADD CONSTRAINT chk_card_members CHECK (total_family_members >= 1);
ALTER TABLE family_members ADD CONSTRAINT chk_family_gender CHECK (gender IN ('M', 'F', 'T'));
ALTER TABLE family_members ADD CONSTRAINT chk_family_active CHECK (is_active IN ('Y', 'N'));
ALTER TABLE entitlement_rules ADD CONSTRAINT chk_rule_method CHECK (calc_method IN ('FIXED_PER_CARD', 'PER_MEMBER', 'PER_MEMBER_WITH_CEILING', 'SLAB_BASED'));
ALTER TABLE entitlement_rules ADD CONSTRAINT chk_rule_active CHECK (is_active IN ('Y', 'N'));
ALTER TABLE entitlements ADD CONSTRAINT chk_ent_month CHECK (allotment_month BETWEEN 1 AND 12);
ALTER TABLE entitlements ADD CONSTRAINT chk_ent_qty CHECK (entitled_quantity >= 0 AND distributed_quantity >= 0 AND remaining_quantity >= 0);
ALTER TABLE entitlements ADD CONSTRAINT chk_ent_status CHECK (status IN ('AVAILABLE', 'PARTIALLY_DRAWN', 'EXHAUSTED', 'LAPSED'));
ALTER TABLE allotments ADD CONSTRAINT chk_allot_month CHECK (allotment_month BETWEEN 1 AND 12);
ALTER TABLE allotments ADD CONSTRAINT chk_allot_status CHECK (allotment_status IN ('DRAFT', 'APPROVED', 'PARTIALLY_DISPATCHED', 'COMPLETED', 'CANCELLED'));
ALTER TABLE allotments ADD CONSTRAINT chk_allot_qty CHECK (allocated_quantity >= 0 AND dispatched_quantity >= 0 AND received_quantity >= 0);
ALTER TABLE shop_stock ADD CONSTRAINT chk_shop_stock_qty CHECK (current_quantity >= 0);
ALTER TABLE shop_stock ADD CONSTRAINT chk_shop_stock_totals CHECK (opening_month_balance >= 0 AND total_received_month >= 0 AND total_distributed_month >= 0);
ALTER TABLE dispatches ADD CONSTRAINT chk_dispatch_status CHECK (dispatch_status IN ('PLANNED', 'DISPATCHED', 'IN_TRANSIT', 'RECEIVED', 'REJECTED'));
ALTER TABLE dispatch_items ADD CONSTRAINT chk_dispatch_qty CHECK (dispatched_quantity > 0);
ALTER TABLE sales ADD CONSTRAINT chk_sales_month CHECK (sale_month BETWEEN 1 AND 12);
ALTER TABLE sales ADD CONSTRAINT chk_sales_paymode CHECK (payment_mode IN ('CASH', 'UPI', 'EXEMPTED'));
ALTER TABLE sales ADD CONSTRAINT chk_sales_authmode CHECK (auth_mode IN ('AADHAAR_OTP', 'BIOMETRIC_IRIS', 'BIOMETRIC_FINGER', 'MANUAL_OVERRIDE'));
ALTER TABLE sales ADD CONSTRAINT chk_sales_status CHECK (sale_status IN ('COMPLETED', 'VOIDED'));
ALTER TABLE sale_items ADD CONSTRAINT chk_sale_items_qty CHECK (quantity > 0);
ALTER TABLE stock_movements ADD CONSTRAINT chk_mv_type CHECK (movement_type IN ('GODOWN_INWARD', 'GODOWN_DISPATCH', 'SHOP_RECEIPT', 'SALE', 'PHYSICAL_ADJUSTMENT', 'RETURN_TO_DEPOT'));
ALTER TABLE stock_movements ADD CONSTRAINT chk_sm_source_type CHECK (source_type IS NULL OR source_type IN ('GODOWN', 'SHOP', 'SUPPLIER', 'ADJUSTMENT'));
ALTER TABLE stock_movements ADD CONSTRAINT chk_sm_dest_type CHECK (dest_type IS NULL OR dest_type IN ('GODOWN', 'SHOP', 'CARDHOLDER', 'ADJUSTMENT'));
ALTER TABLE stock_reconciliation ADD CONSTRAINT chk_recon_status CHECK (investigation_status IN ('MATCHED', 'PENDING_REVIEW', 'INVESTIGATION_OPENED', 'JUSTIFIED_LOSS', 'RECONCILED'));
ALTER TABLE complaints ADD CONSTRAINT chk_complaint_status CHECK (status IN ('REGISTERED', 'ASSIGNED', 'IN_INVESTIGATION', 'RESOLVED', 'REJECTED'));
ALTER TABLE complaints ADD CONSTRAINT chk_complaint_prio CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL'));
ALTER TABLE complaints ADD CONSTRAINT chk_complaint_category CHECK (category IN ('QUANTITY_SHORTAGE', 'STOCK_UNAVAILABLE', 'DELAYED_OPENING', 'INCORRECT_ENTITLEMENT', 'SHOP_BEHAVIOR', 'QUALITY_ISSUE', 'OTHER'));
ALTER TABLE complaints ADD CONSTRAINT chk_complaint_lang CHECK (language IN ('en', 'ta'));
ALTER TABLE users ADD CONSTRAINT chk_users_active CHECK (is_active IN ('Y', 'N'));
ALTER TABLE audit_logs ADD CONSTRAINT chk_audit_action CHECK (action_type IN ('INSERT', 'UPDATE', 'DELETE', 'SECURITY_LOGIN', 'OVERRIDE'));
ALTER TABLE erp_staging_movements ADD CONSTRAINT chk_erp_status CHECK (process_status IN ('PENDING', 'PROCESSED', 'ERROR'));

PROMPT Creating Foreign Keys...;

ALTER TABLE godown_stock ADD CONSTRAINT fk_godown_stock_godown FOREIGN KEY (godown_id) REFERENCES godowns (godown_id);
ALTER TABLE godown_stock ADD CONSTRAINT fk_godown_stock_comm FOREIGN KEY (commodity_id) REFERENCES commodities (commodity_id);

ALTER TABLE card_holders ADD CONSTRAINT fk_card_type FOREIGN KEY (card_type_id) REFERENCES card_types (card_type_id);
ALTER TABLE card_holders ADD CONSTRAINT fk_card_shop FOREIGN KEY (assigned_shop_id) REFERENCES shops (shop_id);

ALTER TABLE family_members ADD CONSTRAINT fk_family_card FOREIGN KEY (card_id) REFERENCES card_holders (card_id) ON DELETE CASCADE;

ALTER TABLE entitlement_rules ADD CONSTRAINT fk_rule_card_type FOREIGN KEY (card_type_id) REFERENCES card_types (card_type_id);
ALTER TABLE entitlement_rules ADD CONSTRAINT fk_rule_commodity FOREIGN KEY (commodity_id) REFERENCES commodities (commodity_id);

ALTER TABLE entitlements ADD CONSTRAINT fk_ent_card FOREIGN KEY (card_id) REFERENCES card_holders (card_id);
ALTER TABLE entitlements ADD CONSTRAINT fk_ent_commodity FOREIGN KEY (commodity_id) REFERENCES commodities (commodity_id);

ALTER TABLE allotments ADD CONSTRAINT fk_allot_shop FOREIGN KEY (shop_id) REFERENCES shops (shop_id);
ALTER TABLE allotments ADD CONSTRAINT fk_allot_commodity FOREIGN KEY (commodity_id) REFERENCES commodities (commodity_id);

ALTER TABLE shop_stock ADD CONSTRAINT fk_shop_stock_shop FOREIGN KEY (shop_id) REFERENCES shops (shop_id);
ALTER TABLE shop_stock ADD CONSTRAINT fk_shop_stock_commodity FOREIGN KEY (commodity_id) REFERENCES commodities (commodity_id);

ALTER TABLE dispatches ADD CONSTRAINT fk_disp_godown FOREIGN KEY (godown_id) REFERENCES godowns (godown_id);
ALTER TABLE dispatches ADD CONSTRAINT fk_disp_shop FOREIGN KEY (shop_id) REFERENCES shops (shop_id);

ALTER TABLE dispatch_items ADD CONSTRAINT fk_disp_item_dispatch FOREIGN KEY (dispatch_id) REFERENCES dispatches (dispatch_id) ON DELETE CASCADE;
ALTER TABLE dispatch_items ADD CONSTRAINT fk_disp_item_commodity FOREIGN KEY (commodity_id) REFERENCES commodities (commodity_id);

ALTER TABLE sales ADD CONSTRAINT fk_sales_shop FOREIGN KEY (shop_id) REFERENCES shops (shop_id);
ALTER TABLE sales ADD CONSTRAINT fk_sales_card FOREIGN KEY (card_id) REFERENCES card_holders (card_id);

ALTER TABLE sale_items ADD CONSTRAINT fk_sale_items_sale FOREIGN KEY (sale_id) REFERENCES sales (sale_id) ON DELETE CASCADE;
ALTER TABLE sale_items ADD CONSTRAINT fk_sale_items_commodity FOREIGN KEY (commodity_id) REFERENCES commodities (commodity_id);

ALTER TABLE stock_movements ADD CONSTRAINT fk_sm_commodity FOREIGN KEY (commodity_id) REFERENCES commodities (commodity_id);

ALTER TABLE stock_reconciliation ADD CONSTRAINT fk_recon_shop FOREIGN KEY (shop_id) REFERENCES shops (shop_id);
ALTER TABLE stock_reconciliation ADD CONSTRAINT fk_recon_commodity FOREIGN KEY (commodity_id) REFERENCES commodities (commodity_id);

ALTER TABLE complaints ADD CONSTRAINT fk_complaint_card FOREIGN KEY (card_id) REFERENCES card_holders (card_id);
ALTER TABLE complaints ADD CONSTRAINT fk_complaint_shop FOREIGN KEY (shop_id) REFERENCES shops (shop_id);
ALTER TABLE complaints ADD CONSTRAINT fk_complaint_officer FOREIGN KEY (assigned_officer_id) REFERENCES users (user_id);

ALTER TABLE complaint_history ADD CONSTRAINT fk_ch_complaint FOREIGN KEY (complaint_id) REFERENCES complaints (complaint_id) ON DELETE CASCADE;

ALTER TABLE sales ADD CONSTRAINT fk_sales_operator FOREIGN KEY (operator_user_id) REFERENCES users (user_id);
ALTER TABLE allotments ADD CONSTRAINT fk_allot_officer FOREIGN KEY (approved_by_user_id) REFERENCES users (user_id);
ALTER TABLE stock_reconciliation ADD CONSTRAINT fk_recon_officer FOREIGN KEY (inspecting_officer_id) REFERENCES users (user_id);

ALTER TABLE users ADD CONSTRAINT fk_users_shop FOREIGN KEY (assigned_shop_id) REFERENCES shops (shop_id);
ALTER TABLE users ADD CONSTRAINT fk_users_godown FOREIGN KEY (assigned_godown_id) REFERENCES godowns (godown_id);

ALTER TABLE user_roles ADD CONSTRAINT fk_ur_user FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE;
ALTER TABLE user_roles ADD CONSTRAINT fk_ur_role FOREIGN KEY (role_id) REFERENCES roles (role_id) ON DELETE CASCADE;

ALTER TABLE erp_staging_movements ADD CONSTRAINT fk_erp_movement FOREIGN KEY (movement_id) REFERENCES stock_movements (movement_id);

PROMPT Creating Performance Indexes...;

CREATE INDEX idx_card_holders_shop ON card_holders (assigned_shop_id, card_status);
CREATE INDEX idx_family_members_card ON family_members (card_id, is_active);
CREATE INDEX idx_entitlement_rules_lookup ON entitlement_rules (card_type_id, commodity_id, is_active);
CREATE INDEX idx_entitlements_lookup ON entitlements (card_id, allotment_year, allotment_month);
CREATE INDEX idx_allotments_shop_period ON allotments (shop_id, allotment_year, allotment_month);
CREATE INDEX idx_sales_shop_date ON sales (shop_id, sale_date);
CREATE INDEX idx_sales_card_period ON sales (card_id, sale_year, sale_month);
CREATE INDEX idx_sale_items_sale ON sale_items (sale_id);
CREATE INDEX idx_shop_stock_current ON shop_stock (shop_id, current_quantity);
CREATE INDEX idx_godown_stock_current ON godown_stock (godown_id, current_quantity);
CREATE INDEX idx_dispatches_shop_status ON dispatches (shop_id, dispatch_status);
CREATE INDEX idx_sm_ref ON stock_movements (reference_doc_type, reference_doc_id);
CREATE INDEX idx_sm_date ON stock_movements (movement_timestamp);
CREATE INDEX idx_complaints_shop_status ON complaints (shop_id, status);
CREATE INDEX idx_recon_shop_date ON stock_reconciliation (shop_id, recon_date);
CREATE INDEX idx_audit_entity ON audit_logs (entity_name, entity_id);

PROMPT Constraints and indexes successfully created.
