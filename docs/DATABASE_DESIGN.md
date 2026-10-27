# PDS-Ration-Intelligence: Oracle Database Design & Schema Specification
## Target Database: Oracle Database 19c / 21c / 23c

### 1. Schema Design Principles
1. **Third Normal Form (3NF) Compliance:** Elimination of redundancy, preservation of functional dependencies, and explicit domain constraints.
2. **Authoritative Stock Single-Source:** Current inventory is maintained in `SHOP_STOCK` and `GODOWN_STOCK`, backed by an immutable append-only ledger in `STOCK_MOVEMENTS`.
3. **Deterministic Identity:** Standardized Oracle sequences (`seq_<table_name>`) or identity columns for primary keys, with strict business keys (`card_number`, `shop_code`, `bill_number`, `complaint_ref`).
4. **Referential Integrity & Cascading:** Foreign keys with `ON DELETE RESTRICT` for master tables to prevent accidental data loss of government records.
5. **Auditing & Timestamps:** Standard `created_at`, `updated_at`, `created_by`, `updated_by` across entities.

---

### 2. Entity Relationship Overview

```
 [CARD_TYPES] 1 --- * [CARD_HOLDERS] 1 --- * [FAMILY_MEMBERS]
       |                      |
       |                      +--- * [ENTITLEMENTS] * --- 1 [COMMODITIES]
       +--- * [ENTITLEMENT_RULES] * --- 1                      |
                                                               |
 [GODOWNS] 1 --- * [GODOWN_STOCK] * --------------------------+
     |                                                         |
     +--- 1 [DISPATCHES] 1 --- * [DISPATCH_ITEMS] * ----------+
                 |                                             |
                 v (Delivered to)                              |
 [SHOPS] 1 --- * [ALLOTMENTS] * -------------------------------+
     |                                                         |
     +--- 1 --- * [SHOP_STOCK] * -----------------------------+
     |                  |                                      |
     +--- 1 --- * [SALES] 1 --- * [SALE_ITEMS] * --------------+
     |                  |               |                      |
     |                  +---------------+                      |
     |                                  v                      |
     +--- 1 --- * [STOCK_MOVEMENTS] * -------------------------+
     |                  |
     +--- 1 --- * [STOCK_RECONCILIATION]
     |
     +--- 1 --- * [COMPLAINTS] 1 --- * [COMPLAINT_HISTORY]

 [USERS] * --- * [ROLES] via [USER_ROLES]
    |
    +--- * [AUDIT_LOGS]
```

---

### 3. Detailed Data Dictionary (22 Tables)

#### 1. `CARD_TYPES`
Defines government ration card classifications.
- `card_type_id` (NUMBER, PK)
- `type_code` (VARCHAR2(20), UNIQUE, NOT NULL) - e.g., 'AAY', 'PHH', 'NPHH', 'AY'
- `type_name` (VARCHAR2(100), NOT NULL) - e.g., 'Antyodaya Anna Yojana'
- `description` (VARCHAR2(255))
- `color_code` (VARCHAR2(20)) - e.g., 'YELLOW', 'PINK', 'WHITE', 'GREEN'
- `is_active` (CHAR(1), DEFAULT 'Y', CHECK IN ('Y', 'N'))
- `created_at` (TIMESTAMP DEFAULT SYSTIMESTAMP)

#### 2. `CARD_HOLDERS`
Master record for citizen ration cards in the taluk.
- `card_id` (NUMBER, PK)
- `card_number` (VARCHAR2(30), UNIQUE, NOT NULL) - e.g., 'RC-TN-04-100234'
- `card_type_id` (NUMBER, FK to CARD_TYPES, NOT NULL)
- `head_of_family_name` (VARCHAR2(150), NOT NULL)
- `aadhaar_hash` (VARCHAR2(64), NOT NULL) - SHA-256 salted hash of Aadhaar
- `aadhaar_last4` (VARCHAR2(4), NOT NULL) - Last 4 digits for POS verification
- `mobile_number` (VARCHAR2(15))
- `address_line1` (VARCHAR2(255), NOT NULL)
- `taluk_name` (VARCHAR2(100), NOT NULL) - Default taluk jurisdiction
- `district_name` (VARCHAR2(100), NOT NULL)
- `pincode` (VARCHAR2(10), NOT NULL)
- `assigned_shop_id` (NUMBER, FK to SHOPS, NOT NULL)
- `total_family_members` (NUMBER(2), DEFAULT 1, CHECK (total_family_members >= 1))
- `annual_income` (NUMBER(10,2))
- `card_status` (VARCHAR2(20), DEFAULT 'ACTIVE', CHECK IN ('ACTIVE', 'SUSPENDED', 'CANCELLED', 'SURRENDERED'))
- `effective_date` (DATE DEFAULT SYSDATE)
- `created_at`, `updated_at` (TIMESTAMP)

