package com.example.DoanJava.controller;

import com.example.DoanJava.model.Order;
import com.example.DoanJava.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {
    private final OrderRepository orderRepository;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("orders", orderRepository.findAll());
        return "admin/order/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Order order = orderRepository.findById(id).orElseThrow();
        model.addAttribute("order", order);
        return "admin/order/detail";
    }

    @PostMapping("/update-status")
    public String updateStatus(@RequestParam Long orderId, @RequestParam Order.OrderStatus status, RedirectAttributes ra) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        
        // Prevent changing status if it's already DELIVERED or CANCELLED
        if (order.getStatus() == Order.OrderStatus.DELIVERED || order.getStatus() == Order.OrderStatus.CANCELLED) {
            ra.addFlashAttribute("error", "Không thể thay đổi trạng thái của đơn hàng đã giao hoặc đã hủy!");
            return "redirect:/admin/orders/" + orderId;
        }

        order.setStatus(status);
        orderRepository.save(order);
        ra.addFlashAttribute("success", "Đã cập nhật trạng thái đơn hàng #" + orderId + " thành công!");
        return "redirect:/admin/orders/" + orderId;
    }

    @GetMapping("/delete/{id}")
    public String deleteOrder(@PathVariable Long id, RedirectAttributes ra) {
        Order order = orderRepository.findById(id).orElseThrow();
        if (order.getStatus() == Order.OrderStatus.DELIVERED || order.getStatus() == Order.OrderStatus.CANCELLED) {
            orderRepository.deleteById(id);
            ra.addFlashAttribute("success", "Đã xóa đơn hàng #" + id + " thành công!");
        } else {
            ra.addFlashAttribute("error", "Chỉ có thể xóa đơn hàng đã giao hoặc đã hủy!");
        }
        return "redirect:/admin/orders";
    }
}
