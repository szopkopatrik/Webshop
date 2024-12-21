package com.inn.webshop.com.inn.webshop.data.repository;

import com.inn.webshop.com.inn.webshop.data.entity.CategoryEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Locale;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Integer> {

    CategoryEntity findByName(String name);

}