#### 3. `FAMILY_MEMBERS`
Beneficiary details attached to ration cards.
- `member_id` (NUMBER, PK)
- `card_id` (NUMBER, FK to CARD_HOLDERS, NOT NULL)
- `member_name` (VARCHAR2(150), NOT NULL)
- `relationship_to_head` (VARCHAR2(50), NOT NULL) - 'SELF', 'SPOUSE', 'SON', 'DAUGHTER', 'PARENT'
- `gender` (CHAR(1), CHECK IN ('M', 'F', 'T'))
- `date_of_birth` (DATE, NOT NULL)
- `age` (NUMBER(3))
- `aadhaar_hash` (VARCHAR2(64), NOT NULL)
- `aadhaar_last4` (VARCHAR2(4), NOT NULL)
- `is_active` (CHAR(1), DEFAULT 'Y', CHECK IN ('Y', 'N'))
- `created_at`, `updated_at` (TIMESTAMP)

#### 4. `SHOPS`
Fair-Price Shops (FPS) authorized to distribute rations.
- `shop_id` (NUMBER, PK)
- `shop_code` (VARCHAR2(20), UNIQUE, NOT NULL) - e.g., 'FPS-TLK-001'
- `shop_name` (VARCHAR2(150), NOT NULL)
- `license_number` (VARCHAR2(50), UNIQUE, NOT NULL)
- `operator_name` (VARCHAR2(150), NOT NULL)
- `contact_number` (VARCHAR2(15), NOT NULL)
- `taluk_name` (VARCHAR2(100), NOT NULL)
- `ward_village` (VARCHAR2(100), NOT NULL)
- `address` (VARCHAR2(255), NOT NULL)
- `latitude` (NUMBER(10,7))
- `longitude` (NUMBER(10,7))
- `shop_status` (VARCHAR2(20), DEFAULT 'ACTIVE', CHECK IN ('ACTIVE', 'SUSPENDED', 'REVOKED'))
- `created_at`, `updated_at` (TIMESTAMP)

#### 5. `COMMODITIES`
Government subsidized commodities distributed via PDS.
- `commodity_id` (NUMBER, PK)
- `commodity_code` (VARCHAR2(20), UNIQUE, NOT NULL) - e.g., 'RICE_COMMON', 'WHEAT', 'SUGAR', 'PALM_OIL', 'KEROSENE'
- `commodity_name` (VARCHAR2(100), NOT NULL)
- `measurement_unit` (VARCHAR2(20), NOT NULL) - 'KG', 'LITRE', 'PACKET'
- `consumer_rate_per_unit` (NUMBER(8,2), NOT NULL) - Consumer price (e.g. 0.00 for free rice, 13.50 for sugar)
- `government_subsidy_per_unit` (NUMBER(8,2), NOT NULL) - Economic cost offset
- `buffer_stock_threshold` (NUMBER(10,2), DEFAULT 100.00)
- `is_active` (CHAR(1), DEFAULT 'Y', CHECK IN ('Y', 'N'))
- `created_at` (TIMESTAMP DEFAULT SYSTIMESTAMP)

#### 6. `ENTITLEMENT_RULES`
Dynamic policy matrix governing quota calculations.
- `rule_id` (NUMBER, PK)
- `card_type_id` (NUMBER, FK to CARD_TYPES, NOT NULL)
- `commodity_id` (NUMBER, FK to COMMODITIES, NOT NULL)
- `calc_method` (VARCHAR2(30), NOT NULL, CHECK IN ('FIXED_PER_CARD', 'PER_MEMBER', 'PER_MEMBER_WITH_CEILING', 'SLAB_BASED'))
- `base_quantity` (NUMBER(8,2), DEFAULT 0.00, NOT NULL) - Fixed minimum per card
- `per_member_quantity` (NUMBER(8,2), DEFAULT 0.00, NOT NULL) - Additional per member
- `min_family_size` (NUMBER(2), DEFAULT 1)
- `max_family_size` (NUMBER(2)) - Upper bound if applicable
- `max_quantity_ceiling` (NUMBER(8,2)) - Hard maximum cap per card
- `effective_start_date` (DATE NOT NULL)
- `effective_end_date` (DATE)
- `is_active` (CHAR(1), DEFAULT 'Y', CHECK IN ('Y', 'N'))
- `created_at`, `updated_at` (TIMESTAMP)

