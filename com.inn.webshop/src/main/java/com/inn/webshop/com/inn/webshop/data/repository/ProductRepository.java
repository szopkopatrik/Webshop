package com.inn.webshop.com.inn.webshop.data.repository;

import com.inn.webshop.com.inn.webshop.data.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<ProductEntity, Integer> {
    List<ProductEntity> findAll();
    List<ProductEntity> findByCategoryEntityId(Integer categoryId);
}
