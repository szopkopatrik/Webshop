package com.inn.webshop.com.inn.webshop.Controller;


import com.inn.webshop.com.inn.webshop.data.entity.CategoryEntity;
import com.inn.webshop.com.inn.webshop.data.entity.ProductEntity;
import com.inn.webshop.com.inn.webshop.service.JwtService;
import com.inn.webshop.com.inn.webshop.service.ProductService;
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
@RequestMapping(path = "/webshop/product")
public class ProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private JwtService jwtService;

    // POST method to add a new product
    @PostMapping("/add")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<String> addProduct(@RequestBody Map<String, String> requestMap, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        // Check if Authorization header is provided with Bearer token
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);

            try {
                // Validate JWT and extract role
                String role = jwtService.getClaim(jwt, claims -> claims.get("role", String.class));

                // Only allow users with the "admin" role to add products
                if ("admin".equals(role)) {
                    // Call service to save the product
                    return productService.saveProduct(requestMap);
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
    public ResponseEntity<?> getAllProducts(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);
            try {
                // Extract role claim
                String role = jwtService.getClaim(jwt, claims -> claims.get("role", String.class));
                if ("admin".equals(role)) {
                    // Fetch all products using service
                    List<ProductEntity> products = productService.getAllProducts();
                    return ResponseEntity.ok(products);
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

    @GetMapping("/get/{id}")
    @PreAuthorize("hasRole('admin') or hasRole('user')")
    public ResponseEntity<ProductEntity> getProduct(@PathVariable Integer id, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        // Check if Authorization header is provided with Bearer token
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);

            try {
                // Extract claims from the JWT token
                Claims claims = jwtService.extractAllClaims(jwt);
                String role = claims.get("role", String.class);
                System.out.println("Role from token: " + role);

                // Only allow users with the "admin" role or "user" to view products
                if ("admin".equals(role) || "user".equals(role)) {
                    // Fetch the product by ID from the service
                    return productService.getProductById(id);
                } else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
                }
            } catch (Exception e) {
                System.out.println("Error processing JWT: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    // PUT method to update an existing product
    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<String> updateProduct(@PathVariable Integer id, @RequestBody Map<String, String> requestMap, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        // Check if Authorization header is provided with Bearer token
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);

            try {
                // Validate JWT and extract role
                String role = jwtService.getClaim(jwt, claims -> claims.get("role", String.class));

                // Only allow users with the "admin" role to update products
                if ("admin".equals(role)) {
                    // Call service to update the product
                    return productService.updateProduct(id, requestMap);
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

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<String> deleteProduct(@PathVariable("id") Integer id, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);
            try {
                // Extract role claim
                String role = jwtService.getClaim(jwt, claims -> claims.get("role", String.class));
                if ("admin".equals(role)) {
                    // Call service to delete the product
                    boolean isDeleted = productService.deleteProductById(id);
                    if (isDeleted) {
                        return ResponseEntity.ok("Product deleted successfully.");
                    } else {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found.");
                    }
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

    @PutMapping("/update-status/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<String> updateProductStatus(@PathVariable("id") Integer id, @RequestBody Map<String, String> requestMap, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);
            try {
                // Extract role claim
                String role = jwtService.getClaim(jwt, claims -> claims.get("role", String.class));
                if ("admin".equals(role)) {
                    // Extract the new status from the request map
                    String newStatus = requestMap.get("status");

                    if (newStatus != null) {
                        // Call service to update the status
                        boolean isUpdated = productService.updateProductStatus(id, newStatus);
                        if (isUpdated) {
                            return ResponseEntity.ok("Product status updated successfully.");
                        } else {
                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found.");
                        }
                    } else {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Status is required.");
                    }
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

    @GetMapping("/get-by-category/{categoryId}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> getProductsByCategory(@PathVariable("categoryId") Integer categoryId, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);
            try {
                // Extract role claim
                String role = jwtService.getClaim(jwt, claims -> claims.get("role", String.class));
                if ("admin".equals(role)) {
                    // Fetch products by category using the service
                    List<ProductEntity> products = productService.getProductsByCategory(categoryId);
                    if (products.isEmpty()) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No products found for this category.");
                    }
                    return ResponseEntity.ok(products);
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

