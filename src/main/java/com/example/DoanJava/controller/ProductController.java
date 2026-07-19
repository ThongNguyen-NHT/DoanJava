package com.example.DoanJava.controller;

import com.example.DoanJava.model.Product;
import com.example.DoanJava.repository.ProductRepository;
import com.example.DoanJava.repository.ReviewRepository;
import com.example.DoanJava.service.CategoryService;
import com.example.DoanJava.service.ProductService;
import com.example.DoanJava.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final CategoryService categoryService;
    private final ReviewRepository reviewRepository;
    private final UserService userService;
    private final ProductRepository productRepository;

    @InitBinder
    public void initBinder(org.springframework.web.bind.WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new org.springframework.beans.propertyeditors.StringTrimmerEditor(true));
    }

    @GetMapping
    public String listProducts(
            @RequestParam(required = false) Long category,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false, defaultValue = "latest") String sort,
            Model model) {
        
        List<Product> products;

        // 1. Load data from DB
        if (category != null) {
            products = productRepository.findByCategoryId(category);
        } else if (keyword != null && !keyword.isEmpty()) {
            products = productRepository.findByNameContainingIgnoreCase(keyword);
        } else {
            products = productRepository.findAll();
        }

        // Ensure list is mutable
        products = new ArrayList<>(products != null ? products : new ArrayList<>());

        // 2. Filter by Price (Use selling price: discountPrice if present, else regular price)
        if (minPrice != null || maxPrice != null) {
            products = products.stream().filter(p -> {
                double sellingPrice = p.getDiscountPrice() != null ? p.getDiscountPrice() : (p.getPrice() != null ? p.getPrice() : 0);
                boolean minMatch = (minPrice == null || sellingPrice >= minPrice);
                boolean maxMatch = (maxPrice == null || sellingPrice <= maxPrice);
                return minMatch && maxMatch;
            }).collect(Collectors.toList());
        }

        // 3. Sort
        products.sort((p1, p2) -> {
            double v1 = p1.getDiscountPrice() != null ? p1.getDiscountPrice() : (p1.getPrice() != null ? p1.getPrice() : 0);
            double v2 = p2.getDiscountPrice() != null ? p2.getDiscountPrice() : (p2.getPrice() != null ? p2.getPrice() : 0);
            
            if ("priceAsc".equals(sort)) return Double.compare(v1, v2);
            if ("priceDesc".equals(sort)) return Double.compare(v2, v1);
            
            // Latest (ID desc)
            return Long.compare(p2.getId(), p1.getId());
        });

        model.addAttribute("products", products);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("selectedCategory", category);
        model.addAttribute("keyword", keyword);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("sort", sort);
        
        return "product/list";
    }

    @GetMapping("/{id}")
    public String productDetail(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        if (product == null) return "redirect:/products";
        
        model.addAttribute("product", product);
        model.addAttribute("reviews", reviewRepository.findByProductIdAndApprovedTrueOrderByCreatedAtDesc(id));
        return "product/detail";
    }

    @GetMapping("/quick-view/{id}")
    public String quickView(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        if (product == null) return "error/404";
        model.addAttribute("product", product);
        model.addAttribute("reviews", reviewRepository.findByProductIdAndApprovedTrueOrderByCreatedAtDesc(id));
        return "product/list :: quickViewModalContent";
    }

    @PostMapping("/review")
    public String postReview(@RequestParam Long productId, @RequestParam Double rating, @RequestParam String content) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        com.example.DoanJava.model.User user = userService.getUserByUsername(username);
        Product product = productService.getProductById(productId);
        
        if (user != null && product != null) {
            com.example.DoanJava.model.Review review = com.example.DoanJava.model.Review.builder()
                    .product(product).user(user).rating(rating).content(content)
                    .createdAt(LocalDateTime.now()).approved(true).build();
            reviewRepository.save(review);
        }
        return "redirect:/products/" + productId;
    }
}
