package com.inn.webshop.com.inn.webshop.Controller;

import com.inn.webshop.com.inn.webshop.service.DashboardService;
import com.inn.webshop.com.inn.webshop.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path = "/dashboard")
public class DashBoardController {

    @Autowired
    DashboardService dashboardService;

    @Autowired
    JwtService jwtService;

    @GetMapping("/details")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> getCounts(HttpServletRequest request) {
        // Get the Authorization header from the request
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // Extract the JWT token from the Authorization header
            String jwt = authHeader.substring(7);
            try {
                // Extract the role claim from the JWT token
                String role = jwtService.getClaim(jwt, claims -> claims.get("role", String.class));

                // Check if the role is 'admin'
                if ("admin".equals(role)) {
                    // Get the count of products, bills, and categories
                    long productCount = dashboardService.getProductCount();
                    long billCount = dashboardService.getBillCount();
                    long categoryCount = dashboardService.getCategoryCount();

                    // Create a response object containing all counts
                    Map<String, Long> counts = new HashMap<>();
                    counts.put("products", productCount);
                    counts.put("bills", billCount);
                    counts.put("categories", categoryCount);

                    return ResponseEntity.ok(counts);
                } else {
                    // Return a forbidden status if the user is not an admin
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: insufficient permissions.");
                }
            } catch (Exception e) {
                // Handle any exception, such as an invalid token
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid JWT token.");
            }
        } else {
            // Return unauthorized if the Authorization header is missing or invalid
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header.");
        }
    }
}
