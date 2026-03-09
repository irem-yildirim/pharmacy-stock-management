package com.pharmacy.service;

import com.pharmacy.dao.CategoryDAO;
import com.pharmacy.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryDAO categoryDAO;

    @Autowired
    public CategoryService(CategoryDAO categoryDAO) {
        this.categoryDAO = categoryDAO;
    }

    public List<Category> getAllCategories() {
        return categoryDAO.findAll();
    }

    public Category getCategoryById(Long id) {
        return categoryDAO.findById(id).orElse(null);
    }

    public Category saveCategory(Category category) {
        return categoryDAO.save(category);
    }

    public void deleteCategory(Long id) {
        categoryDAO.deleteById(id);
    }
}
