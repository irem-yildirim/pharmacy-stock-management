package com.pharmacy.service;

import com.pharmacy.dao.CategoryDAO;
import com.pharmacy.entity.Category;

import java.util.List;

public class CategoryService {
    private final CategoryDAO categoryDAO;

    public CategoryService(CategoryDAO categoryDAO) {
        this.categoryDAO = categoryDAO;
    }

    public List<Category> getAllCategories() {
        return categoryDAO.findAll();
    }

    public Category getCategoryById(Long id) {
        return categoryDAO.findById(id);
    }

    public Category saveCategory(Category category) {
        if (category.getId() != null) {
            categoryDAO.update(category);
        } else {
            categoryDAO.save(category);
        }
        return category;
    }

    public void deleteCategory(Long id) {
        categoryDAO.delete(id);
    }
}
