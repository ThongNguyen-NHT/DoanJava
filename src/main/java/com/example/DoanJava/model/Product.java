package com.example.DoanJava.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"category", "flavors"})
@EqualsAndHashCode(exclude = {"category", "flavors"})
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Column(nullable = false)
    private String name;

    @Column(length = 2000)
    private String description;

    @NotNull(message = "Giá sản phẩm không được để trống")
    @Positive(message = "Giá sản phẩm phải là số dương")
    @Column(nullable = false)
    private Double price;

    private Double discountPrice; // Price after promotion

    @Column(length = 1000)
    private String imageUrl;
    @Column(length = 1000)
    private String imageUrl2;
    @Column(length = 1000)
    private String imageUrl3;

    @NotNull(message = "Số lượng tồn kho không được để trống")
    @Min(value = 0, message = "Số lượng không được âm")
    private Integer stock;

    private Boolean featured = false;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ElementCollection
    @CollectionTable(name = "product_flavors", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "flavor")
    private List<String> flavors;
}
