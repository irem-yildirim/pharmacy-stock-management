package com.pharmacy.controllers;

import com.pharmacy.entity.*;
import com.pharmacy.service.DrugService;
import com.pharmacy.service.CategoryService;
import com.pharmacy.dao.BrandDAO;
import com.pharmacy.dao.PresTypeDAO;

import java.util.List;

/**
 * Stok, kategori, marka ve genel envanter operasyonlarını yönetir.
 * UI'dan gelen ilaç ekleme/güncelleme/silme gibi istekler burada işlenir.
 */
public class InventoryController {
    private final DrugService drugService;
    private final CategoryService categoryService;
    // Marka ve reçete tipi için doğrudan DAO kullanıyoruz — Service yazmaya değmeyecek kadar basit işler
    private final BrandDAO brandDAO;
    private final PresTypeDAO presTypeDAO;

    public InventoryController(DrugService drugService, CategoryService categoryService, 
                               BrandDAO brandDAO, PresTypeDAO presTypeDAO) {
        this.drugService = drugService;
        this.categoryService = categoryService;
        this.brandDAO = brandDAO;
        this.presTypeDAO = presTypeDAO;
    }

    // Dropdown menüler için marka, reçete tipi ve kategori listelerini getiriyoruz
    public List<Brand> getAllBrands() { return brandDAO.findAll(); }
    public List<PresType> getAllPresTypes() { return presTypeDAO.findAll(); }
    public List<Category> getAllCategories() { return categoryService.getAllCategories(); }

    // Yeni marka / kategori / reçete tipi eklemek için kısa metodlar
    public boolean addCategory(Category cat) { categoryService.saveCategory(cat); return true; }
    public boolean addBrand(Brand b) { brandDAO.save(b); return true; }
    public boolean addPresType(PresType p) { presTypeDAO.save(p); return true; }

    // Envanter sayfasının ana listesi — tüm ilaçları getiriyoruz
    public List<Drug> getAllMedicines() { return drugService.getAllDrugs(); }

    // Arama kutusuna yazılan kelimeye göre ilaçları isim, barkod, marka veya kategoriye göre filtreliyoruz
    public List<Drug> searchMedicines(String keyword) {
        final String kw = keyword.toLowerCase().trim();
        List<Drug> results = new java.util.ArrayList<>();
        for (Drug d : drugService.getAllDrugs()) {
            boolean matchesName = d.getName().toLowerCase().contains(kw);
            boolean matchesBarcode = String.valueOf(d.getBarcode()).contains(kw);
            boolean matchesBrand = d.getBrand() != null && d.getBrand().getBrandName().toLowerCase().contains(kw);
            boolean matchesCat = d.getCategory() != null && d.getCategory().getName().toLowerCase().contains(kw);
            
            // Herhangi bir alanda eşleşme varsa listeye ekle
            if (matchesName || matchesBarcode || matchesBrand || matchesCat) {
                results.add(d);
            }
        }
        return results;
    }

    // Belirli bir markaya ait ilaçları filtrelemek için — sol menüden marka seçilince çalışıyor
    public List<Drug> getMedicinesByBrand(long brandId) {
        List<Drug> results = new java.util.ArrayList<>();
        for (Drug m : getAllMedicines()) {
            if (m.getBrand() != null && m.getBrand().getBrandId() == brandId) {
                results.add(m);
            }
        }
        return results;
    }

    // Belirli bir kategoriye ait ilaçları filtrelemek için — kategori seçilince çalışıyor
    public List<Drug> getMedicinesByCategory(long catId) {
        List<Drug> results = new java.util.ArrayList<>();
        for (Drug m : getAllMedicines()) {
            if (m.getCategory() != null && m.getCategory().getId() == catId) {
                results.add(m);
            }
        }
        return results;
    }

    // İlaç ekleme, güncelleme ve silme işlemleri — bunlar doğrudan DrugService'e devrediliyor
    public boolean addMedicine(Drug med) { drugService.addDrug(med); return true; }
    public boolean updateMedicine(Drug med) { drugService.updateDrug(med); return true; }
    public boolean deleteMedicine(String barcode) { drugService.deleteDrug(barcode); return true; }

    // Kategori silmeden önce bu kategoriye bağlı ilaç var mı diye kontrol ediyoruz (veri bütünlüğü!)
    public String deleteCategorySafely(long categoryId) {
        List<Drug> linkedDrugs = getMedicinesByCategory(categoryId);
        if (!linkedDrugs.isEmpty()) return "This category has linked medicines and cannot be deleted!"; 
        categoryService.deleteCategory(categoryId); 
        return "SUCCESS";
    }

    // Marka silmeden önce bu markaya bağlı ilaç var mı diye kontrol ediyoruz — aynı savunmacı mantık
    public String deleteBrandSafely(long brandId) {
        List<Drug> linkedDrugs = getMedicinesByBrand(brandId);
        if (!linkedDrugs.isEmpty()) return "This brand has linked medicines and cannot be deleted!"; 
        brandDAO.delete((int)brandId); 
        return "SUCCESS";
    }
}
