# PDS-Ration-Intelligence: End-User Operations Manual
## Role-Based Portal User Guide

### 1. Introduction
The PDS Ration Shop Stock & Entitlement System provides tailored interfaces based on authenticated user roles. This guide outlines standard operating procedures for each role.

---

### 2. Fair-Price Shop Operator (Shopkeeper Guide)

#### A. Starting the Daily Session
1. Navigate to `http://localhost:5173/login`.
2. Enter your assigned Shopkeeper credentials (e.g. `fps01` / `Password123!`).
3. The system directs you to the **Fair-Price Shop POS Terminal**.

#### B. Inwarding Stock Dispatches
1. Select **Dispatch Receipts** from the navigation menu.
2. Locate dispatches marked as `IN_TRANSIT` from the central godown.
3. Verify the vehicle number and weigh the physical bags.
4. Click **Confirm Receipt**. Once confirmed:
   - Status changes to `RECEIVED`.
   - Your local shop stock is automatically updated.

#### C. Conducting Ration Sales (POS Checkout)
1. In the **POS Sales** tab, enter the citizen's **Ration Card Number** (e.g. `RC-TN-04-100234`).
2. Click **Lookup**. The system displays:
   - Head of Family name and photo/Aadhaar last 4 digits.
   - Card Category (e.g., AAY, PHH).
   - Remaining quota for the current month.
3. Select commodities requested (e.g. Rice, Sugar, Oil).
4. Enter quantities. The system actively blocks quantities exceeding remaining quota or available shop stock.
5. Select Authentication Mode (Aadhaar OTP / Biometric / Manual Override).
6. Click **Complete Sale**.
7. Print or display the instant digital bill receipt.

---

### 3. Godown In-Charge (Godown Manager Guide)

#### A. Viewing Buffer Stock
1. Log in with Godown Manager credentials (`godown_mgr1`).
2. Open **Godown Inventory** to inspect warehouse stock levels (Rice, Wheat, Sugar, Dal) in Metric Tons.

#### B. Generating Outward Dispatches
1. Open **Shop Allotments** to inspect approved monthly shop quotas.
2. Click **Create Dispatch Order**:
   - Select Destination Fair-Price Shop.
   - Enter Vehicle Number and Driver Phone.
   - Specify commodity quantities in kilograms.
3. Click **Dispatch Truck**.
4. The system validates available godown stock, decrements inventory, creates a gate pass reference (`DSP-YYYYMM-XXXX`), and alerts the receiving shop.

---

### 4. Taluk Supply Officer (Officer Guide)

#### A. Monitoring Operations & Key Indicators
1. Log in with Officer credentials (`officer1`).
2. The **Taluk Dashboard** displays:
   - Total Active Cards & Registered Shops (120 FPS).
   - Monthly Distribution Progress (% of quota drawn).
   - Low-Stock Shop Warnings.
   - Pending Grievances and Unreconciled Discrepancies.

#### B. Generating & Approving Monthly Allotments
1. Navigate to **Shop Allotments**.
2. Select Year and Month. Click **Compute Taluk Allotments**.
3. Inspect aggregated shop demands.
4. Click **Approve Allotment Order** to authorize godown releases.

#### C. Conducting Physical Stock Reconciliation
1. Navigate to **Stock Reconciliation**.
2. Select the shop inspected.
3. Enter physical bag weighment counts.
4. The system computes the expected stock based on opening inventory, receipts, and sales.
5. If variance exceeds tolerance, the record is flagged for investigation.

#### D. Reviewing AI Anomaly Alerts & Demand Forecasts
1. Open **AI Stock Radar**: Review shops flagged with unexpected sales velocity, rapid turnover, or negative variances.
2. Open **AI Demand Forecasting**: Inspect 30-day projected commodity requirements and days of stock remaining.
3. Open **AI Officer Assistant**: Type natural language inquiries (e.g., *"Which shops have less than 5 days of rice remaining?"*) to receive verified, read-only summaries.

#### E. Grievance Redressal
1. Open **Complaints Board**.
2. Review new citizen grievances triage-tagged by the AI classifier (Tamil/English).
3. Assign inspection officers, review findings, and submit final resolution notes.
