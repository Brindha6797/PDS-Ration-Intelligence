# PDS-Ration-Intelligence: Operational Workflows & Business Logic
## End-to-End Civil Supplies Supply Chain & Distribution Workflows

### Workflow 1: Beneficiary Registration & Verification
```
 [Citizen Application] 
         |
         v
 [Civil Supplies Taluk Office] ---> Data Verification (Head, Members, Aadhaar, Income)
         |
         v
 [Card Classification] ----------> AAY (Ultra-poor), PHH (Priority), NPHH (General)
         |
         v
 [Assign Fair-Price Shop] --------> Map to nearest FPS (1 of 120 shops in Taluk)
         |
         v
 [Oracle Database] --------------> INSERT into CARD_HOLDERS & FAMILY_MEMBERS
```
- **Integrity Validation:** Prevents duplicate Aadhaar usage across families via hash verification.
- **Family Size Sync:** Dynamic trigger/service updates `total_family_members` count on addition/removal of members.

---

### Workflow 2: Monthly Entitlement Calculation (PL/SQL)
```
 Start of Allocation Cycle (e.g. 25th of Preceding Month)
         |
         v
 [Trigger Batch / Stored Proc] ---> pkg_pds_allotment.generate_monthly_entitlements(YYYY, MM)
         |
         +---> Iterate Active Cardholders (CARD_HOLDERS WHERE card_status = 'ACTIVE')
         |
         +---> For each active COMMODITY:
         |        Invoke PL/SQL Function: monthly_entitlement(card_type_id, family_size, commodity_id)
         |        1. Query active ENTITLEMENT_RULES
         |        2. Apply calc_method ('FIXED_PER_CARD', 'PER_MEMBER', 'PER_MEMBER_WITH_CEILING')
         |        3. Enforce min/max rules & commodity ceiling
         |        4. Return permitted entitlement_quantity
         v
 [Insert into ENTITLEMENTS] ------> Status: 'AVAILABLE', remaining_quantity = entitled_quantity
```
- **Idempotency Guarantee:** Composite Unique constraint on `(card_id, commodity_id, allotment_year, allotment_month)` guarantees no duplicate allotments are created even if recalculations are triggered.

---

### Workflow 3: Shop Allotment & Godown Dispatch
```
 [1. Compute Shop Allotment]
   Sum entitled quantities across all active cardholders assigned to Shop
   Status: 'DRAFT'
         |
         v
 [2. Taluk Supply Officer Review & Approval]
   Officer inspects total demand vs. taluk grain reserves
   Status: 'APPROVED'
         |
         v
 [3. Central Godown Indent & Dispatch]
   Godown Manager initiates truckload dispatch:
   - Selects approved allotment
   - Allocates stock from GODOWN_STOCK (reduces current_quantity)
   - Assigns vehicle, driver, gate pass
   - Creates DISPATCH record (Status: 'IN_TRANSIT') & DISPATCH_ITEMS
   - Appends to STOCK_MOVEMENTS ('GODOWN_DISPATCH')
         |
         v
 [4. Shop Receipt & Inwarding]
   Truck arrives at Fair-Price Shop:
   - Shopkeeper inspects physical bags / seal
   - Confirms received quantity in portal (GET /api/v1/dispatches/shop/{id})
   - If received == dispatched: DISPATCH status -> 'RECEIVED'
   - SHOP_STOCK incremented atomically
   - Appends to STOCK_MOVEMENTS ('SHOP_RECEIPT')
   - Inwarding complete; stock is live for distribution
```

---

### Workflow 4: Point of Sale (e-POS) Ration Sale & Transaction Safety
```
 Citizen visits Fair-Price Shop with Ration Card
         |
         v
 [1. POS Card Lookup]
   Shopkeeper enters Card Number -> UI shows Card Type, Member Count, Remaining Quotas
         |
         v
 [2. Commodity Selection & Quantity Entry]
   E.g., Rice 20kg, Sugar 2kg -> UI submits POST /api/v1/sales
         |
         v
 [3. Atomic Database Transaction: pkg_pds_sales.process_sale]
   BEGIN TRANSACTION
     1. Lock SHOP_STOCK rows: SELECT current_quantity FROM SHOP_STOCK ... FOR UPDATE;
     2. Check Shop Inventory: IF current_quantity < requested_quantity THEN RAISE e_insufficient_stock;
     3. Lock ENTITLEMENTS row: SELECT remaining_quantity FROM ENTITLEMENTS ... FOR UPDATE;
     4. Check Card Quota: IF remaining_quantity < requested_quantity THEN RAISE e_entitlement_exhausted;
     5. INSERT into SALES & SALE_ITEMS;
     6. UPDATE SHOP_STOCK SET current_quantity = current_quantity - requested_quantity;
     7. UPDATE ENTITLEMENTS SET distributed_quantity = distributed_quantity + requested_quantity,
                                remaining_quantity = remaining_quantity - requested_quantity,
                                status = CASE WHEN remaining_quantity = requested_quantity THEN 'EXHAUSTED'
                                              ELSE 'PARTIALLY_DRAWN' END;
     8. INSERT into STOCK_MOVEMENTS ('SALE');
   COMMIT;
         |
         v
 [4. Bill Receipt Printing & Cardholder SMS Confirmation]
```
- **Error Normalization:** If either `e_entitlement_exhausted` or `e_insufficient_stock` fires, the entire database transaction is immediately rolled back with zero partial state changes.

---

### Workflow 5: Physical Stock Count & Reconciliation
```
 Monthly / Surprise Inspection by Taluk Enforcement Officer
         |
         v
 [1. Query System Calculated Stock]
   Expected Stock = Opening Balance + SUM(Receipts) - SUM(Sales)
         |
         v
 [2. Physical Measurement]
   Officer weighs physical grain bags in shop
         |
         v
 [3. Record in STOCK_RECONCILIATION]
   System computes: Variance = Physical Count - System Recorded
   Discrepancy % = (ABS(Variance) / System Recorded) * 100
         |
         +---> Variance == 0: Status = 'MATCHED'
         |
         +---> Variance > 0 or < 0:
                  Status = 'PENDING_REVIEW'
                  Logged for investigation
                  If justified (e.g. allowable moisture loss < 0.5%): Authorized adjustment
                  If unjustified: Escalated to District Supply Officer
```

---

### Workflow 6: Citizen Grievance Redressal
```
 Beneficiary submits Complaint (via Portal, Helpdesk, or Fair-Price Shop)
         |
         v
 [1. Grievance Logging: INSERT into COMPLAINTS]
   Generated Reference: GRV-202610-XXXX (Status: 'REGISTERED')
   Appends initial entry to COMPLAINT_HISTORY
         |
         v
 [2. AI Pre-Classification & Triage]
   Python AI service analyzes text (Tamil or English):
   - Categorizes (e.g. 'QUANTITY_SHORTAGE')
   - Assigns priority ('HIGH' if denial of core food grains)
   - Recommends responsible inspector
         |
         v
 [3. Taluk Officer Investigation]
   Officer reviews shop stock history and cardholder sale record
   Status -> 'IN_INVESTIGATION' (Logged in COMPLAINT_HISTORY)
         |
         v
 [4. Resolution & Closing]
   Officer documents corrective action (e.g., replacement grain, warning to shopkeeper)
   Status -> 'RESOLVED', resolved_at timestamp stamped
   Audited in COMPLAINT_HISTORY
```
