-- ====================================================================
-- PDS RATION SHOP STOCK & ENTITLEMENT SYSTEM
-- SCRIPT 09: SAMPLE DEMONSTRATION DATA (ORACLE SQL)
-- Target: Oracle Database 19c / 21c / 23c
-- ====================================================================

SET SERVEROUTPUT ON;
PROMPT Inserting Sample Demonstration Data...;

-- 1. CARD_TYPES
INSERT INTO card_types (card_type_id, type_code, type_name, description, color_code, is_active)
VALUES (1, 'AAY', 'Antyodaya Anna Yojana', 'Poorest of the poor; maximum subsidized grain quota', 'YELLOW', 'Y');
INSERT INTO card_types (card_type_id, type_code, type_name, description, color_code, is_active)
VALUES (2, 'PHH', 'Priority Household', 'Per-member quota guaranteeing nutritional security', 'PINK', 'Y');
INSERT INTO card_types (card_type_id, type_code, type_name, description, color_code, is_active)
VALUES (3, 'NPHH', 'Non-Priority Household', 'Standard subsidized food grains based on availability', 'WHITE', 'Y');
INSERT INTO card_types (card_type_id, type_code, type_name, description, color_code, is_active)
VALUES (4, 'AY', 'Annapurna Yojana', 'Destitute senior citizens (65+ years) welfare allocation', 'GREEN', 'Y');

-- 2. COMMODITIES
INSERT INTO commodities (commodity_id, commodity_code, commodity_name, measurement_unit, consumer_rate_per_unit, government_subsidy_per_unit, buffer_stock_threshold, is_active)
VALUES (1, 'RICE_COMMON', 'Common Rice (PDS)', 'KG', 0.00, 34.50, 500.00, 'Y');
INSERT INTO commodities (commodity_id, commodity_code, commodity_name, measurement_unit, consumer_rate_per_unit, government_subsidy_per_unit, buffer_stock_threshold, is_active)
VALUES (2, 'WHEAT', 'Whole Wheat Grain', 'KG', 2.00, 26.80, 300.00, 'Y');
INSERT INTO commodities (commodity_id, commodity_code, commodity_name, measurement_unit, consumer_rate_per_unit, government_subsidy_per_unit, buffer_stock_threshold, is_active)
VALUES (3, 'SUGAR', 'Refined Cane Sugar', 'KG', 13.50, 24.00, 150.00, 'Y');
INSERT INTO commodities (commodity_id, commodity_code, commodity_name, measurement_unit, consumer_rate_per_unit, government_subsidy_per_unit, buffer_stock_threshold, is_active)
VALUES (4, 'PALM_OIL', 'Fortified Edible Palm Oil', 'PACKET', 25.00, 65.00, 100.00, 'Y');
INSERT INTO commodities (commodity_id, commodity_code, commodity_name, measurement_unit, consumer_rate_per_unit, government_subsidy_per_unit, buffer_stock_threshold, is_active)
VALUES (5, 'TOOR_DAL', 'Split Pigeon Pea (Toor Dal)', 'KG', 30.00, 85.00, 150.00, 'Y');
INSERT INTO commodities (commodity_id, commodity_code, commodity_name, measurement_unit, consumer_rate_per_unit, government_subsidy_per_unit, buffer_stock_threshold, is_active)
VALUES (6, 'KEROSENE', 'Subsidized Blue Kerosene', 'LITRE', 15.00, 38.00, 80.00, 'Y');

-- 3. GODOWNS
INSERT INTO godowns (godown_id, godown_code, godown_name, taluk_name, storage_capacity_mt, manager_name, contact_number, address, is_active)
VALUES (1, 'GDN-TLK-01', 'Taluk Central Civil Supplies Depot', 'Tambaram Taluk', 2500.00, 'K. Senthil Nathan', '9840112233', 'Station Road, Depot Complex, Tambaram, Chennai 600045', 'Y');
INSERT INTO godowns (godown_id, godown_code, godown_name, taluk_name, storage_capacity_mt, manager_name, contact_number, address, is_active)
VALUES (2, 'GDN-TLK-02', 'Sub-Taluk Grain Storage Warehouse', 'Tambaram Taluk', 1200.00, 'P. Murugesan', '9840445566', 'GST Road, Chromepet Depot, Chennai 600044', 'Y');

