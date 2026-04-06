package com.pharmacy.controllers;

import com.pharmacy.entity.Drug;
import com.pharmacy.entity.Category;
import com.pharmacy.entity.Expiry;
import com.pharmacy.service.DrugService;
import com.pharmacy.service.CategoryService;
import com.pharmacy.dao.ExpiryDAO;
import com.pharmacy.dao.BrandDAO;
import com.pharmacy.dao.PresTypeDAO;
import com.pharmacy.models.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Adapter (Köprü) Pattern: UI'ın beklediği "Medicine" nesnesi ile
 * saf Backend'deki "Drug" POJO'su arasındaki veri dönüşümünü (Mapping) yapar.
 */
public class MedicineController {

    private final DrugService drugService;
    private final CategoryService categoryService;
    private final ExpiryDAO expiryDAO;
    private final com.pharmacy.service.SaleService saleService;
    private final com.pharmacy.service.PurchaseService purchaseService;
    
    private final BrandDAO brandDAO;
    private final PresTypeDAO presTypeDAO;

    public MedicineController(DrugService drugService, CategoryService categoryService, ExpiryDAO expiryDAO, com.pharmacy.service.SaleService saleService, com.pharmacy.service.PurchaseService purchaseService, BrandDAO brandDAO, PresTypeDAO presTypeDAO) {
        this.drugService = drugService;
        this.categoryService = categoryService;
        this.expiryDAO = expiryDAO;
        this.saleService = saleService;
        this.purchaseService = purchaseService;
        this.brandDAO = brandDAO;
        this.presTypeDAO = presTypeDAO;
    }

    // =========================================================================
    // GERÇEK VERİTABANI: BRAND, PRES_TYPE (DAO üzerinden)
    // =========================================================================

    public List<Brand> getAllBrands() {
        return brandDAO.findAll();
    }

    public List<PresType> getAllPresTypes() {
        return presTypeDAO.findAll();
    }

    // =========================================================================
    // GERÇEK VERİTABANI: KATEGORİ (CategoryService üzerinden)
    // =========================================================================

    public List<MedicineCategory> getAllCategories() {
        return categoryService.getAllCategories().stream()
                .map(cat -> new MedicineCategory(cat.getId().intValue(), cat.getName(), cat.getDescription()))
                .collect(Collectors.toList());
    }

    public boolean addCategory(MedicineCategory cat) {
        Category newCat = new Category();
        newCat.setName(cat.getCatName());
        newCat.setDescription(cat.getDescription());
        categoryService.saveCategory(newCat);
        return true;
    }

    public boolean addBrand(Brand b) {
        brandDAO.save(b);
        return true;
    }

    public boolean addPresType(PresType p) {
        return true;
    }

    // =========================================================================
    // GERÇEK VERİTABANI: İLAÇ (DrugService üzerinden - Adapter Köprüsü)
    // =========================================================================

    public List<Medicine> getAllMedicines() {
        return drugService.getAllDrugs().stream()
                .map(this::convertToMedicine).collect(Collectors.toList());
    }

    public List<Medicine> searchMedicines(String keyword) {
        final String kw = keyword.toLowerCase().trim();
        return drugService.getAllDrugs().stream()
                .filter(d -> d.getName().toLowerCase().contains(kw) || 
                             String.valueOf(d.getBarcode()).contains(kw) ||
                             (d.getBrand() != null && d.getBrand().getBrandName().toLowerCase().contains(kw)) ||
                             (d.getCategory() != null && d.getCategory().getName().toLowerCase().contains(kw))
                       )
                .map(this::convertToMedicine).collect(Collectors.toList());
    }

    public List<Medicine> getMedicinesByBrand(long brandId) {
        return getAllMedicines().stream()
                .filter(m -> m.getBrandId() == brandId)
                .collect(Collectors.toList());
    }

    public List<Medicine> getMedicinesByCategory(int catId) {
        return getAllMedicines().stream()
                .filter(m -> m.getCatId() == catId)
                .collect(Collectors.toList());
    }

    public boolean addMedicine(Medicine med) {
        drugService.addDrug(convertToDrug(med));
        return true;
    }

    public boolean updateMedicine(Medicine med) {
        drugService.updateDrug(convertToDrug(med));
        return true;
    }

    public boolean deleteMedicine(int medId) {
        drugService.deleteDrug(String.valueOf(medId));
        return true;
    }

    // =========================================================================
    // ADAPTER: Medicine <-> Drug Çeviri
    // =========================================================================

    private Medicine convertToMedicine(Drug drug) {
        Medicine uiMed = new Medicine();
        try {
            uiMed.setMedId(Integer.parseInt(drug.getBarcode()));
        } catch (Exception e) {
            uiMed.setMedId(Math.abs(drug.getBarcode().hashCode()));
        }
        uiMed.setMedName(drug.getName());
        uiMed.setDose(drug.getDose());
        uiMed.setCost(drug.getCostPrice().doubleValue());
        uiMed.setPrice(drug.getSellingPrice().doubleValue());
        uiMed.setQuantity(drug.getStockQuantity());

        if (drug.getCategory() != null) {
            uiMed.setCatId(drug.getCategory().getId().intValue());
        }

        try {
            Expiry expiry = expiryDAO.findByDrugBarcode(drug.getBarcode());
            if (expiry != null && expiry.getExpirationDate() != null) {
                uiMed.setExpirationDate(expiry.getExpirationDate());
            }
        } catch (Exception e) {}

        if (drug.getBrand() != null) {
            uiMed.setBrandId(drug.getBrand().getBrandId());
        } else {
            uiMed.setBrandId(0);
        }

        if (drug.getPresType() != null) {
            uiMed.setPresId(drug.getPresType().getPresId());
        } else {
            uiMed.setPresId(0);
        }
        
        return uiMed;
    }

    private Drug convertToDrug(Medicine med) {
        com.pharmacy.pattern.DrugBuilder builder = new com.pharmacy.pattern.DrugBuilder();
        Drug d = builder.barcode(String.valueOf(med.getMedId()))
                .name(med.getMedName())
                .dose(med.getDose())
                .costPrice(java.math.BigDecimal.valueOf(med.getCost()))
                .sellingPrice(java.math.BigDecimal.valueOf(med.getPrice()))
                .stockQuantity(med.getQuantity())
                .build();
                
        if (med.getCatId() > 0) {
            Category c = new Category();
            c.setId((long)med.getCatId());
            d.setCategory(c);
        }
        if (med.getBrandId() > 0) {
            d.setBrand(new Brand((int)med.getBrandId(), null));
        }
        if (med.getPresId() > 0) {
            d.setPresType(new PresType((int)med.getPresId(), null, 0));
        }
        
        return d;
    }

    public boolean sellDrug(String barcode, int quantity) {
        try {
            Drug d = drugService.findByBarcode(barcode);
            if (d == null) return false;
            
            if (d.getStockQuantity() < quantity) {
                return false; // Insufficient stock
            }

            com.pharmacy.entity.SaleItem item = new com.pharmacy.entity.SaleItem();
            item.setDrug(d);
            item.setQuantity(quantity);
            item.setUnitPrice(d.getSellingPrice());

            List<com.pharmacy.entity.SaleItem> items = new ArrayList<>();
            items.add(item);
            
            saleService.createSale(items);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // =========================================================================
    // SATIN ALIM KÖPRÜSÜ
    // =========================================================================

    public boolean purchaseDrug(String barcode, int quantity) {
        try {
            purchaseService.addPurchase(barcode, quantity);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
