package com.example.DoanJava.service;

import com.example.DoanJava.model.CartItem;
import com.example.DoanJava.model.Coupon;
import com.example.DoanJava.model.Product;
import com.example.DoanJava.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@SessionScope
@RequiredArgsConstructor
public class CartService {
    private final ProductService productService;
    private final CouponRepository couponRepository;
    private final com.example.DoanJava.repository.OrderRepository orderRepository;
    private final com.example.DoanJava.repository.UserRepository userRepository;
    
    private final Map<String, CartItem> items = new HashMap<>(); // Key: productId + flavor
    private Coupon appliedCoupon;
    private String couponErrorMessage;
    private String cartErrorMessage;

    public boolean addToCart(Long productId, Integer quantity, String flavor) {
        cartErrorMessage = null;
        Product product = productService.getProductById(productId);
        if (product == null) {
            cartErrorMessage = "Sản phẩm không tồn tại!";
            return false;
        }

        String key = productId + (flavor != null ? "_" + flavor : "");
        CartItem item = items.get(key);
        int currentQtyInCart = (item != null) ? item.getQuantity() : 0;
        
        if (product.getStock() < currentQtyInCart + quantity) {
            cartErrorMessage = "Chỉ còn lại " + product.getStock() + " sản phẩm trong kho!";
            return false;
        }

        if (item == null) {
            item = new CartItem(
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    quantity,
                    product.getImageUrl(),
                    flavor
            );
            items.put(key, item);
        } else {
            item.setQuantity(item.getQuantity() + quantity);
        }
        checkCouponValidity(); 
        return true;
    }

    public void removeProduct(String key) {
        items.remove(key);
        checkCouponValidity();
    }

    public boolean updateQuantity(String key, Integer quantity) {
        cartErrorMessage = null;
        CartItem item = items.get(key);
        if (item != null) {
            if (quantity <= 0) {
                items.remove(key);
            } else {
                Product product = productService.getProductById(item.getProductId());
                if (product != null && product.getStock() < quantity) {
                    cartErrorMessage = "Chỉ còn lại " + product.getStock() + " sản phẩm trong kho!";
                    return false;
                }
                item.setQuantity(quantity);
            }
        }
        checkCouponValidity();
        return true;
    }

    public String getCartErrorMessage() {
        return cartErrorMessage;
    }

    public boolean applyCoupon(String code) {
        couponErrorMessage = null;
        this.appliedCoupon = null; // ALWAYS clear before new attempt to be strict

        Optional<Coupon> couponOpt = couponRepository.findByCode(code);
        
        if (couponOpt.isEmpty()) {
            couponErrorMessage = "Mã giảm giá không tồn tại!";
            return false;
        }

        Coupon coupon = couponOpt.get();
        if (!coupon.isActive()) {
            couponErrorMessage = "Mã giảm giá này đã bị vô hiệu hóa!";
            return false;
        }

        if (coupon.getExpiryDate() != null && coupon.getExpiryDate().isBefore(LocalDate.now())) {
            couponErrorMessage = "Mã giảm giá này đã hết hạn!";
            return false;
        }

        // Check if user has already used this coupon
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            String username = auth.getName();
            userRepository.findByUsername(username).ifPresent(user -> {
                if (orderRepository.existsByUserIdAndAppliedCouponCode(user.getId(), code)) {
                    couponErrorMessage = "Bạn đã sử dụng mã giảm giá này rồi!";
                }
            });
            if (couponErrorMessage != null) return false;
        }

        double currentTotal = getTotalBeforeDiscount();
        if (coupon.getMinOrderAmount() != null && currentTotal < coupon.getMinOrderAmount()) {
            couponErrorMessage = "Đơn hàng phải từ " + String.format("%,.0f", coupon.getMinOrderAmount()) + " đ để sử dụng mã này! (Hiện tại: " + String.format("%,.0f", currentTotal) + " đ)";
            return false;
        }

        this.appliedCoupon = coupon;
        return true;
    }

    private void checkCouponValidity() {
        if (appliedCoupon != null) {
            if (appliedCoupon.getMinOrderAmount() != null && getTotalBeforeDiscount() < appliedCoupon.getMinOrderAmount()) {
                appliedCoupon = null;
                // Silent removal when cart changes, but user will see total update
            }
        }
    }

    public String getCouponErrorMessage() {
        return couponErrorMessage;
    }

    public void removeCoupon() {
        this.appliedCoupon = null;
        this.couponErrorMessage = null;
    }

    public Coupon getAppliedCoupon() {
        return appliedCoupon;
    }

    public Double getTotalBeforeDiscount() {
        return items.values().stream()
                .mapToDouble(CartItem::getSubTotal)
                .sum();
    }

    public Double getTotalAmount() {
        double total = getTotalBeforeDiscount();
        if (appliedCoupon != null) {
            if (appliedCoupon.getType() == Coupon.CouponType.PERCENTAGE) {
                total = total * (1 - appliedCoupon.getValue() / 100);
            } else {
                total = total - appliedCoupon.getValue();
            }
        }
        return Math.max(0, total);
    }

    public Double getDiscountAmount() {
        return getTotalBeforeDiscount() - getTotalAmount();
    }

    public Collection<CartItem> getItems() {
        return items.values();
    }

    public void clearCart() {
        items.clear();
        appliedCoupon = null;
        couponErrorMessage = null;
    }

    public int getCount() {
        return items.values().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }
}
