package com.example.DoanJava.config;

import com.example.DoanJava.model.*;
import com.example.DoanJava.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Component
@RequiredArgsConstructor
@lombok.extern.slf4j.Slf4j
public class DataSeeder implements CommandLineRunner {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CouponRepository couponRepository;
    private final OrderRepository orderRepository;
    private final ReviewRepository reviewRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        try {
            log.info("Starting data seeding...");
            // 1. Coupons
            if (couponRepository.count() == 0) {
                couponRepository.save(Coupon.builder().code("SWEET10").type(Coupon.CouponType.PERCENTAGE).value(10.0).minOrderAmount(200000.0).expiryDate(LocalDate.now().plusMonths(1)).active(true).build());
                couponRepository.save(Coupon.builder().code("HELLO2026").type(Coupon.CouponType.FIXED_AMOUNT).value(50000.0).minOrderAmount(500000.0).expiryDate(LocalDate.now().plusMonths(2)).active(true).build());
            }

            // 2. Roles
            Role userRole = roleRepository.findByName("ROLE_USER").orElseGet(() -> roleRepository.save(Role.builder().name("ROLE_USER").build()));
            Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElseGet(() -> roleRepository.save(Role.builder().name("ROLE_ADMIN").build()));

            // 3. Users
            if (userRepository.count() == 0) {
                userRepository.save(User.builder().username("admin").password(passwordEncoder.encode("admin12345")).email("admin@gmail.com").fullName("Administrator").phone("0987654321").address("Admin Office").roles(new HashSet<>(Arrays.asList(adminRole))).enabled(true).build());
                userRepository.save(User.builder().username("user").password(passwordEncoder.encode("user12345")).email("user@gmail.com").fullName("Khách hàng mẫu").phone("0123456789").address("123 Phố Bánh Ngọt").roles(new HashSet<>(Arrays.asList(userRole))).enabled(true).build());
            }
            // 4. Categories & Products
            if (categoryRepository.count() == 0 || productRepository.count() == 0) {
                Category c1 = categoryRepository.findByName("Bánh Kem").orElseGet(() -> categoryRepository.saveAndFlush(Category.builder().name("Bánh Kem").build()));
                Category c2 = categoryRepository.findByName("Bánh Mì").orElseGet(() -> categoryRepository.saveAndFlush(Category.builder().name("Bánh Mì").build()));
                Category c3 = categoryRepository.findByName("Bánh Quy").orElseGet(() -> categoryRepository.saveAndFlush(Category.builder().name("Bánh Quy").build()));
                Category c4 = categoryRepository.findByName("Cupcake").orElseGet(() -> categoryRepository.saveAndFlush(Category.builder().name("Cupcake").build()));
                Category c5 = categoryRepository.findByName("Donut").orElseGet(() -> categoryRepository.saveAndFlush(Category.builder().name("Donut").build()));
                Category c6 = categoryRepository.findByName("Bánh Mặn").orElseGet(() -> categoryRepository.saveAndFlush(Category.builder().name("Bánh Mặn").build()));

                if (productRepository.count() == 0) {
                    List<Product> products = Arrays.asList(
                        // Bánh Kem (c1)
                        Product.builder().name("Bánh Kem Dâu Tây").price(250000.0).discountPrice(210000.0).description("Dâu tây tươi Đà Lạt kết hợp kem béo ngậy.").imageUrl("https://cdn.tgdd.vn/Files/2021/07/28/1371472/cach-lam-banh-kem-dau-tay-mem-min-ngon-kho-cuong-cuc-hap-dan-202210121614169729.jpg").category(c1).stock(10).featured(true).build(),
                        Product.builder().name("Bánh Kem Bắp").price(280000.0).discountPrice(240000.0).description("Hương vị bắp Mỹ ngọt thanh, thơm dịu.").imageUrl("https://beptruong.edu.vn/wp-content/uploads/2019/03/banh-kem-bap.jpg").category(c1).stock(15).featured(true).build(),
                        Product.builder().name("Red Velvet").price(320000.0).discountPrice(280000.0).description("Bánh nhung đỏ sang trọng với lớp kem cheese mịn màng.").imageUrl("https://thescranline.com/wp-content/uploads/2023/06/RED-VELVET-CAKE-23-S-01.jpg").category(c1).stock(5).featured(true).build(),
                        Product.builder().name("Tiramisu Cake").price(300000.0).description("Sự kết hợp hoàn hảo giữa cà phê, cacao và phô mai Mascarpone.").imageUrl("https://images.unsplash.com/photo-1571877227200-a0d98ea607e9?w=800&q=80").category(c1).stock(8).build(),
                        Product.builder().name("Bánh Kem Socola").price(270000.0).description("Socola Bỉ đậm đà cho những tín đồ hảo ngọt.").imageUrl("https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=800&q=80").category(c1).stock(12).build(),

                        // Bánh Mì (c2)
                        Product.builder().name("Bánh Sừng Bò").price(35000.0).description("Croissant Pháp giòn tan, thơm nức mùi bơ.").imageUrl("https://images.unsplash.com/photo-1555507036-ab1f4038808a?w=800&q=80").category(c2).stock(50).featured(true).build(),
                        Product.builder().name("Bánh Mì Hoa Cúc").price(65000.0).description("Bánh mì Harrys Pháp mềm mịn, thơm hương hoa cúc.").imageUrl("https://www.lobanhmi.vn/wp-content/uploads/2025/07/banh-mi-hoa-cuc.webp").category(c2).stock(40).build(),
                        Product.builder().name("Bánh Mì Gối").price(25000.0).description("Bánh mì lát mềm cho bữa sáng tiện lợi.").imageUrl("https://cdn.tgdd.vn/Files/2021/07/22/1370016/bo-tui-cong-thuc-lam-banh-mi-goi-sandwich-thom-ngon-mem-min-de-lam-202107221343365544.jpg").category(c2).stock(100).build(),
                        Product.builder().name("Bánh Mì Bơ Tỏi").price(45000.0).description("Thơm nồng vị bơ tỏi và phô mai kéo sợi.").imageUrl("https://th.bing.com/th/id/R.687a6f0fcfbd338868f27f01cf071156?rik=Tp1%2bgvO3HRFC4g&riu=http%3a%2f%2fwww.savourydays.com%2fwp-content%2fuploads%2f2020%2f03%2fcach-lam-banh-mi-bo-toi-han-quoc-banner.jpg&ehk=%2fhAjRUKPI9Vyw921keosYVNUrOojKaO5oMrcFQiQe9E%3d&risl=&pid=ImgRaw&r=0").category(c2).stock(30).build(),
                        Product.builder().name("Bánh Mì Baguette").price(20000.0).description("Bánh mì Pháp dài vỏ giòn ruột mềm.").imageUrl("https://anhquanbakery.com/uploads/images/banh-my-baguette.jpg").category(c2).stock(60).build(),

                        // Bánh Quy (c3)
                        Product.builder().name("Bánh Macaron").price(150000.0).description("Hộp 6 bánh với nhiều hương vị tinh tế.").imageUrl("https://thf.bing.com/th/id/OIP.iHGgZn10d_aq05CPFaefFwHaEL?r=0&o=7&cb=thfc1falconrm=3&rs=1&pid=ImgDetMain&o=7&rm=3").category(c3).stock(20).featured(true).build(),
                        Product.builder().name("Bánh Quy Yến Mạch").price(85000.0).description("Giòn rụm, tốt cho sức khỏe.").imageUrl("https://daylambanh.edu.vn/wp-content/uploads/2017/11/cach-lam-banh-quy-yen-mach.jpg").category(c3).stock(60).build(),
                        Product.builder().name("Cookies Socola Chip").price(75000.0).description("Nhiều hạt socola, món khoái khẩu của trẻ em.").imageUrl("https://images.unsplash.com/photo-1499636136210-6f4ee915583e?w=800&q=80").category(c3).stock(80).build(),
                        Product.builder().name("Bánh Quy Bơ").price(120000.0).description("Vị bơ truyền thống tan trong miệng.").imageUrl("https://tse4.mm.bing.net/th/id/OIP.E3AqwfYWdpvUleiw-nlniQHaEK?r=0&cb=thfc1falcon&rs=1&pid=ImgDetMain&o=7&rm=3").category(c3).stock(40).build(),
                        Product.builder().name("Bánh Quy Gừng").price(95000.0).description("Ấm áp vị gừng cho mùa lễ hội.").imageUrl("https://media.doisongvietnam.vn/u/rootimage/editor/2016/12/22/23/30/w825/mon1482402654_1475.jpg").category(c3).stock(25).build(),

                        // Cupcake (c4)
                        Product.builder().name("Cupcake Socola").price(25000.0).description("Cốt bánh socola ẩm mịn, kem béo.").imageUrl("https://tse1.mm.bing.net/th/id/OIP.3jDFilfYAa1rEae9dep1GQHaEL?r=0&cb=thfc1falcon&rs=1&pid=ImgDetMain&o=7&rm=3").category(c4).stock(30).flavors(Arrays.asList("Socola truyền thống", "Socola Bạc hà", "Socola Đắng")).build(),
                        Product.builder().name("Cupcake Trà Xanh").price(28000.0).description("Vị Matcha Nhật thanh khiết.").imageUrl("https://tse2.mm.bing.net/th/id/OIP.H_hdTD1gg8K3F3YyiA8R4AHaEK?r=0&cb=thfc1falcon&rs=1&pid=ImgDetMain&o=7&rm=3").category(c4).stock(25).flavors(Arrays.asList("Trà xanh nguyên bản", "Trà xanh Kem sữa")).featured(true).build(),
                        Product.builder().name("Cupcake Dâu").price(25000.0).description("Ngọt ngào hương dâu tây.").imageUrl("https://tse3.mm.bing.net/th/id/OIP.-3XbL4o4WXjeDAjSUNJdYAHaE8?r=0&cb=thfc1falcon&rs=1&pid=ImgDetMain&o=7&rm=3").category(c4).stock(35).build(),
                        Product.builder().name("Cupcake Vani").price(22000.0).description("Hương Vani truyền thống thơm dịu.").imageUrl("https://tse3.mm.bing.net/th/id/OIP.lE-x_V_iDgozmz4Qv09PowHaHa?r=0&cb=thfc1falcon&rs=1&pid=ImgDetMain&o=7&rm=3").category(c4).stock(50).build(),
                        Product.builder().name("Cupcake Việt Quất").price(30000.0).description("Nhân mứt việt quất tươi mọng.").imageUrl("https://img-global.cpcdn.com/recipes/de1faa3cfaeb50d2/1200x630cq80/photo.jpg").category(c4).stock(20).build(),

                        // Donut (c5)
                        Product.builder().name("Donut Socola").price(22000.0).description("Bánh Donut phủ lớp socola bóng bẩy, ngọt ngào.").imageUrl("https://file.hstatic.net/1000396324/file/banh-donut-socola-1_748d83977f934d649f38e4b17aa9f229.jpg").category(c5).stock(45).build(),
                        Product.builder().name("Donut Dâu").price(22000.0).description("Donut phủ kem dâu hồng rực rỡ, thơm lừng.").imageUrl("https://tse1.mm.bing.net/th/id/OIP.PV7ErrweS7hEEUPUm7JdQQHaEL?r=0&cb=thfc1falcon&rs=1&pid=ImgDetMain&o=7&rm=3").category(c5).stock(45).build(),
                        Product.builder().name("Donut Đường").price(18000.0).description("Vị ngọt truyền thống đơn giản mà lôi cuốn.").imageUrl("https://tse1.explicit.bing.net/th/id/OIP.lB-FMz75hB6tA4sAVAi9LAHaHa?r=0&cb=thfc1falcon&rs=1&pid=ImgDetMain&o=7&rm=3").category(c5).stock(60).build(),
                        Product.builder().name("Donut Matcha").price(24000.0).description("Lớp phủ trà xanh độc đáo, đậm chất Nhật Bản.").imageUrl("https://tse1.mm.bing.net/th/id/OIP.c69SowW1s_IpiiXZ7BvlPwHaIL?r=0&cb=thfc1falcon&rs=1&pid=ImgDetMain&o=7&rm=3").category(c5).stock(30).build(),
                        Product.builder().name("Donut Hạnh Nhân").price(26000.0).description("Giòn tan với lớp hạnh nhân lát phủ trên bề mặt.").imageUrl("https://th.bing.com/th/id/R.96f27c2204c50b3b273f99a115e3da8d?rik=mYtSHFhyCuQMRg&riu=http%3a%2f%2fbonpasbakery.com%2fimage%2fcache%2fdata%2f20160222%2f21.1-472x378f.jpg&ehk=PrjLdVjdnN%2f55JjvfByFqoZ%2bsR4EM6uBXZ4cin%2fP180%3d&risl=&pid=ImgRaw&r=0").category(c5).stock(25).build(),

                        // Bánh Mặn (c6)
                        Product.builder().name("Pate Chaud").price(25000.0).description("Bánh gối nhân thịt truyền thống, vỏ ngàn lớp giòn rụm.").imageUrl("https://tse1.mm.bing.net/th/id/OIP.SAjGOcmeYeVGH_9BlmESlQHaHa?r=0&cb=thfc1falcon&rs=1&pid=ImgDetMain&o=7&rm=3").category(c6).stock(40).build(),
                        Product.builder().name("Bánh Mì Xúc Xích").price(30000.0).description("Nhân xúc xích xông khói cao cấp kết hợp sốt đặc biệt.").imageUrl("https://thf.bing.com/th/id/OIP.AVwi0K_93Z_PZRL48XRGegHaEK?r=0&o=7&cb=thfc1falconrm=3&rs=1&pid=ImgDetMain&o=7&rm=3").category(c6).stock(30).build(),
                        Product.builder().name("Bánh Mì Chà Bông").price(28000.0).description("Chà bông gà cay nhẹ và sốt bơ trứng đặc trưng.").imageUrl("https://tse1.mm.bing.net/th/id/OIP.hHcCwjjV9ZyMJT_9qdKeEgHaFd?r=0&cb=thfc1falcon&rs=1&pid=ImgDetMain&o=7&rm=3").category(c6).stock(35).build(),
                        Product.builder().name("Bánh Gà").price(15000.0).description("Bánh chiên giòn rụm nhân gà xé phay đậm đà.").imageUrl("https://tse1.mm.bing.net/th/id/OIP.G8-jmI_7-8luoEqF8a771QHaEL?r=0&cb=thfc1falcon&rs=1&pid=ImgDetMain&o=7&rm=3").category(c6).stock(50).build(),
                        Product.builder().name("Bánh Pizza Mini").price(45000.0).description("Pizza cỡ nhỏ với phô mai kéo sợi và topping phong phú.").imageUrl("https://images.unsplash.com/photo-1513104890138-7c749659a591?w=800&q=80").category(c6).stock(20).build(),
                        Product.builder().name("Bánh Tart Trứng").price(20000.0).description("Vỏ giòn tan, nhân trứng custard béo ngậy tan chảy.").imageUrl("https://tse2.mm.bing.net/th/id/OIP.xhMEpt1vhUA4Af7h_pch1AHaE8?r=0&cb=thfc1falcon&rs=1&pid=ImgDetMain&o=7&rm=3").category(c6).stock(40).build()
                    );

                    List<Product> savedProducts = productRepository.saveAll(products);
                    log.info("Seeded {} products.", savedProducts.size());

                    // 5. Reviews
                    userRepository.findByUsername("user").ifPresent(user -> {
                        reviewRepository.save(Review.builder()
                                .content("Bánh tuyệt vời!")
                                .rating(5.0)
                                .createdAt(LocalDateTime.now())
                                .product(savedProducts.get(0))
                                .user(user)
                                .approved(true)
                                .build());
                        log.info("Seeded sample review.");
                    });

                    // 6. Sample Orders
                    if (orderRepository.count() == 0) {
                        userRepository.findByUsername("user").ifPresent(user -> {
                            Product p1 = savedProducts.get(0); // Bánh Kem Dâu Tây
                            Product p2 = savedProducts.get(5); // Bánh Sừng Bò

                            Order order = Order.builder()
                                    .customerName(user.getFullName())
                                    .shippingAddress(user.getAddress())
                                    .phoneNumber(user.getPhone())
                                    .email(user.getEmail())
                                    .orderDate(LocalDateTime.now().minusDays(1))
                                    .status(Order.OrderStatus.PENDING)
                                    .totalPrice(p1.getPrice() + (p2.getPrice() * 2))
                                    .user(user)
                                    .build();

                            OrderDetail d1 = OrderDetail.builder()
                                    .order(order)
                                    .product(p1)
                                    .quantity(1)
                                    .price(p1.getPrice())
                                    .build();

                            OrderDetail d2 = OrderDetail.builder()
                                    .order(order)
                                    .product(p2)
                                    .quantity(2)
                                    .price(p2.getPrice())
                                    .build();

                            order.setOrderDetails(Arrays.asList(d1, d2));
                            orderRepository.save(order);
                            log.info("Seeded sample order.");
                        });
                    }
                }
            }
            log.info("Data seeding completed successfully.");
        } catch (Exception e) {
            log.error("Error during data seeding: ", e);
        }
    }
}