-- 4. GODOWN_STOCK
INSERT INTO godown_stock (godown_stock_id, godown_id, commodity_id, current_quantity, reserved_quantity, last_replenished_date)
VALUES (1, 1, 1, 125000.00, 15000.00, TRUNC(SYSDATE - 10));
INSERT INTO godown_stock (godown_stock_id, godown_id, commodity_id, current_quantity, reserved_quantity, last_replenished_date)
VALUES (2, 1, 2, 45000.00, 5000.00, TRUNC(SYSDATE - 12));
INSERT INTO godown_stock (godown_stock_id, godown_id, commodity_id, current_quantity, reserved_quantity, last_replenished_date)
VALUES (3, 1, 3, 18000.00, 2000.00, TRUNC(SYSDATE - 8));
INSERT INTO godown_stock (godown_stock_id, godown_id, commodity_id, current_quantity, reserved_quantity, last_replenished_date)
VALUES (4, 1, 4, 12000.00, 1200.00, TRUNC(SYSDATE - 5));
INSERT INTO godown_stock (godown_stock_id, godown_id, commodity_id, current_quantity, reserved_quantity, last_replenished_date)
VALUES (5, 1, 5, 15000.00, 1500.00, TRUNC(SYSDATE - 6));
INSERT INTO godown_stock (godown_stock_id, godown_id, commodity_id, current_quantity, reserved_quantity, last_replenished_date)
VALUES (6, 1, 6, 8000.00, 1000.00, TRUNC(SYSDATE - 15));

-- 5. SHOPS (Fair-Price Shops representing 120-shop Taluk scale)
INSERT INTO shops (shop_id, shop_code, shop_name, license_number, operator_name, contact_number, taluk_name, ward_village, address, latitude, longitude, shop_status)
VALUES (1, 'FPS-TLK-001', 'Fair Price Shop #1 - West Tambaram', 'FPS/LIC/TN/001', 'A. Velmurugan', '9884100001', 'Tambaram Taluk', 'Ward 12', '14 Shanmugam Road, West Tambaram', 12.9249, 80.1000, 'ACTIVE');
INSERT INTO shops (shop_id, shop_code, shop_name, license_number, operator_name, contact_number, taluk_name, ward_village, address, latitude, longitude, shop_status)
VALUES (2, 'FPS-TLK-002', 'Fair Price Shop #2 - East Tambaram', 'FPS/LIC/TN/002', 'M. Saravanan', '9884100002', 'Tambaram Taluk', 'Ward 15', '22 Rajaji Salai, East Tambaram', 12.9220, 80.1250, 'ACTIVE');
INSERT INTO shops (shop_id, shop_code, shop_name, license_number, operator_name, contact_number, taluk_name, ward_village, address, latitude, longitude, shop_status)
VALUES (3, 'FPS-TLK-003', 'Fair Price Shop #3 - Chromepet Main', 'FPS/LIC/TN/003', 'V. Elango', '9884100003', 'Tambaram Taluk', 'Ward 21', '8 Radhanagar Main Road, Chromepet', 12.9516, 80.1462, 'ACTIVE');
INSERT INTO shops (shop_id, shop_code, shop_name, license_number, operator_name, contact_number, taluk_name, ward_village, address, latitude, longitude, shop_status)
VALUES (4, 'FPS-TLK-004', 'Fair Price Shop #4 - Sanatorium Colony', 'FPS/LIC/TN/004', 'K. Deepa', '9884100004', 'Tambaram Taluk', 'Ward 18', '5 Gandhi Street, Tambaram Sanatorium', 12.9410, 80.1320, 'ACTIVE');
INSERT INTO shops (shop_id, shop_code, shop_name, license_number, operator_name, contact_number, taluk_name, ward_village, address, latitude, longitude, shop_status)
VALUES (5, 'FPS-TLK-005', 'Fair Price Shop #5 - Mudichur Village', 'FPS/LIC/TN/005', 'R. Ganesan', '9884100005', 'Tambaram Taluk', 'Mudichur Rural', '19 High Road, Mudichur', 12.9100, 80.0600, 'ACTIVE');
INSERT INTO shops (shop_id, shop_code, shop_name, license_number, operator_name, contact_number, taluk_name, ward_village, address, latitude, longitude, shop_status)
VALUES (6, 'FPS-TLK-006', 'Fair Price Shop #6 - Selaiyur North', 'FPS/LIC/TN/006', 'S. Anbarasan', '9884100006', 'Tambaram Taluk', 'Ward 28', '40 Camp Road Junction, Selaiyur', 12.9160, 80.1450, 'ACTIVE');
INSERT INTO shops (shop_id, shop_code, shop_name, license_number, operator_name, contact_number, taluk_name, ward_village, address, latitude, longitude, shop_status)
VALUES (7, 'FPS-TLK-007', 'Fair Price Shop #7 - Chitlapakkam', 'FPS/LIC/TN/007', 'T. Kalaiselvi', '9884100007', 'Tambaram Taluk', 'Ward 31', '3 Nehru Street, Chitlapakkam', 12.9450, 80.1500, 'ACTIVE');
INSERT INTO shops (shop_id, shop_code, shop_name, license_number, operator_name, contact_number, taluk_name, ward_village, address, latitude, longitude, shop_status)
VALUES (8, 'FPS-TLK-008', 'Fair Price Shop #8 - Peerkankaranai', 'FPS/LIC/TN/008', 'G. Ramesh', '9884100008', 'Tambaram Taluk', 'Ward 24', '12 Kamaraj Salai, Peerkankaranai', 12.9050, 80.0800, 'ACTIVE');
INSERT INTO shops (shop_id, shop_code, shop_name, license_number, operator_name, contact_number, taluk_name, ward_village, address, latitude, longitude, shop_status)
VALUES (9, 'FPS-TLK-009', 'Fair Price Shop #9 - Irumbuliyur', 'FPS/LIC/TN/009', 'N. Thangavel', '9884100009', 'Tambaram Taluk', 'Ward 10', '7 Bypass Service Lane, Irumbuliyur', 12.9150, 80.1100, 'ACTIVE');
INSERT INTO shops (shop_id, shop_code, shop_name, license_number, operator_name, contact_number, taluk_name, ward_village, address, latitude, longitude, shop_status)
VALUES (10, 'FPS-TLK-010', 'Fair Price Shop #10 - Pallavaram Border', 'FPS/LIC/TN/010', 'D. Manjula', '9884100010', 'Tambaram Taluk', 'Ward 04', '16 Old Trunk Road, Pallavaram', 12.9650, 80.1550, 'ACTIVE');

