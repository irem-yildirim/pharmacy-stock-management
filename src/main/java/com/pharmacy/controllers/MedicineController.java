package com.pharmacy.controllers;

import com.pharmacy.entity.Drug;
import com.pharmacy.entity.Category;
import com.pharmacy.entity.Expiry;
import com.pharmacy.service.DrugService;
import com.pharmacy.service.CategoryService;
import com.pharmacy.dao.ExpiryDAO;
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

    public MedicineController(DrugService drugService, CategoryService categoryService, ExpiryDAO expiryDAO) {
        this.drugService = drugService;
        this.categoryService = categoryService;
        this.expiryDAO = expiryDAO;
    }

    // =========================================================================
    // DUMMY MODELLER (DB'de olmayan Brand/Supplier/PresType için)
    // =========================================================================

    public List<Brand> getAllBrands() {
        List<Brand> dummyBrands = new ArrayList<>();
        dummyBrands.add(new Brand(1, "Bilinmiyor"));
        return dummyBrands;
    }

    public List<Supplier> getAllSuppliers() {
        List<Supplier> dummySuppliers = new ArrayList<>();
        dummySuppliers.add(new Supplier(1, "Bilinmiyor", "000-0000"));
        return dummySuppliers;
    }

    public List<PresType> getAllPresTypes() {
        List<PresType> dummyPresTypes = new ArrayList<>();
        dummyPresTypes.add(new PresType(1, "Genel Reçetesiz", 0));
        return dummyPresTypes;
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
        return true;
    }

    public boolean addSupplier(Supplier s) {
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
        return drugService.getAllDrugs().stream()
                .filter(d -> d.getName().toLowerCase().contains(keyword.toLowerCase()))
                .map(this::convertToMedicine).collect(Collectors.toList());
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

        if (drug.getCategory() != null)
            uiMed.setCatId(drug.getCategory().getId().intValue());

        // Expiry: look up from expiry table by drug barcode
        try {
            Expiry expiry = expiryDAO.findByDrugBarcode(drug.getBarcode());
            if (expiry != null && expiry.getExpirationDate() != null) {
                uiMed.setExpirationDate(expiry.getExpirationDate());
            }
        } catch (Exception e) {
            // expiry lookup failed — leave null
        }

        uiMed.setBrandId(1);
        uiMed.setSupplierId(1);
        uiMed.setPresId(1);
        return uiMed;
    }

    private Drug convertToDrug(Medicine med) {
        com.pharmacy.pattern.DrugBuilder builder = new com.pharmacy.pattern.DrugBuilder();
        return builder.barcode(String.valueOf(med.getMedId()))
                .name(med.getMedName())
                .dose(med.getDose())
                .costPrice(java.math.BigDecimal.valueOf(med.getCost()))
                .sellingPrice(java.math.BigDecimal.valueOf(med.getPrice()))
                .stockQuantity(med.getQuantity())
                .build();
    }
}
