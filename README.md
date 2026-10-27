# 🏛️ PDS Ration Intelligence
### AI-Powered Public Distribution System — Ration Shop Stock & Entitlement Management

An intelligent, data-driven platform designed to improve transparency, stock management, ration distribution, and entitlement monitoring across the Public Distribution System (PDS).

The system helps administrators monitor fair-price shops, manage ration card holders, track commodity stock, process monthly entitlements, manage godown dispatches, and identify potential stock discrepancies using AI-assisted analytics.

---

## 📌 Project Overview

The **PDS Ration Intelligence System** is designed for taluk-level Public Distribution System management, covering approximately **120 fair-price shops and 1,00,000 ration cards** in a demonstration environment.

The platform combines a modern web dashboard, a secure backend, an Oracle relational database, and an AI service to support data-driven decisions.

### 🎯 Objectives

- Improve visibility into ration stock and commodity distribution.
- Track monthly entitlements and distribution status.
- Monitor stock movement from godowns to fair-price shops.
- Identify discrepancies between recorded stock and physical stock.
- Forecast commodity demand using historical data.
- Classify complaints in Tamil and English.
- Provide officers with read-only, AI-assisted operational insights.
- Improve accountability through role-based access and audit logs.

> **Note:** The scale and operational figures above describe the intended demonstration scope, not verified live government data.

---

## ✨ Key Features

### 1. 📊 Intelligent Dashboard
- Overview of fair-price shops, ration cards, and commodity stocks.
- Low-stock alerts and stock discrepancy indicators.
- Complaint status and pending case summaries.
- Allotment, dispatch, and distribution monitoring.
- Visual analytics and trend charts.

### 2. 👨‍👩‍👧 Ration Card Management
- Manage ration card holder records.
- Maintain family member details.
- Organize cards by card type.
- View monthly entitlement and distribution history.
- Track monthly drawal status.

### 3. 📦 Stock & Inventory Management
- Track commodity stock at godowns and fair-price shops.
- Record stock receipts, sales, and transfers.
- Monitor allotments and dispatches.
- Compare calculated stock against recorded physical stock.
- Highlight low-stock conditions and unusual stock movements.

### 4. ⚖️ Entitlement Management
- Calculate monthly commodity entitlements using configurable rules.
- Support entitlement calculations based on card type, family size, and commodity rules.
- Track allocated quantities and actual distribution.
- Prevent distribution beyond the applicable remaining entitlement.

**Important:** Official entitlement quantities and eligibility rules must be configured and verified against applicable government regulations. Demonstration data must not be treated as official policy.

### 5. 🚚 Godown & Dispatch Management
- Maintain godown inventory.
- Manage dispatches to fair-price shops.
- Track dispatch quantities and receiving records.
- Reconcile dispatched, received, and available stock.
- Identify discrepancies for authorized officer review.

### 6. 🧾 Sales & Reconciliation
- Record commodity distribution transactions.
- Maintain item-level sales details.
- Update stock movements consistently.
- Prevent conflicting stock updates through database transaction controls.
- Maintain records for stock reconciliation and auditing.

### 7. 📝 Complaint Management
- Register and track public complaints.
- Categorize complaints by type and status.
- Maintain complaint history.
- Support Tamil and English complaint classification.
- Help officers prioritize complaints for review.

### 8. 🤖 AI-Powered Intelligence

| AI Feature | Purpose |
|---|---|
| Stock Anomaly Detection | Flag unusual stock movements and reconciliation differences. |
| Demand Forecasting | Estimate future commodity demand using historical consumption data. |
| Complaint Classification | Categorize Tamil and English complaints to support faster routing. |
| Officer Assistant | Answer operational questions using authorized, read-only system data. |
| Low-Stock Prediction | Identify shops that may require replenishment. |

**Responsible AI:** AI-generated alerts are decision-support signals, not proof of theft, fraud, or wrongdoing. Authorized personnel must review findings before taking action. The AI assistant must not independently modify official records.

---

## 🛠️ Technology Stack

### Frontend
- React.js
- TypeScript
- Vite
- Recharts
- HTML5
- CSS3

### Backend
- Java
- Spring Boot
- Spring Data JPA
- Spring Security
- REST APIs

### Database
- Oracle Database
- Oracle SQL
- PL/SQL
- Stored procedures
- Database transactions
- Audit logging

### AI Service
- Python
- FastAPI
- Pandas
- Scikit-learn
- Natural Language Processing (NLP)

### Testing & Development
- JUnit
- Git
- GitHub
- Antigravity IDE

---

## 🏗️ System Architecture

```text
                 ┌───────────────────────────┐
                 │       Web Browser         │
                 └─────────────┬─────────────┘
                               │
                               ▼
                 ┌───────────────────────────┐
                 │ React + TypeScript + Vite │
                 │ Dashboard and UI          │
                 └─────────────┬─────────────┘
                               │ REST API
                               ▼
                 ┌───────────────────────────┐
                 │ Spring Boot Backend       │
                 │ Security and Business     │
                 │ Logic                     │
                 └──────────┬────────┬───────┘
                            │        │
                  ┌─────────▼───┐  ┌─▼────────────────┐
                  │ Oracle DB   │  │ Python FastAPI   │
                  │ SQL/PLSQL   │  │ AI and Analytics │
                  └─────────────┘  └──────────────────┘
```

---

## 📂 Project Structure

The intended project organization is:

```text
PDS-Ration-Intelligence/
│
├── frontend/
│   ├── src/
│   ├── public/
│   ├── package.json
│   └── vite.config.ts
│
├── backend/
│   ├── src/
│   │   ├── main/
│   │   └── test/
│   └── pom.xml
│
├── database/
│   ├── schema/
│   ├── procedures/
│   ├── triggers/
│   └── seed/
│
├── ai-service/
│   ├── app/
│   ├── models/
│   └── requirements.txt
│
├── docs/
│   ├── architecture/
│   └── project-documentation/
│
├── .gitignore
└── README.md
```

