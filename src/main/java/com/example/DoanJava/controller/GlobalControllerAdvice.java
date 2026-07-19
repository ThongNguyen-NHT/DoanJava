package com.example.DoanJava.controller;

import com.example.DoanJava.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {
    private final CartService cartService;

    @ModelAttribute("cartCount")
    public int getCartCount() {
        return cartService.getCount();
    }

    @ModelAttribute("cartTotal")
    public Double getCartTotal() {
        return cartService.getTotalAmount();
    }
}
