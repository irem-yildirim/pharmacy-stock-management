package com.pharmacy.dao;

import com.pharmacy.entity.*;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;

public class DatabaseSeeder {

    public void seedIfEmpty() {
        System.out.println("[Seeder] Checking database...");
        try {
            createTablesIfNotExist();
            // User requested to drop old test data and seed 40 actual drugs.
            // This runs if sales/drugs are effectively old. If it fails, we catch it.
            // But we only want to drop if the new structural requirements don't map well.
            // Actually, we'll unconditionally drop and reconstruct for this "clean slate".
            if (!isTableEmpty("pres_type") && getCount("pres_type") != 5) {
                wipeDatabase();
            } else if (isTableEmpty("drug")) {
                 wipeDatabase(); // Ensure clean slate if empty.
            }

            seedUsers();
            seedCategories();
            seedBrands();
            seedPresTypes();
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

    private void wipeDatabase() {
        System.out.println("  → Wiping old database for strict 40-drugs seed...");
        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("SET FOREIGN_KEY_CHECKS = 0");
            stmt.executeUpdate("TRUNCATE TABLE sale_item");
            stmt.executeUpdate("TRUNCATE TABLE sale");
            stmt.executeUpdate("TRUNCATE TABLE purchase");
            stmt.executeUpdate("TRUNCATE TABLE expiry");
            stmt.executeUpdate("TRUNCATE TABLE drug");
            stmt.executeUpdate("TRUNCATE TABLE pres_type");
            stmt.executeUpdate("TRUNCATE TABLE brand");
            stmt.executeUpdate("TRUNCATE TABLE category");
            stmt.executeUpdate("TRUNCATE TABLE users");
            try { stmt.executeUpdate("ALTER TABLE drug DROP COLUMN dose"); } catch (Exception ex) {}
            stmt.executeUpdate("SET FOREIGN_KEY_CHECKS = 1");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTablesIfNotExist() {
        Connection conn = DBConnection.getInstance().getConnection();
        try (Statement stmt = conn.createStatement()) {
            // users
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS users (id BIGINT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(100), email VARCHAR(100), username VARCHAR(50) UNIQUE, password VARCHAR(100), role VARCHAR(30))");
            // category
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS category (id BIGINT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(100) UNIQUE NOT NULL, description VARCHAR(500))");
            // brand
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS brand (id BIGINT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(100) UNIQUE NOT NULL)");
            // pres_type
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS pres_type (id BIGINT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(100) NOT NULL, level INT DEFAULT 0)");
            // drug
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS drug (barcode VARCHAR(50) PRIMARY KEY, name VARCHAR(150) NOT NULL, cost_price DECIMAL(10,2) NOT NULL, selling_price DECIMAL(10,2) NOT NULL, stock_quantity INT NOT NULL, category_id BIGINT, brand_id BIGINT, pres_id BIGINT, FOREIGN KEY (category_id) REFERENCES category(id), FOREIGN KEY (brand_id) REFERENCES brand(id), FOREIGN KEY (pres_id) REFERENCES pres_type(id))");
            // expiry
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS expiry (id BIGINT AUTO_INCREMENT PRIMARY KEY, drug_barcode VARCHAR(50), expiration_date DATE, FOREIGN KEY (drug_barcode) REFERENCES drug(barcode))");
            // purchase
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS purchase (id BIGINT AUTO_INCREMENT PRIMARY KEY, drug_barcode VARCHAR(50), quantity_added INT, purchase_date DATE, FOREIGN KEY (drug_barcode) REFERENCES drug(barcode))");
            // sale
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS sale (id BIGINT AUTO_INCREMENT PRIMARY KEY, total_amount DECIMAL(10,2), sale_date DATE)");
            // sale_item
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS sale_item (id BIGINT AUTO_INCREMENT PRIMARY KEY, sale_id BIGINT, drug_barcode VARCHAR(50), quantity INT, unit_price DECIMAL(10,2), FOREIGN KEY (sale_id) REFERENCES sale(id), FOREIGN KEY (drug_barcode) REFERENCES drug(barcode))");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void seedUsers() {
        if (!isTableEmpty("users")) return;
        UserDAO dao = new UserDAO();
        dao.save(new User(null, "Administrator", "admin@pharmacy.com", "admin", "admin123", "ADMIN"));
        System.out.println("[Seeder] Admin user created.");
    }

    private void seedCategories() {
        if (!isTableEmpty("category")) return;
        CategoryDAO dao = new CategoryDAO();
        dao.save(new Category("Pain relief", ""));                   // 1
        dao.save(new Category("Antibiotics", ""));                   // 2
        dao.save(new Category("Cardiology", ""));                    // 3
        dao.save(new Category("Gastroenterology", ""));              // 4
        dao.save(new Category("Cold & Flu", ""));                    // 5
        dao.save(new Category("Diabetes management", ""));           // 6
        dao.save(new Category("Psychiatry & Neurology", ""));        // 7
        dao.save(new Category("Pulmonology", ""));                   // 8
        dao.save(new Category("Supplements", ""));                   // 9
        dao.save(new Category("Blood Products", "Blood factors"));   // 10
        dao.save(new Category("Anesthesia & Surgery", ""));          // 11
    }

    private void seedBrands() {
        if (!isTableEmpty("brand")) return;
        BrandDAO dao = new BrandDAO();
        String[] bList = {"Menarini", "GSK", "Abdi İbrahim", "Bayer", "AstraZeneca", "Bilim İlaç", "Viatris (Pfizer)", "Adeka", "Roche", "Deva", "Exeltis", "Janssen", "Novartis", "Recordati", "Santa Farma", "Sanofi", "Gerot Lannach", "Mundipharma", "İbrahim Hayri", "Galen", "Teva", "Hameln", "Polifarma", "Takeda", "Pfizer", "Novo Nordisk", "CSL Behring", "Octapharma", "Kedrion"};
        for(String b : bList) dao.save(new Brand(0, b));
    }

    private void seedPresTypes() {
        if (!isTableEmpty("pres_type")) return;
        PresTypeDAO dao = new PresTypeDAO();
        // Fixed exactly 5 constraint types
        dao.save(new PresType(0, "White Prescription", 1));  // 1
        dao.save(new PresType(0, "Green Prescription", 2));  // 2
        dao.save(new PresType(0, "Red Prescription", 3));    // 3
        dao.save(new PresType(0, "Orange Prescription", 4)); // 4
        dao.save(new PresType(0, "Purple Prescription", 4)); // 5
    }

    private void seedDrugs() {
        if (!isTableEmpty("drug")) return;
        DrugDAO dao = new DrugDAO();
        // WHITE
        dao.save(drug("86901001", "Arveles", "1", "1", 1));
        dao.save(drug("86901002", "Augmentin", "2", "2", 1));
        dao.save(drug("86901003", "Apranax", "1", "3", 1));
        dao.save(drug("86901004", "Coraspin", "3", "4", 1));
        dao.save(drug("86901005", "Nexium", "4", "5", 1));
        dao.save(drug("86901006", "A-Ferin", "5", "6", 1));
        dao.save(drug("86901007", "Glifor", "6", "6", 1));
        dao.save(drug("86901008", "Lustral", "7", "7", 1));
        dao.save(drug("86901009", "Ventolin", "8", "2", 1));
        dao.save(drug("86901010", "Ferro Sanol", "9", "8", 1));
        
        // GREEN
        dao.save(drug("86902001", "Xanax", "7", "7", 2));
        dao.save(drug("86902002", "Rivotril", "7", "9", 2));
        dao.save(drug("86902003", "Diazem", "7", "10", 2));
        dao.save(drug("86902004", "Lyrica", "7", "7", 2));
        dao.save(drug("86902005", "Ativan", "7", "11", 2));
        dao.save(drug("86902006", "Concerta", "7", "12", 2));
        dao.save(drug("86902007", "Ritalin", "7", "13", 2));
        dao.save(drug("86902008", "Akineton", "7", "14", 2));
        dao.save(drug("86902009", "Contramal", "1", "15", 2));
        dao.save(drug("86902010", "Stilnox", "7", "16", 2));

        // RED
        dao.save(drug("86903001", "Aldolan", "11", "17", 3));
        dao.save(drug("86903002", "Durogesic", "1", "12", 3));
        dao.save(drug("86903003", "M-Eser", "1", "18", 3));
        dao.save(drug("86903004", "Jurnista", "1", "12", 3));
        dao.save(drug("86903005", "Pental", "11", "19", 3));
        dao.save(drug("86903006", "Morphine", "1", "20", 3));
        dao.save(drug("86903007", "OxyContin", "1", "18", 3));
        dao.save(drug("86903008", "Actiq", "1", "21", 3));
        dao.save(drug("86903009", "Fentanyl", "11", "22", 3));
        dao.save(drug("86903010", "Pethidine", "11", "23", 3));

        // ORANGE
        dao.save(drug("86904001", "Advate", "10", "24", 4));
        dao.save(drug("86904002", "Benefix", "10", "25", 4));
        dao.save(drug("86904003", "Feiba", "10", "24", 4));
        dao.save(drug("86904004", "NovoSeven", "10", "26", 4));
        dao.save(drug("86904005", "Haemate P", "10", "27", 4));

        // PURPLE
        dao.save(drug("86905001", "Human Albumin", "10", "28", 5));
        dao.save(drug("86905002", "Privigen", "10", "27", 5));
        dao.save(drug("86905003", "Octagam", "10", "28", 5));
        dao.save(drug("86905004", "Beriglobin", "10", "27", 5));
        dao.save(drug("86905005", "Anti-D", "10", "29", 5));
    }

    private void seedExpiry() {
        if (!isTableEmpty("expiry")) return;
        ExpiryDAO dao = new ExpiryDAO();
        LocalDate today = LocalDate.now();
        // 5 random urgent expirations
        dao.save(new Expiry(new Drug("86901001", null, null, null, 0), today.plusDays(4), 0, "OK"));
        dao.save(new Expiry(new Drug("86902005", null, null, null, 0), today.plusDays(10), 0, "OK"));
        dao.save(new Expiry(new Drug("86903002", null, null, null, 0), today.plusDays(20), 0, "OK"));
        dao.save(new Expiry(new Drug("86904001", null, null, null, 0), today.plusDays(28), 0, "OK"));
        
        // standard expiry
        dao.save(new Expiry(new Drug("86901006", null, null, null, 0), today.plusDays(200), 0, "OK"));
        dao.save(new Expiry(new Drug("86905001", null, null, null, 0), today.plusDays(300), 0, "OK"));
    }
    private void seedPurchases() {}
    private void seedSales() {}

    private boolean isTableEmpty(String tableName) {
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement p = conn.prepareStatement("SELECT COUNT(*) FROM " + tableName);
             ResultSet rs = p.executeQuery()) {
            if (rs.next()) return rs.getInt(1) == 0;
        } catch (Exception e) {}
        return false;
    }

    private int getCount(String tableName) {
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement p = conn.prepareStatement("SELECT COUNT(*) FROM " + tableName);
             ResultSet rs = p.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {}
        return 0;
    }

    private Drug drug(String barcode, String name, String catId, String brandId, int presId) {
        Category cat = new Category();
        cat.setId(Long.parseLong(catId));
        Brand brand = new Brand(Integer.parseInt(brandId), null);
        PresType pres = new PresType(presId, null, 0);

        // Builder Pattern: Adım adım, okunabilir ilaç nesne inşası
        return new com.pharmacy.entity.DrugBuilder()
                .setBarcode(barcode)
                .setName(name)
                .setCostPrice(new BigDecimal("15.00"))
                .setSellingPrice(new BigDecimal("35.00"))
                .setStockQuantity(50)
                .setCategory(cat)
                .setBrand(brand)
                .setPresType(pres)
                .build();
    }
}