#### 7. `ENTITLEMENTS`
Monthly calculated entitlement quota per cardholder and commodity.
- `entitlement_id` (NUMBER, PK)
- `card_id` (NUMBER, FK to CARD_HOLDERS, NOT NULL)
- `commodity_id` (NUMBER, FK to COMMODITIES, NOT NULL)
- `allotment_year` (NUMBER(4), NOT NULL) - e.g., 2026
- `allotment_month` (NUMBER(2), NOT NULL, CHECK (allotment_month BETWEEN 1 AND 12))
- `entitled_quantity` (NUMBER(8,2), NOT NULL)
- `distributed_quantity` (NUMBER(8,2), DEFAULT 0.00, NOT NULL)
- `remaining_quantity` (NUMBER(8,2), NOT NULL)
- `last_distributed_at` (TIMESTAMP)
- `status` (VARCHAR2(20), DEFAULT 'AVAILABLE', CHECK IN ('AVAILABLE', 'PARTIALLY_DRAWN', 'EXHAUSTED', 'LAPSED'))
- `created_at`, `updated_at` (TIMESTAMP)
- *Constraint:* UNIQUE (`card_id`, `commodity_id`, `allotment_year`, `allotment_month`)

#### 8. `ALLOTMENTS`
Monthly bulk quota allocated to fair-price shops.
- `allotment_id` (NUMBER, PK)
- `shop_id` (NUMBER, FK to SHOPS, NOT NULL)
- `commodity_id` (NUMBER, FK to COMMODITIES, NOT NULL)
- `allotment_year` (NUMBER(4), NOT NULL)
- `allotment_month` (NUMBER(2), NOT NULL, CHECK (allotment_month BETWEEN 1 AND 12))
- `allocated_quantity` (NUMBER(10,2), NOT NULL)
- `dispatched_quantity` (NUMBER(10,2), DEFAULT 0.00, NOT NULL)
- `received_quantity` (NUMBER(10,2), DEFAULT 0.00, NOT NULL)
- `allotment_status` (VARCHAR2(20), DEFAULT 'DRAFT', CHECK IN ('DRAFT', 'APPROVED', 'PARTIALLY_DISPATCHED', 'COMPLETED', 'CANCELLED'))
- `approved_by_user_id` (NUMBER)
- `approved_at` (TIMESTAMP)
- `created_at`, `updated_at` (TIMESTAMP)
- *Constraint:* UNIQUE (`shop_id`, `commodity_id`, `allotment_year`, `allotment_month`)

#### 9. `GODOWNS`
Taluk supply depot / buffer warehouse.
- `godown_id` (NUMBER, PK)
- `godown_code` (VARCHAR2(20), UNIQUE, NOT NULL) - e.g., 'GDN-TLK-01'
- `godown_name` (VARCHAR2(150), NOT NULL)
- `taluk_name` (VARCHAR2(100), NOT NULL)
- `storage_capacity_mt` (NUMBER(10,2), NOT NULL)
- `manager_name` (VARCHAR2(150), NOT NULL)
- `contact_number` (VARCHAR2(15), NOT NULL)
- `address` (VARCHAR2(255), NOT NULL)
- `is_active` (CHAR(1), DEFAULT 'Y', CHECK IN ('Y', 'N'))
- `created_at` (TIMESTAMP DEFAULT SYSTIMESTAMP)

#### 10. `GODOWN_STOCK`
Physical inventory within the central supply godowns.
- `godown_stock_id` (NUMBER, PK)
- `godown_id` (NUMBER, FK to GODOWNS, NOT NULL)
- `commodity_id` (NUMBER, FK to COMMODITIES, NOT NULL)
- `current_quantity` (NUMBER(12,2), DEFAULT 0.00, NOT NULL, CHECK (current_quantity >= 0))
- `reserved_quantity` (NUMBER(12,2), DEFAULT 0.00, NOT NULL)
- `last_replenished_date` (DATE)
- `updated_at` (TIMESTAMP DEFAULT SYSTIMESTAMP)
- *Constraint:* UNIQUE (`godown_id`, `commodity_id`)

