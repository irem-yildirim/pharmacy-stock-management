package com.pharmacy.controllers;

import com.pharmacy.entity.*;
import com.pharmacy.service.DrugService;
import com.pharmacy.service.CategoryService;
import com.pharmacy.dao.BrandDAO;
import com.pharmacy.dao.PresTypeDAO;

import java.util.List;

/**
 * Stok, kategori, marka ve genel envanter operasyonlarını yönetir.
 * (Single Responsibility Principle)
 */
public class InventoryController {
    private final DrugService drugService;
    private final CategoryService categoryService;
    private final BrandDAO brandDAO;
    private final PresTypeDAO presTypeDAO;

    public InventoryController(DrugService drugService, CategoryService categoryService, 
                               BrandDAO brandDAO, PresTypeDAO presTypeDAO) {
        this.drugService = drugService;
        this.categoryService = categoryService;
        this.brandDAO = brandDAO;
        this.presTypeDAO = presTypeDAO;
    }

    public List<Brand> getAllBrands() { return brandDAO.findAll(); }
    public List<PresType> getAllPresTypes() { return presTypeDAO.findAll(); }
    public List<Category> getAllCategories() { return categoryService.getAllCategories(); }

    public boolean addCategory(Category cat) { categoryService.saveCategory(cat); return true; }
    public boolean addBrand(Brand b) { brandDAO.save(b); return true; }
    public boolean addPresType(PresType p) { presTypeDAO.save(p); return true; }

    public List<Drug> getAllMedicines() { return drugService.getAllDrugs(); }

    public List<Drug> searchMedicines(String keyword) {
        final String kw = keyword.toLowerCase().trim();
        List<Drug> results = new java.util.ArrayList<>();
        for (Drug d : drugService.getAllDrugs()) {
            boolean matchesName = d.getName().toLowerCase().contains(kw);
            boolean matchesBarcode = String.valueOf(d.getBarcode()).contains(kw);
            boolean matchesBrand = d.getBrand() != null && d.getBrand().getBrandName().toLowerCase().contains(kw);
            boolean matchesCat = d.getCategory() != null && d.getCategory().getName().toLowerCase().contains(kw);
            
            if (matchesName || matchesBarcode || matchesBrand || matchesCat) {
                results.add(d);
            }
        }
        return results;
    }

    public List<Drug> getMedicinesByBrand(long brandId) {
        List<Drug> results = new java.util.ArrayList<>();
        for (Drug m : getAllMedicines()) {
            if (m.getBrand() != null && m.getBrand().getBrandId() == brandId) {
                results.add(m);
            }
        }
        return results;
    }

    public List<Drug> getMedicinesByCategory(long catId) {
        List<Drug> results = new java.util.ArrayList<>();
        for (Drug m : getAllMedicines()) {
            if (m.getCategory() != null && m.getCategory().getId() == catId) {
                results.add(m);
            }
        }
        return results;
    }

    public boolean addMedicine(Drug med) { drugService.addDrug(med); return true; }
    public boolean updateMedicine(Drug med) { drugService.updateDrug(med); return true; }
    public boolean deleteMedicine(String barcode) { drugService.deleteDrug(barcode); return true; }

    public String deleteCategorySafely(long categoryId) {
        List<Drug> linkedDrugs = getMedicinesByCategory(categoryId);
        if (!linkedDrugs.isEmpty()) return "This category has linked medicines and cannot be deleted!"; 
        categoryService.deleteCategory(categoryId); 
        return "SUCCESS";
    }

    public String deleteBrandSafely(long brandId) {
        List<Drug> linkedDrugs = getMedicinesByBrand(brandId);
        if (!linkedDrugs.isEmpty()) return "This brand has linked medicines and cannot be deleted!"; 
        brandDAO.delete((int)brandId); 
        return "SUCCESS";
    }
}