-- 6. USERS, ROLES & USER_ROLES (Inserted before dependent transactions)
INSERT INTO roles (role_id, role_name, description) VALUES (1, 'ROLE_ADMIN', 'State Civil Supplies System Administrator');
INSERT INTO roles (role_id, role_name, description) VALUES (2, 'ROLE_OFFICER', 'Taluk Supply Officer / Enforcement Inspector');
INSERT INTO roles (role_id, role_name, description) VALUES (3, 'ROLE_GODOWN_MGR', 'Central Buffer Warehouse / Godown In-Charge');
INSERT INTO roles (role_id, role_name, description) VALUES (4, 'ROLE_SHOPKEEPER', 'Fair-Price Shop Authorized Operator');

INSERT INTO users (user_id, username, password_hash, full_name, email, phone, taluk_name, is_active)
VALUES (1, 'admin', '$2a$10$7Z2p8Eln1mG2yC6n0/Yh2eN1vH7y1zV9G6qJmP5rY8iB3tD4sK9m6', 'PDS State Administrator', 'admin@pds.gov.in', '9840000001', 'State Directorate', 'Y');
INSERT INTO users (user_id, username, password_hash, full_name, email, phone, taluk_name, is_active)
VALUES (2, 'officer1', '$2a$10$7Z2p8Eln1mG2yC6n0/Yh2eN1vH7y1zV9G6qJmP5rY8iB3tD4sK9m6', 'S. Ramanathan (TSO)', 'officer.tambaram@pds.gov.in', '9840000002', 'Tambaram Taluk', 'Y');
INSERT INTO users (user_id, username, password_hash, full_name, email, phone, taluk_name, assigned_godown_id, is_active)
VALUES (3, 'godown1', '$2a$10$7Z2p8Eln1mG2yC6n0/Yh2eN1vH7y1zV9G6qJmP5rY8iB3tD4sK9m6', 'K. Senthil Nathan', 'godown.central@pds.gov.in', '9840112233', 'Tambaram Taluk', 1, 'Y');
INSERT INTO users (user_id, username, password_hash, full_name, email, phone, taluk_name, assigned_shop_id, is_active)
VALUES (4, 'shopkeeper1', '$2a$10$7Z2p8Eln1mG2yC6n0/Yh2eN1vH7y1zV9G6qJmP5rY8iB3tD4sK9m6', 'A. Velmurugan', 'fps001@pds.gov.in', '9884100001', 'Tambaram Taluk', 1, 'Y');
INSERT INTO users (user_id, username, password_hash, full_name, email, phone, taluk_name, assigned_shop_id, is_active)
VALUES (5, 'shopkeeper2', '$2a$10$7Z2p8Eln1mG2yC6n0/Yh2eN1vH7y1zV9G6qJmP5rY8iB3tD4sK9m6', 'M. Saravanan', 'fps002@pds.gov.in', '9884100002', 'Tambaram Taluk', 2, 'Y');

INSERT INTO user_roles (user_id, role_id) VALUES (1, 1);
INSERT INTO user_roles (user_id, role_id) VALUES (2, 2);
INSERT INTO user_roles (user_id, role_id) VALUES (3, 3);
INSERT INTO user_roles (user_id, role_id) VALUES (4, 4);
INSERT INTO user_roles (user_id, role_id) VALUES (5, 4);

