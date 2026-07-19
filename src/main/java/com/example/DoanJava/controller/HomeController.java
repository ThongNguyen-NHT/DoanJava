package com.example.DoanJava.controller;

import com.example.DoanJava.service.CategoryService;
import com.example.DoanJava.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final CategoryService categoryService;
    private final ProductService productService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("featuredProducts", productService.getFeaturedProducts());
        model.addAttribute("latestProducts", productService.getLatestProducts());
        model.addAttribute("discountedProducts", productService.getDiscountedProducts());
        return "index";
    }

    @GetMapping("/403")
    public String accessDenied() {
        return "error/403";
    }
}
