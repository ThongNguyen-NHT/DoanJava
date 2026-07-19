package com.example.DoanJava.controller;

import com.example.DoanJava.model.Coupon;
import com.example.DoanJava.repository.CouponRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/coupons")
@RequiredArgsConstructor
public class AdminCouponController {
    private final CouponRepository couponRepository;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("coupons", couponRepository.findAll());
        return "admin/coupon/list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("coupon", new Coupon());
        return "admin/coupon/form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Coupon coupon = couponRepository.findById(id).orElseThrow();
        model.addAttribute("coupon", coupon);
        return "admin/coupon/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute Coupon coupon, BindingResult result) {
        if (coupon.getType() == Coupon.CouponType.PERCENTAGE && coupon.getValue() != null && coupon.getValue() > 50) {
            result.rejectValue("value", "error.coupon", "Giảm giá phần trăm không được vượt quá 50%");
        }
        if (coupon.getType() == Coupon.CouponType.FIXED_AMOUNT && coupon.getValue() != null && coupon.getMinOrderAmount() != null) {
            if (coupon.getValue() >= coupon.getMinOrderAmount()) {
                result.rejectValue("value", "error.coupon", "Số tiền giảm phải nhỏ hơn số tiền đơn hàng tối thiểu");
            }
        }

        if (result.hasErrors()) {
            return "admin/coupon/form";
        }
        couponRepository.save(coupon);
        return "redirect:/admin/coupons";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        couponRepository.deleteById(id);
        return "redirect:/admin/coupons";
    }
}
