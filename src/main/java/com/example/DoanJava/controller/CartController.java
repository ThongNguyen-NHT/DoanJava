package com.example.DoanJava.controller;

import com.example.DoanJava.model.Order;
import com.example.DoanJava.repository.UserRepository;
import com.example.DoanJava.service.CartService;
import com.example.DoanJava.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    private final OrderService orderService;
    private final UserRepository userRepository;

    @GetMapping
    public String viewCart(Model model) {
        model.addAttribute("items", cartService.getItems());
        model.addAttribute("totalBeforeDiscount", cartService.getTotalBeforeDiscount());
        model.addAttribute("discountAmount", cartService.getDiscountAmount());
        model.addAttribute("total", cartService.getTotalAmount());
        model.addAttribute("appliedCoupon", cartService.getAppliedCoupon());
        return "cart/index";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam Long productId, 
                            @RequestParam Integer quantity, 
                            @RequestParam(required = false) String flavor,
                            RedirectAttributes ra) {
        if (!cartService.addToCart(productId, quantity, flavor)) {
            ra.addFlashAttribute("error", cartService.getCartErrorMessage() != null ? cartService.getCartErrorMessage() : "Không thể thêm vào giỏ hàng!");
            return "redirect:/products/" + productId;
        }
        ra.addFlashAttribute("success", "Đã thêm vào giỏ hàng!");
        return "redirect:/cart";
    }

    @GetMapping("/remove/{key}")
    public String removeFromCart(@PathVariable String key) {
        cartService.removeProduct(key);
        return "redirect:/cart";
    }

    @PostMapping("/update")
    public String updateQuantity(@RequestParam String key, @RequestParam Integer quantity, RedirectAttributes ra) {
        if (!cartService.updateQuantity(key, quantity)) {
            ra.addFlashAttribute("error", cartService.getCartErrorMessage());
        }
        return "redirect:/cart";
    }

    @PostMapping("/apply-coupon")
    public String applyCoupon(@RequestParam String code, RedirectAttributes ra) {
        if (cartService.applyCoupon(code)) {
            ra.addFlashAttribute("success", "Áp dụng mã giảm giá thành công!");
        } else {
            ra.addFlashAttribute("error", cartService.getCouponErrorMessage());
        }
        return "redirect:/cart";
    }

    @GetMapping("/remove-coupon")
    public String removeCoupon() {
        cartService.removeCoupon();
        return "redirect:/cart";
    }

    @GetMapping("/checkout")
    public String checkoutForm(Model model) {
        if (cartService.getCount() == 0) {
            return "redirect:/products";
        }
        
        Order order = new Order();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        userRepository.findByUsername(username).ifPresent(user -> {
            order.setCustomerName(user.getFullName());
            order.setEmail(user.getEmail());
            order.setPhoneNumber(user.getPhone());
            order.setShippingAddress(user.getAddress());
        });

        model.addAttribute("order", order);
        model.addAttribute("items", cartService.getItems());
        model.addAttribute("totalBeforeDiscount", cartService.getTotalBeforeDiscount());
        model.addAttribute("discountAmount", cartService.getDiscountAmount());
        model.addAttribute("total", cartService.getTotalAmount());
        return "cart/checkout";
    }

    @PostMapping("/checkout")
    public String processCheckout(@ModelAttribute("order") Order order) {
        if (cartService.getCount() == 0) {
            return "redirect:/products";
        }
        
        // Ensure total price reflects discount
        order.setTotalPrice(cartService.getTotalAmount());
        
        // Pass coupon code to order
        if (cartService.getAppliedCoupon() != null) {
            order.setAppliedCouponCode(cartService.getAppliedCoupon().getCode());
        }
        
        orderService.createOrder(order, cartService.getItems());
        cartService.clearCart();
        return "redirect:/cart/success";
    }

    @GetMapping("/success")
    public String success() {
        return "cart/success";
    }
}
