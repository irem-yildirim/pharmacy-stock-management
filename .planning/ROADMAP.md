# ROADMAP.md — Pharmacy Stock Management System

> **Methodology:** GSD (Get Shit Done)  
> **Architecture:** Layered — Swing UI → REST API → Controller → Service → DAO → MySQL  
> **Design Patterns Required:** Builder, Factory, Singleton, DAO  
> **Entity Scope:** ENHANCED completely to 7 tables (Drug, Category, User, Purchase, Sale, SaleItem, Expiry)  
> **Java Version:** 17

---

## Phase Overview

| # | Phase | Focus | Status |
|---|-------|-------|--------|
| 1 | Domain Entities & Design Patterns | Core model + Builder, Factory, Singleton | ✅ Completed |
| 2 | DAO & Service Layers | Data access + Business logic | ✅ Completed |
| 3 | REST API Controllers | HTTP endpoints for all operations | ✅ Completed |
| 4 | Frontend API Clients & Swing Event Logic | ApiClient utilities + button handlers | ✅ Completed |
| 5 | Dashboard & Figma UI Alignment | Category lookup, User Login logic | ✅ Completed |

---

## Phase 1 — Domain Entities & Design Patterns

**Goal:** Establish the core domain model with all 7 JPA entities and implement the 3 creational design patterns required for the course report.

**Must-Haves:**
- All 7 entities compiled and mapped to MySQL tables via JPA
- `DrugBuilder` demonstrates Builder pattern (solves telescoping constructor)
- `TransactionFactory` demonstrates Factory pattern (auto-injects `LocalDate.now()`)
- `AppLogger` demonstrates Singleton pattern (global logging)
- Project builds with `mvn clean compile`

### Plan 1.1 — Maven Project Structure & Spring Boot Bootstrap

> **Wave:** 1 | **Autonomous:** true

**Files:**
- `pom.xml`
- `src/main/resources/application.properties`
- `src/main/java/com/pharmacy/PharmacyApplication.java`

**Tasks:**

<task type="auto">
  <name>Bootstrap Maven Spring Boot project</name>
  <files>pom.xml</files>
  <action>
    Create pom.xml with:
    - Parent: spring-boot-starter-parent 3.x
    - Dependencies: spring-boot-starter-web, spring-boot-starter-data-jpa, mysql-connector-java, lombok
    - Java version: 17
    AVOID: Adding spring-security — this project has no authentication layer.
  </action>
  <verify>mvn clean compile -q exits with code 0</verify>
  <done>✅ Project compiles. No compilation errors.</done>
</task>

<task type="auto">
  <name>Configure application.properties for MySQL</name>
  <files>src/main/resources/application.properties</files>
  <action>
    Set spring.datasource.url, username, password for localhost MySQL.
    Set spring.jpa.hibernate.ddl-auto=update.
    Set spring.jpa.show-sql=true.
    AVOID: ddl-auto=create-drop (wipes data on restart).
  </action>
  <verify>Application starts without DataSource errors.</verify>
  <done>✅ MySQL connection established on startup.</done>
</task>

---

### Plan 1.2 — JPA Entity Classes (7 Tables)

> **Wave:** 2 | **Depends on:** Plan 1.1 | **Autonomous:** true

**Files:**
- `src/main/java/com/pharmacy/entity/Drug.java`
- `src/main/java/com/pharmacy/entity/Category.java` (NEW)
- `src/main/java/com/pharmacy/entity/User.java` (NEW)
- `src/main/java/com/pharmacy/entity/Purchase.java`
- `src/main/java/com/pharmacy/entity/Sale.java`
- `src/main/java/com/pharmacy/entity/SaleItem.java`
- `src/main/java/com/pharmacy/entity/Expiry.java`

**Tasks:**

