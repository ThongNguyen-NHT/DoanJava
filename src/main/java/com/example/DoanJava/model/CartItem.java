package com.example.DoanJava.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItem {
    private Long productId;
    private String name;
    private Double price;
    private Integer quantity;
    private String imageUrl;
    private String flavor;

    public Double getSubTotal() {
        return price * quantity;
    }
}
