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
| **Stack** | Java 25 · Spring Boot · Spring Data JPA · MySQL · Maven · Java Swing |
| **Paradigm** | Layered Architecture: Swing UI → REST API → Controller → Service → DAO → MySQL |

---

## Current Phase

| Field | Value |
|-------|-------|
| **Active Phase** | Phase 5 — Dashboard & Presenter Mode (New) |
| **Status** | 🟢 ALL CORE PHASES COMPLETED |
| **Blocking Issue** | None — System is fully operational |

---

## Entity Model (7 Tables - Upgraded)

| Entity | Key Fields |
|--------|-----------|
| `Drug` | barcode, name, type, dose, costPrice, sellingPrice, stockQuantity, productionDate, expirationDate, categoryId, prescriptionType |
| `Category` | id, name, description |
| `User` | id, username, password, role |
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

## Hard Constraints (Updated)

- ✅ **Users entity** added for basic plain-text login (dashboard requirement).
- ✅ **Category entity** added for Figma UI alignment.
- ❌ NO Spring Security (kept plain-text for simplicity).
- ❌ NO hard-coded Swing layouts (no `GroupLayout`, no `setBounds`).
- ✅ Swing UI communicates with backend via REST API only (`ApiClient`).
- ✅ GUI forms designed manually by student via drag-and-drop GUI Builder.

---

## Completed Steps

- [x] GSD framework installed (`.agents/`)
- [x] STATE.md initialized
- [x] ROADMAP.md created
- [x] Execute Phase 1: Domain Entities & Design Patterns (including Category and User)
- [x] Execute Phase 2: DAO & Service Layers
- [x] Execute Phase 3: REST API Controllers
- [x] Execute Phase 4: Frontend API Clients & Swing Event Logic
- [x] Create Presentation Battle Plan (`PROJE_TEKNIK_REHBER_VE_SUNUM_STRATEJISI.md`)

## Pending Steps

- [ ] **Java 25'e Tekrar Geçiş Denemesi** (ÖNEMLİ: `pom.xml` dosyasındaki `java.version` 25 yapılıp çalıştırılarak test edilecek)
- [ ] Complete Frontend GUI binding in IntelliJ IDEA (ÖNEMLİ: Agent **KESİNLİKLE UI tasarımı veya kodlaması YAPMAYACAKTIR**. Tüm arayüz öğrenci tarafından IntelliJ GUI Builder ile sürükle-bırak yöntemiyle oluşturulacak. Agent sadece Endpointleri ve Event metodlarını bağlayacaktır.)
- [ ] Prepare for final presentation using the Battle Plan

---

## File Registry

| File | Purpose |
|------|---------|
| `.planning/STATE.md` | Session state and project context |
| `.planning/ROADMAP.md` | Phase-by-phase execution plan |

---

*GSD Methodology — Get Shit Done*
