package com.example.DoanJava.controller;

import com.example.DoanJava.model.Review;
import com.example.DoanJava.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/reviews")
@RequiredArgsConstructor
public class AdminReviewController {
    private final ReviewRepository reviewRepository;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("reviews", reviewRepository.findAll());
        return "admin/review/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        reviewRepository.deleteById(id);
        return "redirect:/admin/reviews";
    }
}
