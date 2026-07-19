package com.example.DoanJava.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "coupons")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Mã giảm giá không được để trống")
    @Column(unique = true)
    private String code;

    @NotNull(message = "Loại giảm giá không được để trống")
    private CouponType type; // PERCENTAGE or FIXED_AMOUNT

    @NotNull(message = "Giá trị giảm giá không được để trống")
    @Positive
    private Double value;

    @Positive
    private Double minOrderAmount; // Minimum order amount to apply this coupon

    private LocalDate expiryDate;

    private boolean active = true;

    public enum CouponType {
        PERCENTAGE, FIXED_AMOUNT
    }
}
