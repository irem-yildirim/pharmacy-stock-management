package com.pharmacy.service;

import com.pharmacy.dao.CategoryDAO;
import com.pharmacy.entity.Category;

import java.util.List;

// Kategori (İlaç grubu) işlemlerini yöneten servis sınıfı
public class CategoryService {
    private final CategoryDAO categoryDAO;

    public CategoryService(CategoryDAO categoryDAO) {
        this.categoryDAO = categoryDAO;
    }

    // Tüm kategorileri getir — dropdown listeleri ve sidebar için kullanılıyor
    public List<Category> getAllCategories() {
        return categoryDAO.findAll();
    }

    // ID varsa güncelle, yoksa yeni kayıt ekle — tek metot ile ikisini de hallettik
    public Category saveCategory(Category category) {
        if (category.getId() != null) {
            categoryDAO.update(category);
        } else {
            categoryDAO.save(category);
        }
        return category;
    }

    // Kategori silme — InventoryController önce güvenlik kontrolü yapıyor, bu direkt siliyor
    public void deleteCategory(Long id) {
        categoryDAO.delete(id);
    }
}