<task type="auto">
  <name>Create all 7 JPA entity classes</name>
  <files>src/main/java/com/pharmacy/entity/*.java</files>
  <action>
    Drug: @Entity, fields — barcode(String,@Id), name, type, dose, costPrice(BigDecimal), sellingPrice(BigDecimal), stockQuantity(int), productionDate(LocalDate), expirationDate(LocalDate), @ManyToOne Category, prescriptionType(String). Use @Column constraints.
    Category: @Entity, id, name, description.
    User: @Entity, id, username, password, role.
    Purchase: @Entity, @GeneratedValue id, @ManyToOne Drug drug, quantityAdded(int), purchaseDate(LocalDate).
    Sale: @Entity, @GeneratedValue id, totalAmount(BigDecimal), saleDate(LocalDate), @OneToMany List<SaleItem> items.
    SaleItem: @Entity, @GeneratedValue id, @ManyToOne Sale, @ManyToOne Drug, quantity(int), unitPrice(BigDecimal).
    Expiry: @Entity, @GeneratedValue id, @OneToOne Drug drug, daysRemaining(long), status(String — e.g. "EXPIRED","CRITICAL","OK").
    Use @Lombok @Data, @NoArgsConstructor, @AllArgsConstructor on all core entities.
    AVOID: Circular JSON serialization — use @JsonIgnore on back-references.
  </action>
  <verify>mvn clean compile -q exits with code 0. Tables auto-created in MySQL.</verify>
  <done>✅ All 7 entity classes compile. DB tables created via JPA ddl-auto=update.</done>
</task>

---

### Plan 1.3 — Design Patterns Implementation

> **Wave:** 3 | **Depends on:** Plan 1.2 | **Autonomous:** true

**Files:**
- `src/main/java/com/pharmacy/pattern/DrugBuilder.java`
- `src/main/java/com/pharmacy/pattern/TransactionFactory.java`
- `src/main/java/com/pharmacy/pattern/AppLogger.java`

**Tasks:**

<task type="auto">
  <name>Implement DrugBuilder (Builder Pattern)</name>
  <files>src/main/java/com/pharmacy/pattern/DrugBuilder.java</files>
  <action>
    Implement classic Builder pattern for Drug entity.
    Inner static class Builder with all Drug fields including logic for category() and prescriptionType().
    Final build() method returns a populated Drug instance.
    Include a class-level Javadoc comment: "Implements the Builder pattern to solve the telescoping constructor problem for Drug instantiation."
    AVOID: Using Lombok @Builder — this must be a hand-crafted Builder for academic demonstration.
  </action>
  <verify>Unit usage: new DrugBuilder().name("Aspirin").barcode("123").build() returns non-null Drug.</verify>
  <done>✅ DrugBuilder class compiles. Can build a Drug without calling any constructor directly.</done>
</task>

<task type="auto">
  <name>Implement TransactionFactory (Factory Pattern)</name>
  <files>src/main/java/com/pharmacy/pattern/TransactionFactory.java</files>
  <action>
    Implement Factory pattern with two static factory methods:
    - createSale(BigDecimal totalAmount): returns new Sale with saleDate = LocalDate.now(), id auto-assigned by JPA.
    - createPurchase(Drug drug, int quantity): returns new Purchase with purchaseDate = LocalDate.now().
    Include class-level Javadoc: "Implements the Factory pattern to encapsulate creation of Sale and Purchase objects, automatically injecting the current date."
    AVOID: Injecting Spring beans into this class — keep it a pure static factory (no @Component).
  </action>
  <verify>TransactionFactory.createSale(new BigDecimal("50")) returns Sale with non-null saleDate.</verify>
  <done>✅ Factory correctly produces Sale and Purchase objects with auto-injected dates.</done>
</task>

<task type="auto">
  <name>Implement AppLogger (Singleton Pattern)</name>
  <files>src/main/java/com/pharmacy/pattern/AppLogger.java</files>
  <action>
    Implement thread-safe Singleton using double-checked locking.
    Private static volatile AppLogger instance.
    Private constructor.
    Public static AppLogger getInstance() with synchronized double-check.
    Methods: log(String message) — prints "[PHARMACY LOG] {timestamp} — {message}" to console.
    logError(String message) — prints "[PHARMACY ERROR] {timestamp} — {message}".
    Include class-level Javadoc: "Implements the Singleton pattern to ensure a single global logging instance throughout the application."
    AVOID: Using SLF4J/Logback as the singleton — the pattern must be handcrafted for academic purposes. SLF4J can be used internally (Logger.info) but the Singleton wrapper must be hand-written.
  </action>
  <verify>AppLogger.getInstance() == AppLogger.getInstance() (same reference). log() prints to console.</verify>
  <done>✅ Single AppLogger instance accessible globally. Two calls to getInstance() return identical object.</done>
</task>

---

## Phase 2 — DAO & Service Layers

**Goal:** Implement Spring Data JPA DAO interfaces (named with DAO suffix per course convention) and Service classes containing all business logic.

**Must-Haves:**
- DAO interfaces named `DrugDAO`, `SaleDAO`, `PurchaseDAO`, `SaleItemDAO`, `ExpiryDAO`, `UserDAO`, `CategoryDAO`
- Service classes with `@Service` annotation
- Business rule: `Expiry.daysRemaining` computed as `ChronoUnit.DAYS.between(LocalDate.now(), drug.expirationDate)`
- `AppLogger.getInstance().log(...)` called in each Service method for audit trail

### Plan 2.1 — DAO Interfaces

> **Wave:** 1 | **Depends on:** Phase 1 complete | **Autonomous:** true

**Files:**
- `src/main/java/com/pharmacy/dao/DrugDAO.java`
- `src/main/java/com/pharmacy/dao/PurchaseDAO.java`
- `src/main/java/com/pharmacy/dao/SaleDAO.java`
- `src/main/java/com/pharmacy/dao/SaleItemDAO.java`
- `src/main/java/com/pharmacy/dao/ExpiryDAO.java`
- `src/main/java/com/pharmacy/dao/UserDAO.java` (NEW)
- `src/main/java/com/pharmacy/dao/CategoryDAO.java` (NEW)

**Tasks:**

<task type="auto">
  <name>Create all 7 DAO interfaces extending JpaRepository</name>
  <files>src/main/java/com/pharmacy/dao/*.java</files>
  <action>
    Each interface extends JpaRepository<Entity, IdType>.
    DrugDAO: add findByName(String name), findByExpirationDateBefore(LocalDate date), findByCategory_Id, findByPrescriptionType.
    SaleDAO: add findBySaleDateBetween(LocalDate start, LocalDate end).
    PurchaseDAO: add findByDrug_Barcode(String barcode).
    SaleItemDAO: add findBySale_Id(Long saleId).
    ExpiryDAO: add findByStatus(String status).
    UserDAO: findByUsername(String username).
    CategoryDAO: findByName(String name).
    Name interfaces with DAO suffix — NOT Repository.
    AVOID: Renaming to XxxRepository — course requires DAO suffix.
  </action>
  <verify>mvn clean compile -q exits 0. Spring context loads all DAOs as beans.</verify>
  <done>✅ All 7 DAO interfaces compiled and recognized by Spring Data JPA.</done>
</task>

---

### Plan 2.2 — Service Layer (Business Logic)

> **Wave:** 2 | **Depends on:** Plan 2.1 | **Autonomous:** true

**Files:**
- `src/main/java/com/pharmacy/service/DrugService.java`
- `src/main/java/com/pharmacy/service/SaleService.java`
- `src/main/java/com/pharmacy/service/PurchaseService.java`
- `src/main/java/com/pharmacy/service/ExpiryService.java`
- `src/main/java/com/pharmacy/service/UserService.java` (NEW)
- `src/main/java/com/pharmacy/service/CategoryService.java` (NEW)

**Tasks:**

<task type="auto">
  <name>Implement DrugService with CRUD and stock operations</name>
  <files>src/main/java/com/pharmacy/service/DrugService.java</files>
  <action>
    @Service class injecting DrugDAO via constructor injection.
    Methods: addDrug(Drug), updateDrug(Drug), deleteDrug(String barcode), getAllDrugs(), findByBarcode(String), findExpiringSoon(int daysThreshold), findByCategory(Long), findByPrescriptionType(String).
    Call AppLogger.getInstance().log() at the start of each method.
    AVOID: @Autowired field injection — use constructor injection for testability.
  </action>
  <verify>mvn clean compile -q exits 0.</verify>
  <done>✅ DrugService compiles. CRUD + expiry/category query methods implemented.</done>
</task>

<task type="auto">
  <name>Implement SaleService and PurchaseService with Factory integration</name>
  <files>
    src/main/java/com/pharmacy/service/SaleService.java
    src/main/java/com/pharmacy/service/PurchaseService.java
  </files>
  <action>
    SaleService: createSale(List<SaleItem> items) — uses TransactionFactory.createSale(), computes totalAmount, saves via SaleDAO.
    PurchaseService: addPurchase(String barcode, int qty) — uses TransactionFactory.createPurchase(), updates Drug.stockQuantity, saves via PurchaseDAO and DrugDAO.
    Call AppLogger.getInstance().log() in each method.
    AVOID: Business logic in controllers — all computation stays in services.
  </action>
  <verify>mvn clean compile -q exits 0.</verify>
  <done>✅ Sale and Purchase creation use TransactionFactory. Stock updated on purchase.</done>
</task>

<task type="auto">
  <name>Implement ExpiryService — compute daysRemaining and status</name>
  <files>src/main/java/com/pharmacy/service/ExpiryService.java</files>
  <action>
    @Service class with ExpiryDAO and DrugDAO injected.
    refreshExpiry(): iterate all drugs, compute daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), drug.getExpirationDate()), set status: "EXPIRED" if <=0, "CRITICAL" if <=30, "OK" otherwise. Save Expiry via ExpiryDAO.
    getExpiredDrugs(): returns all Expiry records with status="EXPIRED".
    getCriticalDrugs(): returns all Expiry with status="CRITICAL".
    Call AppLogger.getInstance().log() in each method.
    AVOID: Hardcoding the 30-day threshold as a magic number — define as a constant.
  </action>
  <verify>mvn clean compile -q exits 0. refreshExpiry() populates expiry table correctly.</verify>
  <done>✅ ExpiryService correctly classifies drugs as EXPIRED/CRITICAL/OK based on current date.</done>
</task>

---

## Phase 3 — REST API Controllers

**Goal:** Expose all business logic as REST endpoints. Controllers are thin — zero business logic, delegate everything to services.

**Must-Haves:**
- All endpoints follow RESTful conventions
- All responses wrapped in `ResponseEntity<>`
- Controllers contain NO business logic (pure delegation to services)
- Application runs and responds to HTTP requests

### Plan 3.1 — Drug, Expiry, Category & User Controllers

> **Wave:** 1 | **Depends on:** Phase 2 complete | **Autonomous:** true

**Files:**
- `src/main/java/com/pharmacy/controller/DrugController.java`
- `src/main/java/com/pharmacy/controller/ExpiryController.java`
- `src/main/java/com/pharmacy/controller/CategoryController.java` (NEW)
- `src/main/java/com/pharmacy/controller/UserController.java` (NEW)

**Tasks:**

<task type="auto">
  <name>Create Main Controllers</name>
  <files>src/main/java/com/pharmacy/controller/*.java</files>
  <action>
    Export CRUD bindings.
    Drug: /api/drugs + category and prescriptionType filters.
    Expiry: /api/expiry/refresh + expired + critical.
    Category: /api/categories
    User: /api/users/login (produces User object if auth succeeds)
  </action>
  <verify>curl runs properly to these URLs.</verify>
  <done>✅ All controllers built and REST points map properly to services.</done>
</task>

---

### Plan 3.2 — Sale & Purchase Controllers

> **Wave:** 1 | **Depends on:** Phase 2 complete | **Autonomous:** true

**Files:**
- `src/main/java/com/pharmacy/controller/SaleController.java`
- `src/main/java/com/pharmacy/controller/PurchaseController.java`

**Tasks:**

<task type="auto">
  <name>Create Transaction Controllers</name>
  <files>src/main/java/com/pharmacy/controller/*Controller.java</files>
  <action>
    Sale: /api/sales
    Purchase: /api/purchases
  </action>
  <verify>POST /api/sales saves valid bodies.</verify>
  <done>✅ Sale and Purchase records can be POSTed.</done>
</task>

---

## Phase 4 — Frontend API Clients & Swing Event Logic

**Goal:** Provide pure Java utility classes (`ApiClient`) and short button-click snippets for Swing forms. NO layout code.

**Hard Constraint:** DO NOT generate any `JFrame`, `JPanel`, `GroupLayout`, or `setBounds()` code. KESİNLİKLE UI TASARLAMAN YASAKTIR. ARAYÜZ TAMAMIYLA GUI ÜZERİNDE ELLE (SÜRÜKLE-BIRAK İLE) ÖĞRENCİ TARAFINDAN YAPILACAKTIR. AGENT SADECE ENDPOINTLERİ BAĞLAYACAK.

### Plan 4.1 — ApiClient Utility

> **Wave:** 1 | **Depends on:** Phase 3 complete | **Autonomous:** true

**Files:**
- `src/main/java/com/pharmacy/client/ApiClient.java`

**Tasks:**

<task type="auto">
  <name>Create ApiClient with HTTP methods</name>
  <files>src/main/java/com/pharmacy/client/ApiClient.java</files>
  <action>
    Utility class handling HTTP communication (get, post, put, delete, login).
  </action>
  <verify>ApiClient methods work.</verify>
  <done>✅ ApiClient is fully functional with native HttpURLConnection.</done>
</task>

---

### Plan 4.2 — Swing Event Logic Snippets

> **Wave:** 2 | **Depends on:** Plan 4.1 | **Autonomous:** true

**Files:**
- `src/main/java/com/pharmacy/client/snippets/DrugFormSnippet.java`
- `src/main/java/com/pharmacy/client/snippets/SaleFormSnippet.java`
- `src/main/java/com/pharmacy/client/snippets/PurchaseFormSnippet.java`
- `src/main/java/com/pharmacy/client/snippets/ExpiryFormSnippet.java`
- `src/main/java/com/pharmacy/client/snippets/LoginFormSnippet.java` (NEW)

**Tasks:**

<task type="auto">
  <name>Create Swing event logic snippets</name>
  <files>src/main/java/com/pharmacy/client/snippets/*.java</files>
  <action>
    Contains ONLY actionPerformed / button click logic (SwingWorker used).
  </action>
  <verify>Snippet compiles.</verify>
  <done>✅ Event logic created for all forms, including Login.</done>
</task>

---

## Phase 5 — Dashboard & Figma UI Alignment (NEW)

**Goal:** Provide the necessary endpoints and relationships to construct a modern Figma-designed Pharmacy Dashboard.

### Plan 5.1 — Enhancing Logic for UI presentation

> **Wave:** 1 | **Depends on:** Phase 4 | **Autonomous:** true

**Tasks:**

<task type="auto">
  <name>Implement User Login and Category lookup</name>
  <action>
    1. Hook the ApiClient.login to a generic Swing UI LoginFormSnippet.
    2. Enhance DrugFormSnippet to pull Categories to populate a JComboBox.
    3. Ensure no Spring Security blocks the plain-text endpoints.
  </action>
  <verify>Project compiles and tables map to DB.</verify>
  <done>✅ All Phase 5 backend hooks and Snippets are fully implemented.</done>
</task>

---

## Completion Criteria

The project is **DONE** when:

- [ ] **Java 25'e Geçiş Denendi** (pom.xml java.version 25 yapılıp stabil çalıştığı doğrulandı)
- [x] `mvn clean package` builds a runnable JAR without errors
- [x] MySQL tables auto-created on first run (7 tables: drug, category, user, purchase, sale, sale_item, expiry)
- [x] All REST endpoints respond correctly (verified via curl or Postman)
- [x] Three design patterns demonstrably instantiated: DrugBuilder, TransactionFactory, AppLogger
- [x] DAO suffix used on all JPA interfaces
- [x] Student can wire Swing form events to ApiClient snippets
- [x] Course presentation battle plan and report logic established

---

*GSD Methodology — Pharmacy Stock Management System v1.5 (Dashboard Edition)*
