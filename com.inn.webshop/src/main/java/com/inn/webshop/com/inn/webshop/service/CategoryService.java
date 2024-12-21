package com.inn.webshop.com.inn.webshop.service;

import com.inn.webshop.com.inn.webshop.data.entity.CategoryEntity;

import com.inn.webshop.com.inn.webshop.data.repository.CategoryRepository;

import com.inn.webshop.com.inn.webshop.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public ResponseEntity<String> saveCategory(Map<String, String> requestMap) {
        try {
            // Validate input data for category
            if (validateCategoryMap(requestMap)) {
                // Extract category name from the request map
                String categoryName = requestMap.get("name");

                // Check if the category already exists
                CategoryEntity existingCategory = categoryRepository.findByName(categoryName);
                if (Objects.nonNull(existingCategory)) {
                    return Utils.getResponseEntity("Category already exists.", HttpStatus.BAD_REQUEST);
                }

                CategoryEntity categoryEntity = new CategoryEntity();
                categoryEntity.setName(categoryName);

                categoryRepository.save(categoryEntity);

                return Utils.getResponseEntity("Category added successfully.", HttpStatus.CREATED);
            } else {
                return Utils.getResponseEntity("Invalid data.", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return Utils.getResponseEntity("Something went wrong.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean validateCategoryMap(Map<String, String> requestMap) {
        return requestMap.containsKey("name") && !requestMap.get("name").isEmpty();
    }

    public List<CategoryEntity> getAllCategories() {
        return categoryRepository.findAll();
    }

    public ResponseEntity<String> updateCategory(Map<String, String> requestMap) {
        try {
            int categoryId = Integer.parseInt(requestMap.get("id")); // Assuming "id" is provided in the request map
            CategoryEntity category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));

            // Update category details
            if (requestMap.containsKey("name")) {
                category.setName(requestMap.get("name"));
            }

            // Save the updated category
            categoryRepository.save(category);
            return ResponseEntity.ok("Category updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to update category: " + e.getMessage());
        }
    }
}

