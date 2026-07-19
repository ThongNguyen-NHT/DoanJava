package com.example.DoanJava.service;

import com.example.DoanJava.model.CartItem;
import com.example.DoanJava.model.Order;
import com.example.DoanJava.model.OrderDetail;
import com.example.DoanJava.model.Product;
import com.example.DoanJava.model.User;
import com.example.DoanJava.repository.OrderDetailRepository;
import com.example.DoanJava.repository.OrderRepository;
import com.example.DoanJava.repository.ProductRepository;
import com.example.DoanJava.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional
    public Order createOrder(Order order, Collection<CartItem> cartItems) {
        // Set basic info
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(Order.OrderStatus.PENDING);
        
        // Associate with logged-in user if exists
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        userRepository.findByUsername(username).ifPresent(order::setUser);

        // Calculate total and prepare details
        double total = 0;
        List<OrderDetail> details = new ArrayList<>();
        
        for (CartItem item : cartItems) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            
            if (product.getStock() < item.getQuantity()) {
                throw new RuntimeException("Insufficient stock for " + product.getName());
            }

            // Subtract stock
            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);

            OrderDetail detail = OrderDetail.builder()
                    .order(order)
                    .product(product)
                    .quantity(item.getQuantity())
                    .price(item.getPrice())
                    .flavor(item.getFlavor())
                    .build();
            details.add(detail);
        }
        
        // We use the total price passed from the controller (after discount)
        // or recalculate it here. For safety, let's recalculate with coupon if possible,
        // but for now, we'll assume order.totalPrice is already set by controller.
        
        order.setOrderDetails(details);
        return orderRepository.save(order);
    }

    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new RuntimeException("Only pending orders can be cancelled");
        }
        
        // Restore stock
        for (OrderDetail detail : order.getOrderDetails()) {
            Product product = detail.getProduct();
            if (product != null) {
                product.setStock(product.getStock() + detail.getQuantity());
                productRepository.save(product);
            }
        }
        
        order.setStatus(Order.OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    public List<Order> getOrdersByUser(User user) {
        return orderRepository.findByUserIdOrderByOrderDateDesc(user.getId());
    }
}