*This is the intended structure. The actual folders and filenames may differ depending on your current implementation.*

---

## 🗃️ Database Design

The database is designed around the following entities:

| Entity | Purpose |
|---|---|
| `CARD_HOLDERS` | Ration card holder information |
| `CARD_TYPES` | Categories of ration cards |
| `FAMILY_MEMBERS` | Family member records |
| `SHOPS` | Fair-price shop information |
| `COMMODITIES` | Commodity master data |
| `ENTITLEMENTS` | Monthly entitlement records |
| `ENTITLEMENT_RULES` | Configurable entitlement rules |
| `ALLOTMENTS` | Commodity allotments to shops |
| `SHOP_STOCK` | Fair-price shop inventory |
| `GODOWNS` | Godown information |
| `GODOWN_STOCK` | Commodity stock at godowns |
| `DISPATCHES` | Godown-to-shop dispatch records |
| `DISPATCH_ITEMS` | Commodity details for dispatches |
| `SALES` | Distribution transactions |
| `SALE_ITEMS` | Commodity-level sale details |
| `STOCK_MOVEMENTS` | Inventory movement history |
| `COMPLAINTS` | Public complaint records |
| `COMPLAINT_HISTORY` | Complaint status history |
| `USERS` | Application user accounts |
| `ROLES` | User role definitions |
| `USER_ROLES` | User-role assignments |
| `AUDIT_LOGS` | Auditable application activities |

The final schema should be aligned with the tables and migrations implemented in the project.

---

## 🔐 Security & Data Integrity

The system is designed to support:

- Role-based access control.
- Authentication and authorization.
- Input validation and secure API handling.
- Database transaction management.
- Stock updates with appropriate concurrency controls.
- Audit logs for important operations.
- Separation of read-only AI insights from authorized record-changing operations.
- Protection of personal and sensitive ration card information.

Use fictional demonstration records when sharing screenshots, demonstrations, or source code publicly. Never commit passwords, API keys, database credentials, or real beneficiary data to GitHub.

---

## ⚙️ Installation & Setup

### Prerequisites

Install the following tools before running the project:

- Git
- Node.js and npm
- Java Development Kit (JDK)
- Maven, if it is not included through the project wrapper
- Oracle Database
- Python
- A code editor such as Antigravity

Confirm that your installed versions are compatible with the project's configuration.

### 1. Clone the Repository

```bash
git clone https://github.com/brindha6797/PDS-Ration-Intelligence.git
cd PDS-Ration-Intelligence
```

### 2. Configure the Oracle Database

1. Start your Oracle Database.
2. Create or select a development schema.
3. Configure the database connection using environment variables or an untracked local configuration file.
4. Run the project's schema scripts in the correct order.
5. Load fictional demonstration data if seed scripts are available.

Do not commit database passwords or production credentials.

### 3. Run the Backend

Open a terminal in the backend directory:

```bash
cd backend
```

If the project includes Maven Wrapper on Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

Alternatively, if Maven is installed:

```bash
mvn spring-boot:run
```

Configure Oracle connection details before starting the application.

### 4. Run the Frontend

Open another terminal:

```bash
cd frontend
npm install
npm run dev
```

Open the local development URL printed by Vite in your terminal.

### 5. Run the AI Service

Open another terminal:

```bash
cd ai-service
python -m venv .venv
```

Activate the environment on Windows PowerShell:

```powershell
.\.venv\Scripts\Activate.ps1
```

Install the dependencies:

```bash
pip install -r requirements.txt
```

If the service exposes `app.main:app`, start it with:

```bash
uvicorn app.main:app --reload
```

The AI service entry point and commands may need adjustment to match the actual implementation.

---

## 🧪 Testing

Run the tests supported by the project.

### Backend

```bash
cd backend
mvn test
```

### Frontend

```bash
cd frontend
npm run build
```

### AI Service

Use the test framework and commands configured in the AI service. Validate models against representative test data before using their outputs for operational decisions.

---

## 📈 Expected Benefits

- Improved visibility into stock availability.
- Faster identification of reconciliation discrepancies.
- Better tracking of monthly entitlements and distribution.
- More informed commodity replenishment planning.
- More organized complaint handling.
- Stronger accountability through traceable transactions and audit records.
- Better decision support for authorized PDS officers.

These are intended benefits; actual results must be established through testing and evaluation.

---

## 🚀 Future Enhancements

- Tamil-first interface and accessibility improvements.
- Offline-friendly data entry with controlled synchronization.
- SMS or notification integration for relevant alerts.
- Advanced commodity demand forecasting.
- Improved complaint prioritization.
- Exportable reconciliation and performance reports.
- Integration with authorized government systems, subject to approval and applicable regulations.

---

## 👩‍💻 Development & Contribution

This project is maintained as a software development project focused on improving the monitoring and management of Public Distribution System operations.

Contributions should follow the project's coding standards, security requirements, and data-protection practices.

1. Create a feature branch.
2. Implement and test your changes.
3. Review the changes for security and data integrity.
4. Submit a pull request with a clear description.

---

## ⚠️ Disclaimer

This project is a software prototype for educational, development, or demonstration purposes. It is not an official government application and is not affiliated with or endorsed by any government department unless formally authorized.

All demonstration data should be fictional. Official entitlement rules, beneficiary records, and government integrations must be validated and authorized before real-world deployment.

---

## 📄 License

No license has been specified yet. Unless a license is added to this repository, reuse and redistribution are subject to applicable copyright law.

---

### Built with a focus on Transparency, Accountability, and Intelligent Public Distribution.
