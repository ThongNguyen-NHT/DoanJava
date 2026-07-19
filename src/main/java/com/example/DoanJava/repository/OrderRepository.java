package com.example.DoanJava.repository;

import com.example.DoanJava.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserIdOrderByOrderDateDesc(Long userId);
    boolean existsByUserIdAndAppliedCouponCode(Long userId, String appliedCouponCode);
}
