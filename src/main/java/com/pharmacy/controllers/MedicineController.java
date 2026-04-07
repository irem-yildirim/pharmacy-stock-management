package com.pharmacy.controllers;

import com.pharmacy.entity.*;
import com.pharmacy.service.DrugService;
import com.pharmacy.service.CategoryService;
import com.pharmacy.dao.BrandDAO;
import com.pharmacy.dao.PresTypeDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller katmanı: UI ile Backend arasındaki iş akışını yönetir.
 * Artık direkt Entity modellerini kullanarak gereksiz dönüşüm yükünden kurtulduk.
 */
public class MedicineController {

    private final DrugService drugService;
    private final CategoryService categoryService;
    private final com.pharmacy.service.SaleService saleService;
    private final com.pharmacy.service.PurchaseService purchaseService;
    
    private final BrandDAO brandDAO;
    private final PresTypeDAO presTypeDAO;

    public MedicineController(DrugService drugService, CategoryService categoryService, com.pharmacy.service.SaleService saleService, com.pharmacy.service.PurchaseService purchaseService, BrandDAO brandDAO, PresTypeDAO presTypeDAO) {
        this.drugService = drugService;
        this.categoryService = categoryService;
        this.saleService = saleService;
        this.purchaseService = purchaseService;
        this.brandDAO = brandDAO;
        this.presTypeDAO = presTypeDAO;
    }

    // =========================================================================
    // BRAND & PRES_TYPE
    // =========================================================================

    public List<Brand> getAllBrands() {
        return brandDAO.findAll();
    }

    public List<PresType> getAllPresTypes() {
        return presTypeDAO.findAll();
    }

    // =========================================================================
    // CATEGORY
    // =========================================================================

    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    public boolean addCategory(Category cat) {
        categoryService.saveCategory(cat);
        return true;
    }

    public boolean addBrand(Brand b) {
        brandDAO.save(b);
        return true;
    }

    public boolean addPresType(PresType p) {
        presTypeDAO.save(p);
        return true;
    }

    // =========================================================================
    // DRUG (İlaç İşlemleri)
    // =========================================================================

    public List<Drug> getAllMedicines() {
        return drugService.getAllDrugs();
    }

    public List<Drug> searchMedicines(String keyword) {
        final String kw = keyword.toLowerCase().trim();
        return drugService.getAllDrugs().stream()
                .filter(d -> d.getName().toLowerCase().contains(kw) || 
                             String.valueOf(d.getBarcode()).contains(kw) ||
                             (d.getBrand() != null && d.getBrand().getBrandName().toLowerCase().contains(kw)) ||
                             (d.getCategory() != null && d.getCategory().getName().toLowerCase().contains(kw))
                       )
                .collect(Collectors.toList());
    }

    public List<Drug> getMedicinesByBrand(long brandId) {
        return getAllMedicines().stream()
                .filter(m -> m.getBrand() != null && m.getBrand().getBrandId() == brandId)
                .collect(Collectors.toList());
    }

    public List<Drug> getMedicinesByCategory(long catId) {
        return getAllMedicines().stream()
                .filter(m -> m.getCategory() != null && m.getCategory().getId() == catId)
                .collect(Collectors.toList());
    }

    public boolean addMedicine(Drug med) {
        drugService.addDrug(med);
        return true;
    }

    public boolean updateMedicine(Drug med) {
        drugService.updateDrug(med);
        return true;
    }

    public boolean deleteMedicine(String barcode) {
        drugService.deleteDrug(barcode);
        return true;
    }

    public boolean sellDrug(String barcode, int quantity) {
        try {
            Drug d = drugService.findByBarcode(barcode);
            if (d == null) return false;
            
            if (d.getStockQuantity() < quantity) {
                return false; 
            }

            SaleItem item = new SaleItem();
            item.setDrug(d);
            item.setQuantity(quantity);
            item.setUnitPrice(d.getSellingPrice());

            List<SaleItem> items = new ArrayList<>();
            items.add(item);
            
            saleService.createSale(items);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean purchaseDrug(String barcode, int quantity) {
        try {
            purchaseService.addPurchase(barcode, quantity);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // =========================================================================
    // FINANCIAL REPORTING
    // =========================================================================

    public static class FinancialTransaction {
        public final String type; 
        public final java.time.LocalDate date;
        public final java.math.BigDecimal amount;
        public final String reference; 
        
        public FinancialTransaction(String type, java.time.LocalDate date, java.math.BigDecimal amount, String reference) {
            this.type = type;
            this.date = date;
            this.amount = amount;
            this.reference = reference;
        }
    }

    public static class FinancialSummary {
        public final java.math.BigDecimal totalSales;
        public final java.math.BigDecimal totalPurchases;
        public final java.math.BigDecimal netProfit;
        
        public final java.math.BigDecimal todayRevenue;
        public final int lowStockCount;
        public final int expiryCount;
        public final int totalInventory;

        public FinancialSummary(java.math.BigDecimal totalSales, java.math.BigDecimal totalPurchases, 
                                 java.math.BigDecimal todayRevenue, int lowStockCount, int expiryCount, int totalInventory) {
            this.totalSales = totalSales != null ? totalSales : java.math.BigDecimal.ZERO;
            this.totalPurchases = totalPurchases != null ? totalPurchases : java.math.BigDecimal.ZERO;
            this.netProfit = this.totalSales.subtract(this.totalPurchases);
            this.todayRevenue = todayRevenue != null ? todayRevenue : java.math.BigDecimal.ZERO;
            this.lowStockCount = lowStockCount;
            this.expiryCount = expiryCount;
            this.totalInventory = totalInventory;
        }
    }

    public FinancialSummary getFinancialSummary() {
        List<Drug> all = getAllMedicines();
        int totalInv = all.size();
        int lowStock = (int) all.stream().filter(m -> m.getStockQuantity() < 10).count();
        
        java.time.LocalDate threshold30 = java.time.LocalDate.now().plusDays(30);
        int expCount = (int) all.stream()
                .filter(m -> {
                    Expiry exp = m.getExpiry();
                    return exp != null && exp.getExpirationDate() != null && !exp.getExpirationDate().isAfter(threshold30);
                })
                .count();

        return new FinancialSummary(
            saleService.calculateTotalSales(), 
            purchaseService.calculateTotalPurchases(),
            saleService.calculateTodaySales(),
            lowStock,
            expCount,
            totalInv
        );
    }

    public List<FinancialTransaction> getFinancialTransactions() {
        List<FinancialTransaction> list = new ArrayList<>();
        
        for (Sale s : saleService.getAllSales()) {
            list.add(new FinancialTransaction("SALE", s.getSaleDate(), s.getTotalAmount(), "Sale ID #" + s.getId()));
        }
        
        for (Purchase p : purchaseService.getAllPurchases()) {
            java.math.BigDecimal amount = java.math.BigDecimal.ZERO;
            if (p.getDrug() != null && p.getDrug().getBarcode() != null) {
                Drug drug = drugService.findByBarcode(p.getDrug().getBarcode());
                if (drug != null && drug.getCostPrice() != null) {
                    amount = drug.getCostPrice().multiply(java.math.BigDecimal.valueOf(p.getQuantityAdded()));
                }
            }
            list.add(new FinancialTransaction("PURCHASE", p.getPurchaseDate(), amount, "Purchase: " + (p.getDrug() != null ? p.getDrug().getBarcode() : "Unknown")));
        }
        
        list.sort((a,b) -> {
            if (a.date == null && b.date == null) return 0;
            if (a.date == null) return 1;
            if (b.date == null) return -1;
            return b.date.compareTo(a.date);
        });
        
        return list;
    }
}
