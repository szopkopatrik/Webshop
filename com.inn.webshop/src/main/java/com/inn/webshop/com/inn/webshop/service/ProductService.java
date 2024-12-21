package com.inn.webshop.com.inn.webshop.service;

import com.inn.webshop.com.inn.webshop.data.entity.CategoryEntity;
import com.inn.webshop.com.inn.webshop.data.entity.ProductEntity;
import com.inn.webshop.com.inn.webshop.data.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public ResponseEntity<String> saveProduct(Map<String, String> requestMap) {
        try {
            // Create a new ProductEntity and populate it with data from requestMap
            ProductEntity product = new ProductEntity();
            product.setName(requestMap.get("name"));
            product.setDescription(requestMap.get("description"));
            product.setPrice(Integer.parseInt(requestMap.get("price")));  // Assuming price is provided as a string
            product.setStatus(requestMap.get("status"));

            // Handle the CategoryEntity association (assuming "categoryId" is provided in the request)
            Integer categoryId = Integer.parseInt(requestMap.get("categoryId"));
            CategoryEntity categoryEntity = new CategoryEntity(); // Assuming CategoryEntity has a way to set the ID
            categoryEntity.setId(categoryId);
            product.setCategoryEntity(categoryEntity);

            // Save the product to the database
            productRepository.save(product);

            return ResponseEntity.ok("Product added successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error adding product: " + e.getMessage());
        }
    }
    public ResponseEntity<ProductEntity> getProductById(Integer id) {
        try {
            Optional<ProductEntity> productOptional = productRepository.findById(id);
            if (productOptional.isPresent()) {
                return ResponseEntity.ok(productOptional.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    public ResponseEntity<String> updateProduct(Integer id, Map<String, String> requestMap) {
        try {
            Optional<ProductEntity> productOptional = productRepository.findById(id);
            if (productOptional.isPresent()) {
                ProductEntity product = productOptional.get();
                product.setName(requestMap.get("name"));
                product.setDescription(requestMap.get("description"));
                product.setPrice(Integer.parseInt(requestMap.get("price"))); // Parse the price
                product.setStatus(requestMap.get("status"));

                // Handle category update
                if (requestMap.containsKey("categoryId")) {
                    Integer categoryId = Integer.parseInt(requestMap.get("categoryId"));
                    CategoryEntity categoryEntity = new CategoryEntity();
                    categoryEntity.setId(categoryId);
                    product.setCategoryEntity(categoryEntity);
                }

                // Save the updated product to the database
                productRepository.save(product);
                return ResponseEntity.ok("Product updated successfully.");
            } else {
                return ResponseEntity.status(404).body("Product not found.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating product: " + e.getMessage());
        }
    }

    public List<ProductEntity> getAllProducts() {
        return productRepository.findAll();  // Fetches all products from the database
    }

    public boolean deleteProductById(Integer id) {
        // Check if the product exists
        Optional<ProductEntity> product = productRepository.findById(id);
        if (product.isPresent()) {
            // Delete the product if it exists
            productRepository.deleteById(id);
            return true;
        } else {
            // Return false if the product does not exist
            return false;
        }
    }

    public boolean updateProductStatus(Integer id, String newStatus) {
        // Find the product by ID
        Optional<ProductEntity> product = productRepository.findById(id);
        if (product.isPresent()) {
            // Update the status of the product
            ProductEntity productEntity = product.get();
            productEntity.setStatus(newStatus);
            productRepository.save(productEntity); // Save the updated product
            return true;
        } else {
            // Return false if the product does not exist
            return false;
        }
    }

    public List<ProductEntity> getProductsByCategory(Integer categoryId) {
        // Find products by category using the category ID
        return productRepository.findByCategoryEntityId(categoryId);
    }
}
