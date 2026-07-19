package com.example.DoanJava.service;

import com.example.DoanJava.model.Product;
import com.example.DoanJava.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> getFeaturedProducts() {
        return productRepository.findByFeaturedTrue();
    }

    public List<Product> getLatestProducts() {
        return productRepository.findTop8ByOrderByIdDesc();
    }

    public List<Product> getDiscountedProducts() {
        return productRepository.findByDiscountPriceIsNotNull();
    }

    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    public List<Product> searchProducts(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword);
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }
}
