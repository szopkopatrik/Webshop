package com.inn.webshop.com.inn.webshop.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inn.webshop.com.inn.webshop.data.entity.BillEntity;
import com.inn.webshop.com.inn.webshop.service.BillService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.inn.webshop.com.inn.webshop.service.JwtService;
import com.itextpdf.text.DocumentException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/webshop/bill")
public class BillController {

    @Autowired
    private BillService billService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;



    // Create a new bill
    @PostMapping(path = "createBill")
    public ResponseEntity<String> createBill(@RequestBody BillEntity billEntity) {
        try {
            // Validate and parse the product details JSON
            validateProductDetails(billEntity.getProductDetail());

            // Save the bill
            billService.saveBill(billEntity);

            billService.saveBillToPdf(billEntity);
            return ResponseEntity.ok("Bill created successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating bill: " + e.getMessage());
        }
    }



    @GetMapping("/getAll")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> getAllBills(HttpServletRequest request) {
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
                    // Fetch all bills using the service
                    List<BillEntity> bills = billService.getAllBills();
                    return ResponseEntity.ok(bills);
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

    @GetMapping("/get/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> getBillById(@PathVariable Integer id, HttpServletRequest request) {
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
                    // Fetch the bill by ID using the service
                    BillEntity bill = billService.getBillById(id);
                    if (bill != null) {
                        return ResponseEntity.ok(bill);
                    } else {
                        return ResponseEntity.notFound().build();
                    }
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

    // Validate the product details JSON
    private void validateProductDetails(String productDetailsJson) throws JsonProcessingException {
        if (productDetailsJson == null || productDetailsJson.isEmpty()) {
            throw new IllegalArgumentException("Product details cannot be null or empty.");
        }

        // Try parsing the JSON to ensure it's valid
        objectMapper.readValue(productDetailsJson, new TypeReference<List<Map<String, Object>>>() {});
    }

    @GetMapping("/getPdf/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> getPdf(@PathVariable Integer id, HttpServletRequest request) {
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
                    // Fetch the PDF file (this would be a byte array or InputStream)
                    byte[] pdfBytes = billService.getBillPdfById(id);

                    if (pdfBytes != null) {
                        // Set the content type to 'application/pdf' and return the file
                        return ResponseEntity.ok()
                                .contentType(MediaType.APPLICATION_PDF)
                                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=bill-" + id + ".pdf")
                                .body(pdfBytes);
                    } else {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("PDF not found for this bill.");
                    }
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

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> deleteBill(@PathVariable Integer id, HttpServletRequest request) {
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
                    // Call the service to delete the bill by ID
                    boolean isDeleted = billService.deleteBillById(id);

                    if (isDeleted) {
                        // Return a success response
                        return ResponseEntity.ok("Bill with ID " + id + " deleted successfully.");
                    } else {
                        // Return a not found response if the bill does not exist
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bill not found.");
                    }
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

