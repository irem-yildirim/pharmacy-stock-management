# STATE.md — Pharmacy Stock Management System

> **Last Updated:** 2026-04-05
> **Session:** 2 — Architecting UI & Vanilla MVC Interactions
> **Status:** 🟢 IN PROGRESS — UI Action Binding

---

## Project Identity

| Field | Value |
|-------|-------|
| **Name** | Pharmacy Stock Management System |
| **Type** | University Course Project |
| **Goal** | Manage drug inventory, sales, and expiration dates utilizing hardcore Vanilla Java principles |
| **Stack** | Java 25 · MySQL · Maven · Vanilla Java Swing (JDBC) |
| **Paradigm** | Native Layered Architecture: Swing UI → Service Layer → Generic DAO → JDBC → MySQL |

---

## Current Phase

| Field | Value |
|-------|-------|
| **Active Phase** | Phase 4 — Swing UI Form Implementations & Action Listeners |
| **Status** | 🟡 INTEGRATING UI COMPONENTS WITH BUSINESS LOGIC |
| **Blocking Issue** | Connecting GUI buttons/forms with Service methods safely |

---

## Entity Model (7 Tables - Vanilla POJO)

| Entity | Key Fields |
|--------|-----------|
| `Drug` | barcode, name, dose, costPrice, sellingPrice, stockQuantity |
| `Category` | id, name, description |
| `User` | id, username, password, role |
| `Purchase` | id, drugId, quantityAdded, purchaseDate |
| `Sale` | id, totalAmount, saleDate |
| `SaleItem` | id, saleId, drugId, quantity, unitPrice |
| `Expiry` | id, drugId, daysRemaining, status |

---

## Design Patterns (Manual Hand-Implementation)

| Pattern | Implementation Target | Purpose |
|---------|-----------------------|---------|
| **Builder** | `DrugBuilder` class | Eliminates telescoping constructor problem |
| **Factory** | `TransactionFactory` | Isolates standard object creation safely |
| **Singleton** | `AppLogger` & `DBConnection` | Centralizes native resources and states |
| **DAO** | `Native Custom DAOs` | Securely manages SQL generation out of sight from business logic |

---

## Hard Constraints (Vanilla OOP Focus)

- ✅ **No Framework Abstractions:** Project actively refuses Spring Boot/JPA to demonstrate pure computer science structural intelligence and raw Object-Oriented patterns.
- ✅ **Swing UI Layer Separation (Desktop MVC):** All View code stays purely graphical. Events route to backend Service classes without touching database protocols.
- ✅ **JDBC Prepared Statements Only:** Extreme protection against database manipulation natively.

---

## Completed Steps

- [x] GSD framework setup initialized.
- [x] Java 25 compilation and property configuration (`pom.xml`).
- [x] Build core JDBC engine logic (`DBConnection`).
- [x] 7 POJO base classes finalized successfully without annotations.
- [x] Implemented Native Database Access Layer (DAOs).
- [x] Completed Pure Business Services (`UserService`, `DrugService`, etc).

## Pending Steps (Action Items)

- [ ] Connect `LoginFrame` interactions to `UserService`.
- [ ] Connect graphical `DrugForm` save buttons to `DrugService` capabilities.
- [ ] Build multi-table action logic for `SaleService`.

---

*GSD Methodology — Get Shit Done*
