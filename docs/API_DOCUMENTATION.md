# PDS-Ration-Intelligence: REST API Documentation
## Base URL: `http://localhost:8080/api/v1`

### Authentication & Authorization
All secured endpoints require an HTTP Authorization header:
`Authorization: Bearer <JWT_TOKEN>`

Roles: `ADMIN`, `OFFICER`, `GODOWN_MGR`, `SHOPKEEPER`

---

### 1. Authentication Endpoints (`/auth`)
- `POST /auth/login`
  - **Request:** `{ "username": "officer1", "password": "Password123!" }`
  - **Response (200 OK):**
    ```json
    {
      "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
      "username": "officer1",
      "fullName": "S. Ramanathan",
      "roles": ["ROLE_OFFICER"],
      "shopId": null,
      "godownId": null
    }
    ```
- `GET /auth/me`
  - Returns authenticated user profile and roles.

---

### 2. Cardholder & Beneficiary Endpoints (`/cards`)
- `GET /cards` (Query: `?page=0&size=20&shopId=1&search=RC-TN`)
- `GET /cards/{id}`
- `GET /cards/by-number/{cardNumber}`
- `POST /cards` (Admin/Officer only)
  - Create cardholder with family members.
- `PUT /cards/{id}`
- `GET /cards/{id}/members`
- `POST /cards/{id}/members` (Add family member)
- `GET /cards/inactive-drawal-report`
  - Identifies cards with 0 recorded drawals for 3 completed consecutive months.

---

### 3. Entitlement Endpoints (`/entitlements`)
- `GET /entitlements/card/{cardId}` (Fetch all monthly entitlements)
- `GET /entitlements/card/{cardId}/remaining?year=2026&month=10`
  - Returns remaining permitted quantities per commodity for POS checkout.
- `POST /entitlements/generate` (Officer only)
  - **Request:** `{ "year": 2026, "month": 10 }`
  - Executes PL/SQL batch calculation.
- `GET /entitlements/rules` (Fetch active entitlement rules)
- `POST /entitlements/rules` (Create/Update policy rule)

---

### 4. Fair-Price Shop & Commodity Endpoints (`/shops`, `/commodities`)
- `GET /shops` (List all 120 shops with status, operator, and location)
- `GET /shops/{id}`
- `GET /shops/{id}/stock` (Live inventory for shop)
- `GET /commodities` (List all active commodities, units, and rates)
- `POST /commodities`

---

### 5. Allotment & Supply Chain Endpoints (`/allotments`, `/godowns`, `/dispatches`)
- `GET /allotments` (Filter: `?year=2026&month=10&shopId=1`)
- `POST /allotments/calculate` (Calculate monthly taluk quotas)
- `PUT /allotments/{id}/approve` (Approve allotment for dispatch)
- `GET /godowns` (List godowns)
- `GET /godowns/{id}/stock` (Godown stock levels)
- `POST /dispatches` (Create dispatch order)
  - **Request:**
    ```json
    {
      "godownId": 1,
      "shopId": 12,
      "vehicleNumber": "TN-09-AB-4021",
      "driverName": "M. Selvam",
      "driverPhone": "9840123456",
      "items": [
        { "commodityId": 1, "dispatchedQuantity": 5000.00 },
        { "commodityId": 3, "dispatchedQuantity": 500.00 }
      ]
    }
    ```
- `GET /dispatches/shop/{shopId}` (Pending & completed dispatches)
- `POST /dispatches/{dispatchId}/confirm-receipt` (Shopkeeper confirmation)
  - Updates shop stock and marks dispatch as RECEIVED.

---

### 6. Sales & POS Distribution Endpoints (`/sales`)
- `POST /sales` (Atomic sale execution)
  - **Request:**
    ```json
    {
      "shopId": 12,
      "cardId": 101,
      "paymentMode": "CASH",
      "authMode": "AADHAAR_OTP",
      "items": [
        { "commodityId": 1, "quantity": 20.00 },
        { "commodityId": 3, "quantity": 2.00 }
      ]
    }
    ```
  - **Success Response (201 Created):**
    ```json
    {
      "saleId": 9821,
      "billNumber": "BILL-FPS12-202610-09821",
      "saleDate": "2026-10-27T10:15:00",
      "totalAmount": 27.00,
      "items": [
        { "commodityName": "Common Rice", "quantity": 20.00, "unitRate": 0.00, "totalPrice": 0.00 },
        { "commodityName": "Sugar", "quantity": 2.00, "unitRate": 13.50, "totalPrice": 27.00 }
      ]
    }
    ```
  - **Error (409 Conflict):**
    ```json
    {
      "error": "ENTITLEMENT_EXHAUSTED",
      "message": "Card quota exceeded for commodity 'Common Rice'. Permitted remaining: 5.00 KG, Requested: 20.00 KG."
    }
    ```

---

### 7. Stock Reconciliation Endpoints (`/reconciliation`)
- `GET /reconciliation` (List reconciliation records)
- `POST /reconciliation` (Record physical stock count and compute variance)
- `PUT /reconciliation/{id}/status` (Update investigation status)

---

### 8. Grievance Redressal Endpoints (`/complaints`)
- `GET /complaints` (Filter by status, category, shop)
- `POST /complaints` (Register complaint)
- `PUT /complaints/{id}/assign` (Assign officer)
- `PUT /complaints/{id}/resolve` (Record resolution notes)
- `GET /complaints/{id}/history` (Audit log of transitions)

---

### 9. AI Intelligence Engine Endpoints (`http://localhost:8000/api/v1/ai`)
- `GET /api/v1/ai/anomalies` (Scan taluk shops and return anomaly scoreboards)
- `GET /api/v1/ai/forecast?shopId=12&commodityId=1` (Predict 30-day requirement and days of inventory)
- `POST /api/v1/ai/classify-complaint`
  - Input: `{ "text": "Shopkeeper refused to give 20kg rice and claimed no stock", "language": "en" }`
  - Output: Category, sentiment, urgency, and suggested priority.
- `POST /api/v1/ai/officer-assistant`
  - Natural language read-only Q&A over current system metrics.