#### 11. `DISPATCHES`
Truckload dispatches from godown to fair-price shops.
- `dispatch_id` (NUMBER, PK)
- `dispatch_number` (VARCHAR2(30), UNIQUE, NOT NULL) - e.g., 'DSP-202610-0042'
- `godown_id` (NUMBER, FK to GODOWNS, NOT NULL)
- `shop_id` (NUMBER, FK to SHOPS, NOT NULL)
- `vehicle_number` (VARCHAR2(30), NOT NULL)
- `driver_name` (VARCHAR2(100), NOT NULL)
- `driver_phone` (VARCHAR2(15))
- `dispatch_status` (VARCHAR2(20), DEFAULT 'IN_TRANSIT', CHECK IN ('PLANNED', 'DISPATCHED', 'IN_TRANSIT', 'RECEIVED', 'REJECTED'))
- `dispatched_at` (TIMESTAMP NOT NULL)
- `received_at` (TIMESTAMP)
- `dispatched_by` (VARCHAR2(50), NOT NULL)
- `received_by` (VARCHAR2(50))
- `remarks` (VARCHAR2(255))
- `created_at` (TIMESTAMP DEFAULT SYSTIMESTAMP)

#### 12. `DISPATCH_ITEMS`
Line item quantities included in a dispatch.
- `dispatch_item_id` (NUMBER, PK)
- `dispatch_id` (NUMBER, FK to DISPATCHES, NOT NULL)
- `commodity_id` (NUMBER, FK to COMMODITIES, NOT NULL)
- `dispatched_quantity` (NUMBER(10,2), NOT NULL, CHECK (dispatched_quantity > 0))
- `received_quantity` (NUMBER(10,2))
- `discrepancy_quantity` (NUMBER(10,2) DEFAULT 0.00)
- `condition_notes` (VARCHAR2(255))
- *Constraint:* UNIQUE (`dispatch_id`, `commodity_id`)

#### 13. `SHOP_STOCK`
Authoritative inventory level held at fair-price shops.
- `stock_id` (NUMBER, PK)
- `shop_id` (NUMBER, FK to SHOPS, NOT NULL)
- `commodity_id` (NUMBER, FK to COMMODITIES, NOT NULL)
- `current_quantity` (NUMBER(10,2), DEFAULT 0.00, NOT NULL, CHECK (current_quantity >= 0))
- `opening_month_balance` (NUMBER(10,2), DEFAULT 0.00, NOT NULL)
- `total_received_month` (NUMBER(10,2), DEFAULT 0.00, NOT NULL)
- `total_distributed_month` (NUMBER(10,2), DEFAULT 0.00, NOT NULL)
- `last_sale_date` (TIMESTAMP)
- `last_reconciliation_date` (DATE)
- `updated_at` (TIMESTAMP DEFAULT SYSTIMESTAMP)
- *Constraint:* UNIQUE (`shop_id`, `commodity_id`)

#### 14. `SALES`
POS transaction header for ration distribution.
- `sale_id` (NUMBER, PK)
- `bill_number` (VARCHAR2(35), UNIQUE, NOT NULL) - e.g., 'BILL-FPS01-202610-00891'
- `shop_id` (NUMBER, FK to SHOPS, NOT NULL)
- `card_id` (NUMBER, FK to CARD_HOLDERS, NOT NULL)
- `sale_date` (TIMESTAMP DEFAULT SYSTIMESTAMP, NOT NULL)
- `sale_month` (NUMBER(2), NOT NULL)
- `sale_year` (NUMBER(4), NOT NULL)
- `total_amount` (NUMBER(8,2), DEFAULT 0.00, NOT NULL)
- `payment_mode` (VARCHAR2(20), DEFAULT 'CASH', CHECK IN ('CASH', 'UPI', 'EXEMPTED'))
- `auth_mode` (VARCHAR2(20), DEFAULT 'AADHAAR_OTP', CHECK IN ('AADHAAR_OTP', 'BIOMETRIC_IRIS', 'BIOMETRIC_FINGER', 'MANUAL_OVERRIDE'))
- `operator_user_id` (NUMBER, NOT NULL)
- `sale_status` (VARCHAR2(20), DEFAULT 'COMPLETED', CHECK IN ('COMPLETED', 'VOIDED'))
- `created_at` (TIMESTAMP DEFAULT SYSTIMESTAMP)

