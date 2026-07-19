package com.example.DoanJava.controller;

import com.example.DoanJava.model.Product;
import com.example.DoanJava.repository.CategoryRepository;
import com.example.DoanJava.repository.ProductRepository;
import com.example.DoanJava.util.FileUploadUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/products")
@RequiredArgsConstructor
public class AdminProductController {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("products", productRepository.findAll());
        return "admin/product/list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryRepository.findAll());
        return "admin/product/form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Product product = productRepository.findById(id).orElseThrow();
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryRepository.findAll());
        return "admin/product/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute Product product, 
                       BindingResult result,
                       @RequestParam("image") MultipartFile multipartFile,
                       @RequestParam("image2") MultipartFile multipartFile2,
                       @RequestParam("image3") MultipartFile multipartFile3,
                       @RequestParam(value = "flavorsList", required = false) String flavorsList,
                       RedirectAttributes ra,
                       Model model) throws IOException {
        
        // Check for duplicate product name (for new products)
        if (product.getId() == null) {
            java.util.List<Product> existing = productRepository.findByNameContainingIgnoreCase(product.getName());
            boolean isDuplicate = existing.stream()
                    .anyMatch(p -> p.getName().equalsIgnoreCase(product.getName().trim()));
            
            if (isDuplicate) {
                model.addAttribute("error", "Sản phẩm này đã tồn tại trong hệ thống!");
                model.addAttribute("categories", categoryRepository.findAll());
                return "admin/product/form";
            }
        }

        if (product.getDiscountPrice() != null && product.getPrice() != null) {
            if (product.getDiscountPrice() >= product.getPrice()) {
                result.rejectValue("discountPrice", "error.product", "Giá khuyến mãi phải nhỏ hơn giá gốc");
            } else if (product.getDiscountPrice() < product.getPrice() * 0.5) {
                result.rejectValue("discountPrice", "error.product", "Giá khuyến mãi không được thấp hơn 50% giá gốc (giảm tối đa 50%)");
            }
        }

        if (result.hasErrors()) {
            model.addAttribute("categories", categoryRepository.findAll());
            return "admin/product/form";
        }

        Product productToSave;
        if (product.getId() != null) {
            // Edit mode: Load existing to preserve data not in form
            productToSave = productRepository.findById(product.getId()).orElseThrow();
            productToSave.setName(product.getName());
            productToSave.setCategory(product.getCategory());
            productToSave.setStock(product.getStock());
            productToSave.setPrice(product.getPrice());
            productToSave.setDiscountPrice(product.getDiscountPrice());
            productToSave.setDescription(product.getDescription());
            productToSave.setFeatured(product.getFeatured());
        } else {
            // Add mode
            productToSave = product;
        }

        // Handle flavors
        if (flavorsList != null && !flavorsList.isEmpty()) {
            productToSave.setFlavors(Arrays.stream(flavorsList.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList()));
        } else {
            productToSave.setFlavors(null);
        }

        String uploadDir = "src/main/resources/static/images/uploads/";

        // Handle Image 1
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            productToSave.setImageUrl("/images/uploads/" + fileName);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        } else {
            productToSave.setImageUrl(product.getImageUrl());
        }
        
        // Handle Image 2
        if (!multipartFile2.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile2.getOriginalFilename());
            productToSave.setImageUrl2("/images/uploads/" + fileName);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile2);
        } else {
            productToSave.setImageUrl2(product.getImageUrl2());
        }

        // Handle Image 3
        if (!multipartFile3.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile3.getOriginalFilename());
            productToSave.setImageUrl3("/images/uploads/" + fileName);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile3);
        } else {
            productToSave.setImageUrl3(product.getImageUrl3());
        }

        productRepository.save(productToSave);
        ra.addFlashAttribute("success", "Đã lưu sản phẩm thành công!");
        return "redirect:/admin/products";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        productRepository.deleteById(id);
        ra.addFlashAttribute("success", "Đã xóa sản phẩm thành công!");
        return "redirect:/admin/products";
    }
}