-- 7. ENTITLEMENT_RULES
-- AAY (Antyodaya): Fixed 35 kg Rice per card, 1 kg Sugar, 1 Litre Oil, 1 kg Dal
INSERT INTO entitlement_rules (rule_id, card_type_id, commodity_id, calc_method, base_quantity, per_member_quantity, min_family_size, max_quantity_ceiling, effective_start_date, is_active)
VALUES (1, 1, 1, 'FIXED_PER_CARD', 35.00, 0.00, 1, 35.00, DATE '2025-01-01', 'Y');
INSERT INTO entitlement_rules (rule_id, card_type_id, commodity_id, calc_method, base_quantity, per_member_quantity, min_family_size, max_quantity_ceiling, effective_start_date, is_active)
VALUES (2, 1, 3, 'FIXED_PER_CARD', 1.00, 0.00, 1, 1.00, DATE '2025-01-01', 'Y');
INSERT INTO entitlement_rules (rule_id, card_type_id, commodity_id, calc_method, base_quantity, per_member_quantity, min_family_size, max_quantity_ceiling, effective_start_date, is_active)
VALUES (3, 1, 4, 'FIXED_PER_CARD', 1.00, 0.00, 1, 1.00, DATE '2025-01-01', 'Y');
INSERT INTO entitlement_rules (rule_id, card_type_id, commodity_id, calc_method, base_quantity, per_member_quantity, min_family_size, max_quantity_ceiling, effective_start_date, is_active)
VALUES (4, 1, 5, 'FIXED_PER_CARD', 1.00, 0.00, 1, 1.00, DATE '2025-01-01', 'Y');

-- PHH (Priority Household): 5 kg Rice per member with statutory min 12kg (base) and ceiling 20kg; 5kg Wheat; 1kg Sugar; 1 pkt Oil; 1kg Dal
INSERT INTO entitlement_rules (rule_id, card_type_id, commodity_id, calc_method, base_quantity, per_member_quantity, min_family_size, max_quantity_ceiling, effective_start_date, is_active)
VALUES (5, 2, 1, 'PER_MEMBER_WITH_CEILING', 12.00, 5.00, 1, 20.00, DATE '2025-01-01', 'Y');
INSERT INTO entitlement_rules (rule_id, card_type_id, commodity_id, calc_method, base_quantity, per_member_quantity, min_family_size, max_quantity_ceiling, effective_start_date, is_active)
VALUES (6, 2, 2, 'FIXED_PER_CARD', 5.00, 0.00, 1, 5.00, DATE '2025-01-01', 'Y');
INSERT INTO entitlement_rules (rule_id, card_type_id, commodity_id, calc_method, base_quantity, per_member_quantity, min_family_size, max_quantity_ceiling, effective_start_date, is_active)
VALUES (7, 2, 3, 'PER_MEMBER', 0.50, 0.25, 1, 2.00, DATE '2025-01-01', 'Y');
INSERT INTO entitlement_rules (rule_id, card_type_id, commodity_id, calc_method, base_quantity, per_member_quantity, min_family_size, max_quantity_ceiling, effective_start_date, is_active)
VALUES (8, 2, 4, 'FIXED_PER_CARD', 1.00, 0.00, 1, 1.00, DATE '2025-01-01', 'Y');
INSERT INTO entitlement_rules (rule_id, card_type_id, commodity_id, calc_method, base_quantity, per_member_quantity, min_family_size, max_quantity_ceiling, effective_start_date, is_active)
VALUES (9, 2, 5, 'FIXED_PER_CARD', 1.00, 0.00, 1, 1.00, DATE '2025-01-01', 'Y');

-- NPHH (Non-Priority): 5 kg Rice base + 3kg per member up to 20kg; 5kg Wheat; 0.5kg Sugar
INSERT INTO entitlement_rules (rule_id, card_type_id, commodity_id, calc_method, base_quantity, per_member_quantity, min_family_size, max_quantity_ceiling, effective_start_date, is_active)
VALUES (10, 3, 1, 'PER_MEMBER_WITH_CEILING', 5.00, 3.00, 1, 20.00, DATE '2025-01-01', 'Y');
INSERT INTO entitlement_rules (rule_id, card_type_id, commodity_id, calc_method, base_quantity, per_member_quantity, min_family_size, max_quantity_ceiling, effective_start_date, is_active)
VALUES (11, 3, 2, 'FIXED_PER_CARD', 5.00, 0.00, 1, 5.00, DATE '2025-01-01', 'Y');
INSERT INTO entitlement_rules (rule_id, card_type_id, commodity_id, calc_method, base_quantity, per_member_quantity, min_family_size, max_quantity_ceiling, effective_start_date, is_active)
VALUES (12, 3, 3, 'FIXED_PER_CARD', 0.50, 0.00, 1, 1.00, DATE '2025-01-01', 'Y');