#### 15. `SALE_ITEMS`
Detailed commodity quantities dispensed in a sale.
- `sale_item_id` (NUMBER, PK)
- `sale_id` (NUMBER, FK to SALES, NOT NULL)
- `commodity_id` (NUMBER, FK to COMMODITIES, NOT NULL)
- `quantity` (NUMBER(8,2), NOT NULL, CHECK (quantity > 0))
- `unit_rate` (NUMBER(8,2), NOT NULL)
- `total_price` (NUMBER(8,2), NOT NULL)
- `subsidy_amount` (NUMBER(8,2), DEFAULT 0.00)
- *Constraint:* UNIQUE (`sale_id`, `commodity_id`)

#### 16. `STOCK_MOVEMENTS`
Universal immutable ledger of all physical stock movements.
- `movement_id` (NUMBER, PK)
- `movement_type` (VARCHAR2(30), NOT NULL, CHECK IN ('GODOWN_INWARD', 'GODOWN_DISPATCH', 'SHOP_RECEIPT', 'SALE', 'PHYSICAL_ADJUSTMENT', 'RETURN_TO_DEPOT'))
- `source_type` (VARCHAR2(20), CHECK IN ('GODOWN', 'SHOP', 'SUPPLIER', 'ADJUSTMENT'))
- `source_id` (NUMBER)
- `dest_type` (VARCHAR2(20), CHECK IN ('GODOWN', 'SHOP', 'CARDHOLDER', 'ADJUSTMENT'))
- `dest_id` (NUMBER)
- `commodity_id` (NUMBER, FK to COMMODITIES, NOT NULL)
- `quantity` (NUMBER(10,2), NOT NULL)
- `reference_doc_type` (VARCHAR2(30)) - e.g., 'DISPATCH', 'BILL', 'RECONCILIATION_ORDER'
- `reference_doc_id` (VARCHAR2(50))
- `created_by` (VARCHAR2(50), NOT NULL)
- `movement_timestamp` (TIMESTAMP DEFAULT SYSTIMESTAMP, NOT NULL)

#### 17. `STOCK_RECONCILIATION`
Audited reconciliation comparison between physical and recorded shop inventory.
- `recon_id` (NUMBER, PK)
- `shop_id` (NUMBER, FK to SHOPS, NOT NULL)
- `commodity_id` (NUMBER, FK to COMMODITIES, NOT NULL)
- `recon_date` (DATE NOT NULL)
- `recorded_system_stock` (NUMBER(10,2), NOT NULL)
- `calculated_expected_stock` (NUMBER(10,2), NOT NULL) - Derived: Opening + Receipts - Sales
- `physical_counted_stock` (NUMBER(10,2), NOT NULL)
- `variance_quantity` (NUMBER(10,2), NOT NULL) - Physical - Recorded
- `discrepancy_percentage` (NUMBER(5,2))
- `investigation_status` (VARCHAR2(25), DEFAULT 'PENDING_REVIEW', CHECK IN ('MATCHED', 'PENDING_REVIEW', 'INVESTIGATION_OPENED', 'JUSTIFIED_LOSS', 'RECONCILED'))
- `inspecting_officer_id` (NUMBER NOT NULL)
- `officer_remarks` (VARCHAR2(500))
- `adjustment_movement_id` (NUMBER)
- `created_at` (TIMESTAMP DEFAULT SYSTIMESTAMP)

