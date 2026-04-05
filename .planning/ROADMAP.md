# ROADMAP.md — Pharmacy Stock Management System

> **Methodology:** GSD (Get Shit Done)  
> **Architecture:** Pure Layered OOP — Swing UI → Native Service Controller → Native DAO → Local JDBC → MySQL  
> **Design Patterns Required:** Builder, Factory, Singleton, DAO  
> **Entity Scope:** ENHANCED completely to 7 tables (Drug, Category, User, Purchase, Sale, SaleItem, Expiry)  
> **Java Version:** 25

---

## Phase Overview

| # | Phase | Focus | Status |
|---|-------|-------|--------|
| 1 | Domain Entities & Design Patterns | Core POJO model + Builder, Factory, Singleton | ✅ Completed |
| 2 | Pure DAO & Database Layer (JDBC) | Data access layer using PreparedStatement | ✅ Completed |
| 3 | Core Service Layer (Business Logic) | Safe transaction flows routing via DAOs | ✅ Completed |
| 4 | Swing UI Action Controllers | Integrating forms with pure Service handlers | 🟡 In Progress |

---

## Phase 1 — Domain Entities & Design Patterns

**Goal:** Establish the core structural domain objects and implement raw, un-frameworked Design Patterns.

### Plan 1.1 — Entities Implementation (Native POJO)
- Write pure encapsulated Java models for the base 7 tables.
- Refactor the Database map out to rely purely manually handled queries (encapsulated logic).
- Extract Expiry logic into Expiry component.

### Plan 1.2 — Core Architecture & Patterns
- **DrugBuilder**: Creates `Drug` without telescoping constructors.
- **TransactionFactory**: Enforces transaction setup (Date initialization).
- **AppLogger**: Double-checked locking Singleton to log manually instead of 3rd party frameworks.

---

## Phase 2 — Pure DAO & Database Layer

**Goal:** Secure database communication through strict Data Access Objects using Vanilla JDBC.

### Plan 2.1 — Singleton DB Connection
- Setup `DBConnection` using JDBC bridging directly to MySQL.

### Plan 2.2 — Building Custom DAOs
- Use `PreparedStatement` internally across `DrugDAO`, `SaleDAO` and others to eradicate SQL Injection possibility.

---

## Phase 3 — Core Service Layer (Business Logic)

**Goal:** Form a robust business logic layer that handles computation, checking logic, and cross-DAO interactions.

### Plan 3.1 — Business Logics Assembly
- `DrugService`: Validate inputs and route safely.
- `SaleService`: Safely coordinates complex deductions to `DrugDAO` during a sale event, maintaining stock.
- `ExpiryService`: Perform computational scans to flag days remaining. Nothing leaks to the UI level.

---

## Phase 4 — Swing UI Action Controllers & Presenter Patterns

**Goal:** Take existing Student-Designed UI logic (the `.java` Swing classes) and surgically insert proper architecture hooks that trigger the `Service` classes. NO SQL in ActionListeners.

### Plan 4.1 — User Authentication Routing
- Wire up the `LoginForm` login button.
- Delegate UI values securely safely to `UserService.authenticate()`.

### Plan 4.2 — Operational Dashboards binding
- Add item listeners to pull `CategoryService` structures dynamically.
- Hand off Sales processing requests to `SaleService.createSale()`.