-- AY (Annapurna): Fixed 10 kg Rice free
INSERT INTO entitlement_rules (rule_id, card_type_id, commodity_id, calc_method, base_quantity, per_member_quantity, min_family_size, max_quantity_ceiling, effective_start_date, is_active)
VALUES (13, 4, 1, 'FIXED_PER_CARD', 10.00, 0.00, 1, 10.00, DATE '2025-01-01', 'Y');

-- 8. CARD_HOLDERS (Fictional Demonstration Records, initial base size = 1)
INSERT INTO card_holders (card_id, card_number, card_type_id, head_of_family_name, aadhaar_hash, aadhaar_last4, mobile_number, address_line1, taluk_name, district_name, pincode, assigned_shop_id, total_family_members, annual_income, card_status)
VALUES (1001, 'RC-TN-04-100234', 1, 'M. Subramanian', 'e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855', '3412', '9840900001', '12 Gandhi Nagar, West Tambaram', 'Tambaram Taluk', 'Chengalpattu', '600045', 1, 1, 36000.00, 'ACTIVE');
INSERT INTO card_holders (card_id, card_number, card_type_id, head_of_family_name, aadhaar_hash, aadhaar_last4, mobile_number, address_line1, taluk_name, district_name, pincode, assigned_shop_id, total_family_members, annual_income, card_status)
VALUES (1002, 'RC-TN-04-100235', 2, 'K. Revathi', 'b5a2c94298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b866', '7890', '9840900002', '45 Station Road, West Tambaram', 'Tambaram Taluk', 'Chengalpattu', '600045', 1, 1, 72000.00, 'ACTIVE');
INSERT INTO card_holders (card_id, card_number, card_type_id, head_of_family_name, aadhaar_hash, aadhaar_last4, mobile_number, address_line1, taluk_name, district_name, pincode, assigned_shop_id, total_family_members, annual_income, card_status)
VALUES (1003, 'RC-TN-04-100236', 3, 'S. Vijayakumar', 'c7d3e44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b877', '1122', '9840900003', '78 Rajaji Street, West Tambaram', 'Tambaram Taluk', 'Chengalpattu', '600045', 1, 1, 180000.00, 'ACTIVE');
INSERT INTO card_holders (card_id, card_number, card_type_id, head_of_family_name, aadhaar_hash, aadhaar_last4, mobile_number, address_line1, taluk_name, district_name, pincode, assigned_shop_id, total_family_members, annual_income, card_status)
VALUES (1004, 'RC-TN-04-100237', 4, 'P. Meenakshi Ammal', 'd8e4f54298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b888', '5566', '9840900004', '3 Temple Street, West Tambaram', 'Tambaram Taluk', 'Chengalpattu', '600045', 1, 1, 12000.00, 'ACTIVE');
INSERT INTO card_holders (card_id, card_number, card_type_id, head_of_family_name, aadhaar_hash, aadhaar_last4, mobile_number, address_line1, taluk_name, district_name, pincode, assigned_shop_id, total_family_members, annual_income, card_status)
VALUES (1005, 'RC-TN-04-100238', 2, 'R. Anandhan', 'e9f5a64298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b899', '9988', '9840900005', '16 Muthurangan Salai, West Tambaram', 'Tambaram Taluk', 'Chengalpattu', '600045', 1, 1, 60000.00, 'ACTIVE');

-- 9. FAMILY_MEMBERS (Trigger automatically increments total_family_members for non-SELF dependents)
INSERT INTO family_members (member_id, card_id, member_name, relationship_to_head, gender, date_of_birth, age, aadhaar_hash, aadhaar_last4, is_active)
VALUES (5001, 1001, 'M. Subramanian', 'SELF', 'M', DATE '1975-04-12', 51, 'h111', '3412', 'Y');
INSERT INTO family_members (member_id, card_id, member_name, relationship_to_head, gender, date_of_birth, age, aadhaar_hash, aadhaar_last4, is_active)
VALUES (5002, 1001, 'S. Lakshmi', 'SPOUSE', 'F', DATE '1980-08-20', 46, 'h112', '3413', 'Y');
INSERT INTO family_members (member_id, card_id, member_name, relationship_to_head, gender, date_of_birth, age, aadhaar_hash, aadhaar_last4, is_active)
VALUES (5003, 1001, 'S. Karthik', 'SON', 'M', DATE '2005-01-15', 21, 'h113', '3414', 'Y');
INSERT INTO family_members (member_id, card_id, member_name, relationship_to_head, gender, date_of_birth, age, aadhaar_hash, aadhaar_last4, is_active)
VALUES (5004, 1001, 'S. Sneha', 'DAUGHTER', 'F', DATE '2008-06-10', 18, 'h114', '3415', 'Y');