#### 18. `COMPLAINTS`
Citizen and cardholder grievances.
- `complaint_id` (NUMBER, PK)
- `complaint_ref` (VARCHAR2(30), UNIQUE, NOT NULL) - e.g., 'GRV-202610-0012'
- `card_id` (NUMBER, FK to CARD_HOLDERS)
- `shop_id` (NUMBER, FK to SHOPS)
- `category` (VARCHAR2(50), NOT NULL, CHECK IN ('QUANTITY_SHORTAGE', 'STOCK_UNAVAILABLE', 'DELAYED_OPENING', 'INCORRECT_ENTITLEMENT', 'SHOP_BEHAVIOR', 'QUALITY_ISSUE', 'OTHER'))
- `description` (VARCHAR2(2000), NOT NULL)
- `language` (VARCHAR2(10), DEFAULT 'en', CHECK IN ('en', 'ta'))
- `priority` (VARCHAR2(20), DEFAULT 'MEDIUM', CHECK IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL'))
- `status` (VARCHAR2(25), DEFAULT 'REGISTERED', CHECK IN ('REGISTERED', 'ASSIGNED', 'IN_INVESTIGATION', 'RESOLVED', 'REJECTED'))
- `assigned_officer_id` (NUMBER)
- `resolution_notes` (VARCHAR2(2000))
- `resolved_at` (TIMESTAMP)
- `created_at`, `updated_at` (TIMESTAMP)

#### 19. `COMPLAINT_HISTORY`
Immutable chronological audit of complaint status transitions.
- `history_id` (NUMBER, PK)
- `complaint_id` (NUMBER, FK to COMPLAINTS, NOT NULL)
- `previous_status` (VARCHAR2(25))
- `new_status` (VARCHAR2(25), NOT NULL)
- `changed_by_user` (VARCHAR2(50), NOT NULL)
- `action_taken` (VARCHAR2(255), NOT NULL)
- `action_timestamp` (TIMESTAMP DEFAULT SYSTIMESTAMP, NOT NULL)
- `comments` (VARCHAR2(1000))

#### 20. `USERS`
System users with authentication credentials.
- `user_id` (NUMBER, PK)
- `username` (VARCHAR2(50), UNIQUE, NOT NULL)
- `password_hash` (VARCHAR2(255), NOT NULL) - BCrypt encrypted
- `full_name` (VARCHAR2(150), NOT NULL)
- `email` (VARCHAR2(100), UNIQUE)
- `phone` (VARCHAR2(15))
- `taluk_name` (VARCHAR2(100))
- `assigned_shop_id` (NUMBER, FK to SHOPS)
- `assigned_godown_id` (NUMBER, FK to GODOWNS)
- `is_active` (CHAR(1), DEFAULT 'Y', CHECK IN ('Y', 'N'))
- `last_login_at` (TIMESTAMP)
- `created_at`, `updated_at` (TIMESTAMP)

#### 21. `ROLES` & `USER_ROLES`
Role-Based Access Control matrix.
- `ROLES`: `role_id` (PK), `role_name` (UNIQUE, e.g. 'ROLE_ADMIN', 'ROLE_OFFICER', 'ROLE_GODOWN_MGR', 'ROLE_SHOPKEEPER'), `description`
- `USER_ROLES`: `user_id` (FK to USERS), `role_id` (FK to ROLES) - Composite PK (`user_id`, `role_id`)

#### 22. `AUDIT_LOGS`
Comprehensive system compliance and security audit trail.
- `log_id` (NUMBER, PK)
- `entity_name` (VARCHAR2(50), NOT NULL) - Table name
- `entity_id` (VARCHAR2(50), NOT NULL) - Primary key of altered record
- `action_type` (VARCHAR2(20), NOT NULL, CHECK IN ('INSERT', 'UPDATE', 'DELETE', 'SECURITY_LOGIN', 'OVERRIDE'))
- `performed_by` (VARCHAR2(50), NOT NULL)
- `action_timestamp` (TIMESTAMP DEFAULT SYSTIMESTAMP, NOT NULL)
- `old_values` (CLOB) - JSON serialization
- `new_values` (CLOB) - JSON serialization
- `client_ip` (VARCHAR2(45))
- `status_result` (VARCHAR2(20), DEFAULT 'SUCCESS')

---

### 4. Indexing Strategy
To guarantee sub-second queries across 1 lakh cards and 120 shops:
- `idx_card_holders_shop`: B-Tree on `CARD_HOLDERS(assigned_shop_id, card_status)`
- `idx_card_holders_number`: Unique B-Tree on `CARD_HOLDERS(card_number)`
- `idx_entitlements_lookup`: Composite Unique on `ENTITLEMENTS(card_id, commodity_id, allotment_year, allotment_month)`
- `idx_sales_shop_date`: Composite B-Tree on `SALES(shop_id, sale_date)`
- `idx_sales_card_date`: Composite B-Tree on `SALES(card_id, sale_month, sale_year)`
- `idx_stock_movements_ref`: B-Tree on `STOCK_MOVEMENTS(reference_doc_type, reference_doc_id)`
- `idx_complaints_shop_status`: Composite B-Tree on `COMPLAINTS(shop_id, status)`
- `idx_recon_shop_date`: Composite B-Tree on `STOCK_RECONCILIATION(shop_id, recon_date)`
