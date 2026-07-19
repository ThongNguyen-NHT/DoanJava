package com.example.DoanJava.controller;

import com.example.DoanJava.model.Order;
import com.example.DoanJava.model.Product;
import com.example.DoanJava.repository.OrderRepository;
import com.example.DoanJava.repository.ProductRepository;
import com.example.DoanJava.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @GetMapping
    public String dashboard(Model model) {
        List<Order> allOrders = orderRepository.findAll();
        
        double totalRevenue = allOrders.stream()
                .filter(o -> o.getStatus() == Order.OrderStatus.DELIVERED)
                .mapToDouble(Order::getTotalPrice)
                .sum();
        
        long newOrdersCount = allOrders.stream()
                .filter(o -> o.getStatus() == Order.OrderStatus.PENDING)
                .count();

        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("newOrdersCount", newOrdersCount);
        model.addAttribute("totalCustomers", userRepository.count());
        model.addAttribute("lowStockProducts", productRepository.findAll().stream()
                .filter(p -> p.getStock() < 5)
                .toList());
        
        return "admin/dashboard";
    }
}
