package com.pharmacy.dao;

import com.pharmacy.entity.*;
import com.pharmacy.pattern.DrugBuilder;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Startup seed mechanism.
 * Checks each table — if empty, fills it with realistic pharmacy demo data.
 * Safe to call on every launch: never overwrites existing rows.
 */
public class DatabaseSeeder {

    public void seedIfEmpty() {
        System.out.println("[Seeder] Checking database...");
        try {
            createTablesIfNotExist();
            seedUsers();
            seedCategories();
            seedDrugs();
            seedExpiry();
            seedPurchases();
            seedSales();
            System.out.println("[Seeder] Database check complete.");
        } catch (Exception e) {
            System.err.println("[Seeder] ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // =========================================================================
    //  CREATE TABLES (IF NOT EXISTS)
    // =========================================================================
    private void createTablesIfNotExist() {
        System.out.println("  → Ensuring table structure...");
        Connection conn = DBConnection.getInstance().getConnection();
        try (Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS users (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(100),
                    email VARCHAR(100),
                    username VARCHAR(50) UNIQUE,
                    password VARCHAR(100),
                    role VARCHAR(30)
                )
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS category (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(100),
                    description VARCHAR(255)
                )
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS drug (
                    barcode VARCHAR(50) PRIMARY KEY,
                    name VARCHAR(100),
                    dose VARCHAR(50),
                    cost_price DECIMAL(10,2),
                    selling_price DECIMAL(10,2),
                    stock_quantity INT DEFAULT 0,
                    category_id BIGINT,
                    FOREIGN KEY (category_id) REFERENCES category(id)
                )
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS expiry (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    drug_barcode VARCHAR(50),
                    expiration_date DATE,
                    days_remaining BIGINT,
                    status VARCHAR(20),
                    FOREIGN KEY (drug_barcode) REFERENCES drug(barcode)
                )
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS purchase (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    drug_barcode VARCHAR(50),
                    quantity_added INT,
                    purchase_date DATE,
                    FOREIGN KEY (drug_barcode) REFERENCES drug(barcode)
                )
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS sale (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    total_amount DECIMAL(10,2),
                    sale_date DATE
                )
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS sale_item (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    sale_id BIGINT,
                    drug_barcode VARCHAR(50),
                    quantity INT,
                    unit_price DECIMAL(10,2),
                    FOREIGN KEY (sale_id) REFERENCES sale(id),
                    FOREIGN KEY (drug_barcode) REFERENCES drug(barcode)
                )
            """);

            System.out.println("    Tables OK.");
        } catch (SQLException e) {
            System.err.println("  Table creation error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // =========================================================================
    //  USERS
    // =========================================================================
    private void seedUsers() {
        if (!isTableEmpty("users")) return;
        System.out.println("  → Seeding users...");

        UserDAO dao = new UserDAO();
        dao.save(new User(null, "Administrator",    "admin@pharmacy.com",    "admin",   "admin123",   "ADMIN"));
        dao.save(new User(null, "Dr. Sarah Miller", "sarah@pharmacy.com",    "sarah",   "sarah123",   "PHARMACIST"));
        dao.save(new User(null, "John Carter",      "john@pharmacy.com",     "john",    "john123",    "CASHIER"));
    }

    // =========================================================================
    //  CATEGORIES
    // =========================================================================
    private void seedCategories() {
        if (!isTableEmpty("category")) return;
        System.out.println("  → Seeding categories...");

        CategoryDAO dao = new CategoryDAO();
        dao.save(new Category("Painkillers",      "Pain relief and anti-inflammatory drugs"));
        dao.save(new Category("Antibiotics",      "Anti-bacterial infection treatments"));
        dao.save(new Category("Vitamins",         "Vitamin and mineral supplements"));
        dao.save(new Category("Digestive Health", "Stomach, intestinal and digestive medicines"));
        dao.save(new Category("Dermatology",      "Skin care creams and lotions"));
        dao.save(new Category("Cold & Flu",       "Cold, cough and nasal decongestants"));
    }

    // =========================================================================
    //  DRUGS  (category_id: 1=Painkillers, 2=Antibiotics, 3=Vitamins,
    //          4=Digestive, 5=Dermatology, 6=Cold&Flu)
    // =========================================================================
    private void seedDrugs() {
        if (!isTableEmpty("drug")) return;
        System.out.println("  → Seeding drugs...");

        DrugDAO dao = new DrugDAO();
        dao.save(drug("1001", "Paracetamol 500mg",   "500mg",   "3.50",  "6.99",   120, 1L));
        dao.save(drug("1002", "Ibuprofen 400mg",     "400mg",   "4.00",  "8.50",    85, 1L));
        dao.save(drug("1003", "Aspirin 100mg",       "100mg",   "2.50",  "5.25",    60, 1L));
        dao.save(drug("1004", "Amoxicillin 500mg",   "500mg",  "12.00", "24.90",    30, 2L));
        dao.save(drug("1005", "Azithromycin 250mg",  "250mg",  "15.00", "29.50",    45, 2L));
        dao.save(drug("1006", "Ciprofloxacin 500mg", "500mg",  "10.00", "21.90",     7, 2L));
        dao.save(drug("1007", "Vitamin C 1000mg",    "1000mg",  "5.00", "10.50",   200, 3L));
        dao.save(drug("1008", "Vitamin D3 1000IU",   "1000IU",  "6.00", "12.90",   150, 3L));
        dao.save(drug("1009", "Vitamin B12",         "1mg",     "8.00", "16.00",     4, 3L));
        dao.save(drug("1010", "Omeprazole 20mg",     "20mg",   "14.00", "28.50",    40, 4L));
        dao.save(drug("1011", "Antacid Tablets",     "500mg",   "3.00",  "7.25",    90, 4L));
        dao.save(drug("1012", "Hydrocortisone Cream","1%",      "9.00", "18.75",    35, 5L));
        dao.save(drug("1013", "Cetirizine 10mg",     "10mg",    "2.00",  "5.50",    70, 6L));
        dao.save(drug("1014", "Pseudoephedrine 60mg","60mg",    "4.50",  "9.90",     3, 6L));
        dao.save(drug("1015", "Throat Lozenges",     "1.2mg",   "3.00",  "6.75",   110, 6L));
    }

    // =========================================================================
    //  EXPIRY  — some EXPIRED, some CRITICAL (≤30 days), rest OK
    // =========================================================================
    private void seedExpiry() {
        if (!isTableEmpty("expiry")) return;
        System.out.println("  → Seeding expiry records...");

        ExpiryDAO dao = new ExpiryDAO();
        LocalDate today = LocalDate.now();

        expiry(dao, "1001", today.plusDays(365));    // OK
        expiry(dao, "1002", today.plusDays(12));     // CRITICAL — 12 days
        expiry(dao, "1003", today.plusDays(180));    // OK
        expiry(dao, "1004", today.plusDays(20));     // CRITICAL — 20 days
        expiry(dao, "1005", today.plusDays(90));     // OK
        expiry(dao, "1006", today.minusDays(10));    // EXPIRED
        expiry(dao, "1007", today.plusDays(540));    // OK
        expiry(dao, "1008", today.plusDays(420));    // OK
        expiry(dao, "1009", today.plusDays(5));      // CRITICAL — 5 days
        expiry(dao, "1010", today.plusDays(270));    // OK
        expiry(dao, "1011", today.plusDays(60));     // OK
        expiry(dao, "1012", today.minusDays(3));     // EXPIRED
        expiry(dao, "1013", today.plusDays(300));    // OK
        expiry(dao, "1014", today.plusDays(25));     // CRITICAL — 25 days
        expiry(dao, "1015", today.plusDays(150));    // OK
    }

    // =========================================================================
    //  PURCHASES (stock-in history)
    // =========================================================================
    private void seedPurchases() {
        if (!isTableEmpty("purchase")) return;
        System.out.println("  → Seeding purchases...");

        PurchaseDAO dao = new PurchaseDAO();
        LocalDate today = LocalDate.now();

        purchase(dao, "1001", 60,  today.minusDays(30));
        purchase(dao, "1001", 60,  today.minusDays(5));
        purchase(dao, "1004", 30,  today.minusDays(15));
        purchase(dao, "1007", 100, today.minusDays(20));
        purchase(dao, "1007", 100, today.minusDays(2));
        purchase(dao, "1010", 40,  today.minusDays(25));
        purchase(dao, "1013", 70,  today.minusDays(10));
        purchase(dao, "1015", 110, today.minusDays(8));
    }

    // =========================================================================
    //  SALES + SALE_ITEMS (sales history)
    // =========================================================================
    private void seedSales() {
        if (!isTableEmpty("sale")) return;
        System.out.println("  → Seeding sales...");

        SaleDAO saleDAO = new SaleDAO();
        SaleItemDAO itemDAO = new SaleItemDAO();
        LocalDate today = LocalDate.now();

        // Sale 1
        Sale s1 = new Sale(new BigDecimal("20.48"), today.minusDays(3));
        saleDAO.save(s1);
        itemDAO.save(saleItem(s1, "1001", 2, "6.99"));
        itemDAO.save(saleItem(s1, "1013", 1, "5.50"));

        // Sale 2
        Sale s2 = new Sale(new BigDecimal("54.40"), today.minusDays(2));
        saleDAO.save(s2);
        itemDAO.save(saleItem(s2, "1004", 1, "24.90"));
        itemDAO.save(saleItem(s2, "1005", 1, "29.50"));

        // Sale 3
        Sale s3 = new Sale(new BigDecimal("28.25"), today.minusDays(1));
        saleDAO.save(s3);
        itemDAO.save(saleItem(s3, "1007", 2, "10.50"));
        itemDAO.save(saleItem(s3, "1011", 1, "7.25"));

        // Sale 4 (today)
        Sale s4 = new Sale(new BigDecimal("28.90"), today);
        saleDAO.save(s4);
        itemDAO.save(saleItem(s4, "1008", 1, "12.90"));
        itemDAO.save(saleItem(s4, "1009", 1, "16.00"));
    }

    // =========================================================================
    //  HELPER METHODS
    // =========================================================================

    private boolean isTableEmpty(String tableName) {
        String query = "SELECT COUNT(*) FROM " + tableName;
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(query);
                 ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("  Table check error (" + tableName + "): " + e.getMessage());
        }
        return false;
    }

    private Drug drug(String barcode, String name, String dose,
                      String cost, String sell, int qty, Long catId) {
        Drug d = new DrugBuilder()
                .barcode(barcode).name(name).dose(dose)
                .costPrice(new BigDecimal(cost))
                .sellingPrice(new BigDecimal(sell))
                .stockQuantity(qty)
                .build();
        if (catId != null) {
            Category cat = new Category();
            cat.setId(catId);
            d.setCategory(cat);
        }
        return d;
    }

    private void expiry(ExpiryDAO dao, String barcode, LocalDate expDate) {
        Drug d = new Drug();
        d.setBarcode(barcode);
        long days = ChronoUnit.DAYS.between(LocalDate.now(), expDate);
        String status = days <= 0 ? "EXPIRED" : days <= 30 ? "CRITICAL" : "OK";
        dao.save(new Expiry(d, expDate, days, status));
    }

    private void purchase(PurchaseDAO dao, String barcode, int qty, LocalDate date) {
        Drug d = new Drug();
        d.setBarcode(barcode);
        dao.save(new Purchase(d, qty, date));
    }

    private SaleItem saleItem(Sale sale, String barcode, int qty, String price) {
        Drug d = new Drug();
        d.setBarcode(barcode);
        return new SaleItem(sale, d, qty, new BigDecimal(price));
    }
}
