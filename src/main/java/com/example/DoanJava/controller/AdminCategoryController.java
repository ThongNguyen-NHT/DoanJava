package com.example.DoanJava.controller;

import com.example.DoanJava.model.Category;
import com.example.DoanJava.repository.CategoryRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {
    private final CategoryRepository categoryRepository;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("categories", categoryRepository.findAll());
        return "admin/category/list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("category", new Category());
        return "admin/category/form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Category category = categoryRepository.findById(id).orElseThrow();
        model.addAttribute("category", category);
        return "admin/category/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute Category category, BindingResult result, RedirectAttributes ra) {
        if (result.hasErrors()) {
            return "admin/category/form";
        }
        
        if (category.getId() != null) {
            Category existing = categoryRepository.findById(category.getId()).orElseThrow();
            existing.setName(category.getName());
            categoryRepository.save(existing);
            ra.addFlashAttribute("success", "Đã cập nhật danh mục thành công!");
        } else {
            categoryRepository.save(category);
            ra.addFlashAttribute("success", "Đã thêm danh mục mới thành công!");
        }
        return "redirect:/admin/categories";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            categoryRepository.deleteById(id);
            ra.addFlashAttribute("success", "Đã xóa danh mục thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Không thể xóa danh mục này vì vẫn còn sản phẩm bên trong!");
        }
        return "redirect:/admin/categories";
    }
}
