# STATE.md — Pharmacy Stock Management System

> **Last Updated:** 2026-03-09  
> **Session:** 1 — Initial Planning  
> **Status:** 🟡 PLANNING — Awaiting user approval of ROADMAP.md

---

## Project Identity

| Field | Value |
|-------|-------|
| **Name** | Pharmacy Stock Management System |
| **Type** | University Course Project |
| **Goal** | Manage drug inventory, sales, and expiration dates |
| **Stack** | Java 17 · Spring Boot · Spring Data JPA · MySQL · Maven · Java Swing |
| **Paradigm** | Layered Architecture: Swing UI → REST API → Controller → Service → DAO → MySQL |

---

## Current Phase

| Field | Value |
|-------|-------|
| **Active Phase** | Phase 0 — Planning & Initialization |
| **Next Phase** | Phase 1 — Domain Entities & Design Patterns |
| **Blocking Issue** | Awaiting user approval of ROADMAP.md |

---

## Entity Model (Strictly 5 Tables)

| Entity | Key Fields |
|--------|-----------|
| `Drug` | barcode, name, type, dose, costPrice, sellingPrice, stockQuantity, productionDate, expirationDate |
| `Purchase` | id, drugId, quantityAdded, purchaseDate |
| `Sale` | id, totalAmount, saleDate |
| `SaleItem` (HistorySale) | id, saleId, drugId, quantity, unitPrice |
| `Expiry` | id, drugId, daysRemaining, status |

---

## Design Patterns (Required for Report)

| Pattern | Implementation Target | Purpose |
|---------|-----------------------|---------|
| **Builder** | `DrugBuilder` class | Solves telescoping constructor problem for `Drug` |
| **Factory** | `TransactionFactory` | Creates `Sale`/`Purchase` with auto-injected `LocalDate.now()` |
| **Singleton** | `AppLogger` class | Global logging mechanism |
| **DAO** | `DrugDAO`, `SaleDAO`, etc. | Spring Data JPA interfaces with DAO suffix |

---

## Hard Constraints

- ❌ NO Users entity, NO Login system, NO Companies, NO Messages
- ❌ NO hard-coded Swing layouts (no `GroupLayout`, no `setBounds`)
- ✅ Swing UI communicates with backend via REST API only (`ApiClient`)
- ✅ GUI forms designed manually by student via drag-and-drop GUI Builder

---

## Completed Steps

- [x] GSD framework installed (`.agents/`)
- [x] STATE.md initialized
- [x] ROADMAP.md created

## Pending Steps

- [ ] User approves ROADMAP.md
- [ ] Execute Phase 1: Domain Entities & Design Patterns
- [ ] Execute Phase 2: DAO & Service Layers
- [ ] Execute Phase 3: REST API Controllers
- [ ] Execute Phase 4: Frontend API Clients & Swing Event Logic

---

## File Registry

| File | Purpose |
|------|---------|
| `.planning/STATE.md` | Session state and project context |
| `.planning/ROADMAP.md` | Phase-by-phase execution plan |

---

*GSD Methodology — Get Shit Done*