INSERT INTO family_members (member_id, card_id, member_name, relationship_to_head, gender, date_of_birth, age, aadhaar_hash, aadhaar_last4, is_active)
VALUES (5005, 1002, 'K. Revathi', 'SELF', 'F', DATE '1982-11-05', 44, 'h221', '7890', 'Y');
INSERT INTO family_members (member_id, card_id, member_name, relationship_to_head, gender, date_of_birth, age, aadhaar_hash, aadhaar_last4, is_active)
VALUES (5006, 1002, 'K. Dinesh', 'SON', 'M', DATE '2004-03-22', 22, 'h222', '7891', 'Y');
INSERT INTO family_members (member_id, card_id, member_name, relationship_to_head, gender, date_of_birth, age, aadhaar_hash, aadhaar_last4, is_active)
VALUES (5007, 1002, 'K. Priya', 'DAUGHTER', 'F', DATE '2007-09-14', 19, 'h223', '7892', 'Y');

INSERT INTO family_members (member_id, card_id, member_name, relationship_to_head, gender, date_of_birth, age, aadhaar_hash, aadhaar_last4, is_active)
VALUES (5008, 1003, 'S. Vijayakumar', 'SELF', 'M', DATE '1978-02-18', 48, 'h331', '1122', 'Y');
INSERT INTO family_members (member_id, card_id, member_name, relationship_to_head, gender, date_of_birth, age, aadhaar_hash, aadhaar_last4, is_active)
VALUES (5009, 1003, 'V. Malathi', 'SPOUSE', 'F', DATE '1983-05-30', 43, 'h332', '1123', 'Y');
INSERT INTO family_members (member_id, card_id, member_name, relationship_to_head, gender, date_of_birth, age, aadhaar_hash, aadhaar_last4, is_active)
VALUES (5010, 1003, 'V. Ashwin', 'SON', 'M', DATE '2010-08-04', 16, 'h333', '1124', 'Y');
INSERT INTO family_members (member_id, card_id, member_name, relationship_to_head, gender, date_of_birth, age, aadhaar_hash, aadhaar_last4, is_active)
VALUES (5011, 1003, 'V. Anitha', 'DAUGHTER', 'F', DATE '2014-12-12', 12, 'h334', '1125', 'Y');

INSERT INTO family_members (member_id, card_id, member_name, relationship_to_head, gender, date_of_birth, age, aadhaar_hash, aadhaar_last4, is_active)
VALUES (5012, 1004, 'P. Meenakshi Ammal', 'SELF', 'F', DATE '1954-07-15', 72, 'h441', '5566', 'Y');

