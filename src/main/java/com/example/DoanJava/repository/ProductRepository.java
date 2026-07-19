package com.example.DoanJava.repository;

import com.example.DoanJava.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByFeaturedTrue();
    List<Product> findTop8ByOrderByIdDesc();
    List<Product> findByCategoryId(Long categoryId);
    List<Product> findByNameContainingIgnoreCase(String keyword);
    List<Product> findByDiscountPriceIsNotNull();
}
