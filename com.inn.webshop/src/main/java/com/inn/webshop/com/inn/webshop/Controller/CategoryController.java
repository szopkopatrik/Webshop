package com.inn.webshop.com.inn.webshop.Controller;


import com.inn.webshop.com.inn.webshop.data.entity.CategoryEntity;
import com.inn.webshop.com.inn.webshop.service.CategoryService;
import com.inn.webshop.com.inn.webshop.service.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/webshop/category")
public class CategoryController {

    @Autowired
    private JwtService jwtService;

    @Autowired
    CategoryService categoryService;

    @PostMapping("/add")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<String> addNewCategory(@RequestBody Map<String, String> requestMap, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);
            try {
                Claims claims = jwtService.extractAllClaims(jwt);
                String role = claims.get("role", String.class);
                System.out.println("Request Map: " + requestMap);

                if ("admin".equals(role)) {
                    return categoryService.saveCategory(requestMap);
                } else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: insufficient permissions.");
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid JWT token.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header.");
        }
    }

    @GetMapping("/get")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> getAllCategories(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);
            try {
                // Extract role claim
                String role = jwtService.getClaim(jwt, claims -> claims.get("role", String.class));
                if ("admin".equals(role)) {
                    // Fetch categories using service
                    List<CategoryEntity> categories = categoryService.getAllCategories();
                    return ResponseEntity.ok(categories);
                } else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: insufficient permissions.");
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid JWT token.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header.");
        }
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<String> updateCategory(@RequestBody Map<String, String> requestMap, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);
            try {
                // Extract role claim
                String role = jwtService.getClaim(jwt, claims -> claims.get("role", String.class));
                if ("admin".equals(role)) {
                    // Call service to update category
                    return categoryService.updateCategory(requestMap);
                } else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: insufficient permissions.");
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid JWT token.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header.");
        }
    }

}