-- 10. SHOP_STOCK (Shop #1 initial live stock)
INSERT INTO shop_stock (stock_id, shop_id, commodity_id, current_quantity, opening_month_balance, total_received_month, total_distributed_month)
VALUES (1, 1, 1, 4850.00, 1200.00, 5000.00, 1350.00);
INSERT INTO shop_stock (stock_id, shop_id, commodity_id, current_quantity, opening_month_balance, total_received_month, total_distributed_month)
VALUES (2, 1, 2, 1200.00, 300.00, 1200.00, 300.00);
INSERT INTO shop_stock (stock_id, shop_id, commodity_id, current_quantity, opening_month_balance, total_received_month, total_distributed_month)
VALUES (3, 1, 3, 420.00, 100.00, 450.00, 130.00);
INSERT INTO shop_stock (stock_id, shop_id, commodity_id, current_quantity, opening_month_balance, total_received_month, total_distributed_month)
VALUES (4, 1, 4, 380.00, 80.00, 400.00, 100.00);
INSERT INTO shop_stock (stock_id, shop_id, commodity_id, current_quantity, opening_month_balance, total_received_month, total_distributed_month)
VALUES (5, 1, 5, 390.00, 90.00, 400.00, 100.00);

-- Shop #2 stock
INSERT INTO shop_stock (stock_id, shop_id, commodity_id, current_quantity, opening_month_balance, total_received_month, total_distributed_month)
VALUES (6, 2, 1, 5200.00, 1000.00, 5500.00, 1300.00);
INSERT INTO shop_stock (stock_id, shop_id, commodity_id, current_quantity, opening_month_balance, total_received_month, total_distributed_month)
VALUES (7, 2, 2, 1100.00, 250.00, 1100.00, 250.00);

-- 11. ENTITLEMENTS for 2026-10
-- Card 1001 (AAY, 4 members): Rice 35kg, Sugar 1kg, Oil 1pkt, Dal 1kg
INSERT INTO entitlements (entitlement_id, card_id, commodity_id, allotment_year, allotment_month, entitled_quantity, distributed_quantity, remaining_quantity, status)
VALUES (1, 1001, 1, 2026, 10, 35.00, 20.00, 15.00, 'PARTIALLY_DRAWN');
INSERT INTO entitlements (entitlement_id, card_id, commodity_id, allotment_year, allotment_month, entitled_quantity, distributed_quantity, remaining_quantity, status)
VALUES (2, 1001, 3, 2026, 10, 1.00, 1.00, 0.00, 'EXHAUSTED');
INSERT INTO entitlements (entitlement_id, card_id, commodity_id, allotment_year, allotment_month, entitled_quantity, distributed_quantity, remaining_quantity, status)
VALUES (3, 1001, 4, 2026, 10, 1.00, 0.00, 1.00, 'AVAILABLE');
INSERT INTO entitlements (entitlement_id, card_id, commodity_id, allotment_year, allotment_month, entitled_quantity, distributed_quantity, remaining_quantity, status)
VALUES (4, 1001, 5, 2026, 10, 1.00, 0.00, 1.00, 'AVAILABLE');

-- Card 1002 (PHH, 3 members): Rice 15kg (5kg/member), Wheat 5kg, Sugar 1.25kg, Oil 1pkt, Dal 1kg
INSERT INTO entitlements (entitlement_id, card_id, commodity_id, allotment_year, allotment_month, entitled_quantity, distributed_quantity, remaining_quantity, status)
VALUES (5, 1002, 1, 2026, 10, 15.00, 0.00, 15.00, 'AVAILABLE');
INSERT INTO entitlements (entitlement_id, card_id, commodity_id, allotment_year, allotment_month, entitled_quantity, distributed_quantity, remaining_quantity, status)
VALUES (6, 1002, 2, 2026, 10, 5.00, 0.00, 5.00, 'AVAILABLE');
INSERT INTO entitlements (entitlement_id, card_id, commodity_id, allotment_year, allotment_month, entitled_quantity, distributed_quantity, remaining_quantity, status)
VALUES (7, 1002, 3, 2026, 10, 1.25, 0.00, 1.25, 'AVAILABLE');

-- 12. ALLOTMENTS for 2026-10
INSERT INTO allotments (allotment_id, shop_id, commodity_id, allotment_year, allotment_month, allocated_quantity, dispatched_quantity, received_quantity, allotment_status)
VALUES (1, 1, 1, 2026, 10, 6000.00, 5000.00, 5000.00, 'PARTIALLY_DISPATCHED');
INSERT INTO allotments (allotment_id, shop_id, commodity_id, allotment_year, allotment_month, allocated_quantity, dispatched_quantity, received_quantity, allotment_status)
VALUES (2, 1, 2, 2026, 10, 1500.00, 1200.00, 1200.00, 'PARTIALLY_DISPATCHED');
INSERT INTO allotments (allotment_id, shop_id, commodity_id, allotment_year, allotment_month, allocated_quantity, dispatched_quantity, received_quantity, allotment_status)
VALUES (3, 1, 3, 2026, 10, 500.00, 450.00, 450.00, 'PARTIALLY_DISPATCHED');

-- 13. DISPATCHES & DISPATCH_ITEMS
INSERT INTO dispatches (dispatch_id, dispatch_number, godown_id, shop_id, vehicle_number, driver_name, driver_phone, dispatch_status, dispatched_at, received_at, dispatched_by, received_by, remarks)
VALUES (101, 'DSP-202610-00101', 1, 1, 'TN-09-AB-4021', 'M. Selvam', '9840123456', 'RECEIVED', SYSTIMESTAMP - 5, SYSTIMESTAMP - 4, 'GDN_MGR_SENTHIL', 'FPS_OP_VELMURUGAN', 'Regular October quota consignment');

INSERT INTO dispatch_items (dispatch_item_id, dispatch_id, commodity_id, dispatched_quantity, received_quantity, discrepancy_quantity)
VALUES (501, 101, 1, 5000.00, 5000.00, 0.00);
INSERT INTO dispatch_items (dispatch_item_id, dispatch_id, commodity_id, dispatched_quantity, received_quantity, discrepancy_quantity)
VALUES (502, 101, 2, 1200.00, 1200.00, 0.00);
INSERT INTO dispatch_items (dispatch_item_id, dispatch_id, commodity_id, dispatched_quantity, received_quantity, discrepancy_quantity)
VALUES (503, 101, 3, 450.00, 450.00, 0.00);

-- Active In-Transit Dispatch
INSERT INTO dispatches (dispatch_id, dispatch_number, godown_id, shop_id, vehicle_number, driver_name, driver_phone, dispatch_status, dispatched_at, dispatched_by, remarks)
VALUES (102, 'DSP-202610-00102', 1, 2, 'TN-11-CD-8910', 'V. Natarajan', '9840789012', 'IN_TRANSIT', SYSTIMESTAMP - 0.1, 'GDN_MGR_SENTHIL', 'Mid-month replenishment truck');

INSERT INTO dispatch_items (dispatch_item_id, dispatch_id, commodity_id, dispatched_quantity, discrepancy_quantity)
VALUES (504, 102, 1, 3000.00, 0.00);

-- 14. SALES & SALE_ITEMS (User 4 is operator)
INSERT INTO sales (sale_id, bill_number, shop_id, card_id, sale_date, sale_month, sale_year, total_amount, payment_mode, auth_mode, operator_user_id, sale_status)
VALUES (10001, 'BILL-FPS01-202610-010001', 1, 1001, SYSTIMESTAMP - 2, 10, 2026, 13.50, 'CASH', 'AADHAAR_OTP', 4, 'COMPLETED');

INSERT INTO sale_items (sale_item_id, sale_id, commodity_id, quantity, unit_rate, total_price, subsidy_amount)
VALUES (50001, 10001, 1, 20.00, 0.00, 0.00, 690.00);
INSERT INTO sale_items (sale_item_id, sale_id, commodity_id, quantity, unit_rate, total_price, subsidy_amount)
VALUES (50002, 10001, 3, 1.00, 13.50, 13.50, 24.00);

-- 15. STOCK_MOVEMENTS
INSERT INTO stock_movements (movement_id, movement_type, source_type, source_id, dest_type, dest_id, commodity_id, quantity, reference_doc_type, reference_doc_id, created_by, movement_timestamp)
VALUES (1, 'GODOWN_DISPATCH', 'GODOWN', 1, 'SHOP', 1, 1, 5000.00, 'DISPATCH', 'DSP-202610-00101', 'GDN_MGR_SENTHIL', SYSTIMESTAMP - 5);
INSERT INTO stock_movements (movement_id, movement_type, source_type, source_id, dest_type, dest_id, commodity_id, quantity, reference_doc_type, reference_doc_id, created_by, movement_timestamp)
VALUES (2, 'SHOP_RECEIPT', 'GODOWN', 1, 'SHOP', 1, 1, 5000.00, 'DISPATCH', 'DSP-202610-00101', 'FPS_OP_VELMURUGAN', SYSTIMESTAMP - 4);
INSERT INTO stock_movements (movement_id, movement_type, source_type, source_id, dest_type, dest_id, commodity_id, quantity, reference_doc_type, reference_doc_id, created_by, movement_timestamp)
VALUES (3, 'SALE', 'SHOP', 1, 'CARDHOLDER', 1001, 1, 20.00, 'BILL', 'BILL-FPS01-202610-010001', 'FPS_OP_VELMURUGAN', SYSTIMESTAMP - 2);

-- 16. STOCK_RECONCILIATION (User 2 is inspecting officer)
INSERT INTO stock_reconciliation (recon_id, shop_id, commodity_id, recon_date, recorded_system_stock, calculated_expected_stock, physical_counted_stock, variance_quantity, discrepancy_percentage, investigation_status, inspecting_officer_id, officer_remarks)
VALUES (1, 1, 1, TRUNC(SYSDATE - 1), 4850.00, 4850.00, 4850.00, 0.00, 0.00, 'MATCHED', 2, 'Physical bag count matches electronic register accurately.');
INSERT INTO stock_reconciliation (recon_id, shop_id, commodity_id, recon_date, recorded_system_stock, calculated_expected_stock, physical_counted_stock, variance_quantity, discrepancy_percentage, investigation_status, inspecting_officer_id, officer_remarks)
VALUES (2, 3, 1, TRUNC(SYSDATE - 2), 3200.00, 3200.00, 3100.00, -100.00, 3.13, 'INVESTIGATION_OPENED', 2, 'Unaccounted shortage of 100kg rice observed during surprise weighment.');

-- 17. COMPLAINTS & COMPLAINT_HISTORY (User 2 is assigned officer)
INSERT INTO complaints (complaint_id, complaint_ref, card_id, shop_id, category, description, language, priority, status, assigned_officer_id, created_at, updated_at)
VALUES (101, 'GRV-202610-00101', 1002, 1, 'QUANTITY_SHORTAGE', 'Shop operator weighed 14kg instead of 15kg entitled rice and refused to reweigh.', 'en', 'HIGH', 'IN_INVESTIGATION', 2, SYSTIMESTAMP - 3, SYSTIMESTAMP - 1);
INSERT INTO complaints (complaint_id, complaint_ref, card_id, shop_id, category, description, language, priority, status, assigned_officer_id, resolution_notes, resolved_at, created_at, updated_at)
VALUES (102, 'GRV-202610-00102', 1003, 3, 'STOCK_UNAVAILABLE', 'கடை திறக்கவில்லை மற்றும் சர்க்கரை இருப்பு இல்லை என்று கூறினர்.', 'ta', 'MEDIUM', 'RESOLVED', 2, 'Inspected shop stock. Stock replenishment delivered on Oct 25 and distributed to complainant.', SYSTIMESTAMP - 0.5, SYSTIMESTAMP - 4, SYSTIMESTAMP - 0.5);

COMMIT;
PROMPT Sample demonstration data inserted successfully.
