package com.example.DoanJava.controller;

import com.example.DoanJava.model.Product;
import com.example.DoanJava.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductApiController {
    private final ProductRepository productRepository;

    @GetMapping("/search-suggestions")
    public List<ProductSuggestion> getSearchSuggestions(@RequestParam String keyword) {
        if (keyword == null || keyword.trim().length() < 2) {
            return List.of();
        }
        
        List<Product> products = productRepository.findByNameContainingIgnoreCase(keyword.trim());
        
        // Limit to 5 suggestions
        return products.stream()
                .limit(5)
                .map(p -> new ProductSuggestion(
                        p.getId(),
                        p.getName(),
                        p.getDiscountPrice() != null ? p.getDiscountPrice() : p.getPrice(),
                        p.getImageUrl()
                ))
                .collect(Collectors.toList());
    }

    public record ProductSuggestion(Long id, String name, Double price, String imageUrl) {}
}
