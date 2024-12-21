package com.inn.webshop.com.inn.webshop.service;


import com.inn.webshop.com.inn.webshop.data.repository.BillRepository;
import com.inn.webshop.com.inn.webshop.data.repository.CategoryRepository;
import com.inn.webshop.com.inn.webshop.data.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DashboardService {

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    BillRepository billRepository;


    public long getProductCount() {
        return productRepository.count(); // Returns the total number of products
    }

    public long getBillCount() {
        return billRepository.count(); // Returns the total number of bills
    }

    public long getCategoryCount() {
        return categoryRepository.count(); // Returns the total number of categories
    }

}
