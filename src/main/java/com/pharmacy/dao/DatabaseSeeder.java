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
        System.out.println("Seed kontrolü yapılıyor...");
        try {
            seedUsers();
            seedCategories();
            seedDrugs();
            seedExpiry();
            seedPurchases();
            seedSales();
            System.out.println("Seed kontrolü tamamlandı.");
        } catch (Exception e) {
            System.err.println("Seed hatası: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // =========================================================================
    // USERS
    // =========================================================================
    private void seedUsers() {
        if (!isTableEmpty("users"))
            return;
        System.out.println("→ users tablosu dolduruluyor...");

        UserDAO dao = new UserDAO();

        User admin = new User(null, "Yönetici", "admin@pharmacy.com", "admin", "admin123", "ADMIN");
        dao.save(admin);

        User pharmacist = new User(null, "Ecz. Ayşe Demir", "ayse@pharmacy.com", "ayse", "ayse123", "PHARMACIST");
        dao.save(pharmacist);

        User cashier = new User(null, "Mehmet Kaya", "mehmet@pharmacy.com", "mehmet", "mehmet123", "CASHIER");
        dao.save(cashier);
    }

    // =========================================================================
    // CATEGORIES
    // =========================================================================
    private void seedCategories() {
        if (!isTableEmpty("category"))
            return;
        System.out.println("→ category tablosu dolduruluyor...");

        CategoryDAO dao = new CategoryDAO();

        dao.save(buildCategory("Ağrı Kesici", "Ağrı ve ateş düşürücü ilaçlar"));
        dao.save(buildCategory("Antibiyotik", "Bakteri enfeksiyonlarına karşı ilaçlar"));
        dao.save(buildCategory("Vitamin", "Vitamin ve mineral takviyeleri"));
        dao.save(buildCategory("Sindirim Sistemi", "Mide, bağırsak ve sindirime yönelik ilaçlar"));
        dao.save(buildCategory("Cilt Bakım", "Dermatolojik kremler ve losyonlar"));
        dao.save(buildCategory("Soğuk Algınlığı", "Grip, öksürük ve nazal ilaçlar"));
    }

    // =========================================================================
    // DRUGS
    // =========================================================================
    private void seedDrugs() {
        if (!isTableEmpty("drug"))
            return;
        System.out.println("→ drug tablosu dolduruluyor...");

        DrugDAO dao = new DrugDAO();
        // category_id'leri seed sırasına göre: 1=Ağrı, 2=Antibiyotik, 3=Vitamin,
        // 4=Sindirim, 5=Cilt, 6=Soğuk Algınlığı

        dao.save(buildDrug("1001", "Parol 500mg", "500mg", new BigDecimal("8.50"), new BigDecimal("14.75"), 120, 1L));
        dao.save(buildDrug("1002", "Aspirin 100mg", "100mg", new BigDecimal("5.00"), new BigDecimal("9.90"), 85, 1L));
        dao.save(buildDrug("1003", "Nurofen 400mg", "400mg", new BigDecimal("12.00"), new BigDecimal("22.50"), 60, 1L));
        dao.save(buildDrug("1004", "Augmentin 1000mg", "1000mg", new BigDecimal("35.00"), new BigDecimal("58.90"), 30,
                2L));
        dao.save(buildDrug("1005", "Amoksil 500mg", "500mg", new BigDecimal("18.00"), new BigDecimal("32.50"), 45, 2L));
        dao.save(buildDrug("1006", "Cipro 500mg", "500mg", new BigDecimal("22.00"), new BigDecimal("39.90"), 8, 2L));
        dao.save(buildDrug("1007", "C Vitamini 1000mg", "1000mg", new BigDecimal("10.00"), new BigDecimal("18.50"), 200,
                3L));
        dao.save(buildDrug("1008", "D Vitamini 1000IU", "1000IU", new BigDecimal("15.00"), new BigDecimal("26.90"), 150,
                3L));
        dao.save(buildDrug("1009", "B12 Vitamini", "1mg", new BigDecimal("20.00"), new BigDecimal("35.00"), 5, 3L));
        dao.save(buildDrug("1010", "Nexium 40mg", "40mg", new BigDecimal("30.00"), new BigDecimal("52.90"), 40, 4L));
        dao.save(buildDrug("1011", "Rennie Tablet", "680mg", new BigDecimal("8.00"), new BigDecimal("15.50"), 90, 4L));
        dao.save(buildDrug("1012", "Advantan Krem", "1mg/g", new BigDecimal("25.00"), new BigDecimal("45.00"), 35, 5L));
        dao.save(buildDrug("1013", "Tylol Hot", "500mg", new BigDecimal("6.00"), new BigDecimal("12.90"), 70, 6L));
        dao.save(buildDrug("1014", "Sudafed 60mg", "60mg", new BigDecimal("10.00"), new BigDecimal("19.90"), 3, 6L));
        dao.save(buildDrug("1015", "Strepsils Pastil", "1.2mg", new BigDecimal("7.00"), new BigDecimal("13.50"), 110,
                6L));
    }

    // =========================================================================
    // EXPIRY
    // =========================================================================
    private void seedExpiry() {
        if (!isTableEmpty("expiry"))
            return;
        System.out.println("→ expiry tablosu dolduruluyor...");

        ExpiryDAO dao = new ExpiryDAO();
        LocalDate today = LocalDate.now();

        // 3 critical (≤30 gün), 2 expired, geri kalan OK
        addExpiry(dao, "1001", today.plusDays(365)); // OK — 1 yıl
        addExpiry(dao, "1002", today.plusDays(15)); // CRITICAL — 15 gün
        addExpiry(dao, "1003", today.plusDays(180)); // OK
        addExpiry(dao, "1004", today.plusDays(22)); // CRITICAL — 22 gün
        addExpiry(dao, "1005", today.plusDays(90)); // OK
        addExpiry(dao, "1006", today.minusDays(10)); // EXPIRED — 10 gün önce
        addExpiry(dao, "1007", today.plusDays(540)); // OK
        addExpiry(dao, "1008", today.plusDays(420)); // OK
        addExpiry(dao, "1009", today.plusDays(7)); // CRITICAL — 7 gün
        addExpiry(dao, "1010", today.plusDays(270)); // OK
        addExpiry(dao, "1011", today.plusDays(60)); // OK
        addExpiry(dao, "1012", today.minusDays(5)); // EXPIRED — 5 gün önce
        addExpiry(dao, "1013", today.plusDays(300)); // OK
        addExpiry(dao, "1014", today.plusDays(200)); // OK
        addExpiry(dao, "1015", today.plusDays(150)); // OK
    }

    // =========================================================================
    // PURCHASES (stok giriş geçmişi)
    // =========================================================================
    private void seedPurchases() {
        if (!isTableEmpty("purchase"))
            return;
        System.out.println("→ purchase tablosu dolduruluyor...");

        PurchaseDAO dao = new PurchaseDAO();
        LocalDate today = LocalDate.now();

        addPurchase(dao, "1001", 60, today.minusDays(30));
        addPurchase(dao, "1001", 60, today.minusDays(5));
        addPurchase(dao, "1004", 30, today.minusDays(15));
        addPurchase(dao, "1007", 100, today.minusDays(20));
        addPurchase(dao, "1007", 100, today.minusDays(2));
        addPurchase(dao, "1013", 70, today.minusDays(10));
        addPurchase(dao, "1010", 40, today.minusDays(25));
        addPurchase(dao, "1015", 110, today.minusDays(8));
    }

    // =========================================================================
    // SALES + SALE_ITEMS (satış geçmişi)
    // =========================================================================
    private void seedSales() {
        if (!isTableEmpty("sale"))
            return;
        System.out.println("→ sale / sale_item tabloları dolduruluyor...");

        SaleDAO saleDAO = new SaleDAO();
        SaleItemDAO itemDAO = new SaleItemDAO();
        LocalDate today = LocalDate.now();

        // Satış 1
        Sale s1 = new Sale(new BigDecimal("44.65"), today.minusDays(3));
        saleDAO.save(s1);
        itemDAO.save(buildSaleItem(s1, "1001", 2, new BigDecimal("14.75")));
        itemDAO.save(buildSaleItem(s1, "1013", 1, new BigDecimal("12.90")));

        // Satış 2
        Sale s2 = new Sale(new BigDecimal("91.40"), today.minusDays(2));
        saleDAO.save(s2);
        itemDAO.save(buildSaleItem(s2, "1004", 1, new BigDecimal("58.90")));
        itemDAO.save(buildSaleItem(s2, "1005", 1, new BigDecimal("32.50")));

        // Satış 3
        Sale s3 = new Sale(new BigDecimal("53.80"), today.minusDays(1));
        saleDAO.save(s3);
        itemDAO.save(buildSaleItem(s3, "1007", 2, new BigDecimal("18.50")));
        itemDAO.save(buildSaleItem(s3, "1011", 1, new BigDecimal("15.50")));

        // Satış 4 (bugün)
        Sale s4 = new Sale(new BigDecimal("62.40"), today);
        saleDAO.save(s4);
        itemDAO.save(buildSaleItem(s4, "1008", 1, new BigDecimal("26.90")));
        itemDAO.save(buildSaleItem(s4, "1009", 1, new BigDecimal("35.00")));
    }

    // =========================================================================
    // YARDIMCI METODLAR
    // =========================================================================

    private boolean isTableEmpty(String tableName) {
        String query = "SELECT COUNT(*) FROM " + tableName;
        try (Connection conn = DBConnection.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        } catch (SQLException e) {
            System.err.println("Tablo kontrol hatası (" + tableName + "): " + e.getMessage());
        }
        return false;
    }

    private Category buildCategory(String name, String desc) {
        Category c = new Category(name, desc);
        return c;
    }

    private Drug buildDrug(String barcode, String name, String dose,
            BigDecimal cost, BigDecimal sell, int qty, Long catId) {
        Drug d = new DrugBuilder()
                .barcode(barcode).name(name).dose(dose)
                .costPrice(cost).sellingPrice(sell).stockQuantity(qty)
                .build();
        if (catId != null) {
            Category cat = new Category();
            cat.setId(catId);
            d.setCategory(cat);
        }
        return d;
    }

    private void addExpiry(ExpiryDAO dao, String barcode, LocalDate expDate) {
        Drug d = new Drug();
        d.setBarcode(barcode);

        long daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), expDate);
        String status;
        if (daysRemaining <= 0)
            status = "EXPIRED";
        else if (daysRemaining <= 30)
            status = "CRITICAL";
        else
            status = "OK";

        Expiry e = new Expiry(d, expDate, daysRemaining, status);
        dao.save(e);
    }

    private void addPurchase(PurchaseDAO dao, String barcode, int qty, LocalDate date) {
        Drug d = new Drug();
        d.setBarcode(barcode);
        Purchase p = new Purchase(d, qty, date);
        dao.save(p);
    }

    private SaleItem buildSaleItem(Sale sale, String barcode, int qty, BigDecimal unitPrice) {
        Drug d = new Drug();
        d.setBarcode(barcode);
        return new SaleItem(sale, d, qty, unitPrice);
    }
}
