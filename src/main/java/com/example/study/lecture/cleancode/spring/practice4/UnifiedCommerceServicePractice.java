package com.example.study.lecture.cleancode.spring.practice4;

import com.example.study.PurchaseStatus;
import com.example.study.entity.Product;
import com.example.study.entity.Purchase;
import com.example.study.entity.User;
import com.example.study.repository.ProductRepository;
import com.example.study.repository.PurchaseRepository;
import com.example.study.repository.UserJpaRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 도메인별 관심사가 전혀 분리되지 않은 예제 서비스.
 *
 * - 사용자 생성/업데이트
 * - 상품 재고 차감
 * - 주문 생성
 * - 외부 알림 호출
 *
 * 모든 로직이 한 메서드에 섞여 있어 테스트/확장/예외 처리가 어렵다.
 *
 * TODO(DOMAIN-MIXED):
 *  1) User / Inventory / Order / Notification 책임을 별도 컴포넌트로 분리하세요.
 *  2) Map 기반 요청/응답 대신 요청 DTO와 응답 DTO를 정의하세요.
 *  3) 트랜잭션 경계는 도메인 서비스에 두고, 외부 호출은 분리된 게이트웨이로 이동시키세요.
 */
@Service
public class UnifiedCommerceServicePractice {

    private final UserJpaRepository userRepository;
    private final ProductRepository productRepository;
    private final PurchaseRepository purchaseRepository;

    public UnifiedCommerceServicePractice(
            UserJpaRepository userRepository,
            ProductRepository productRepository,
            PurchaseRepository purchaseRepository
    ) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.purchaseRepository = purchaseRepository;
    }

    @Transactional
    public Map<String, Object> handleOrder(Map<String, Object> payload) {
        String email = (String) payload.get("email");
        String name = (String) payload.get("name");
        if (!StringUtils.hasText(email)) {
            throw new IllegalArgumentException("email required");
        }
        if (!StringUtils.hasText(name)) {
            name = "Guest";
        }
        final String finalName = name;

        Optional<User> existing = userRepository.findAll().stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(email))
                .findFirst();

        User user = existing.orElseGet(() -> userRepository.save(
                User.builder()
                        .name(finalName)
                        .email(email)
                        .passwordHash("temp-" + email)
                        .build()
        ));

        if (!finalName.equals(user.getName())) {
            user.updateProfile(finalName, email);
            userRepository.save(user);
        }

        Long productId = Long.valueOf(payload.get("productId").toString());
        Integer quantity = Integer.valueOf(payload.getOrDefault("quantity", 1).toString());

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("product not found"));

        if (product.getStock() < quantity) {
            throw new IllegalStateException("out of stock");
        }

        product.decreaseStock(quantity);
        productRepository.save(product);

        Purchase purchase = Purchase.builder()
                .user(user)
                .product(product)
                .quantity(quantity)
                .unitPrice(product.getPrice())
                .totalPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity)))
                .status(PurchaseStatus.COMPLETED)
                .build();

        purchaseRepository.save(purchase);

        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> notificationPayload = Map.of(
                "userEmail", user.getEmail(),
                "productId", product.getId(),
                "purchaseId", purchase.getId()
        );
        restTemplate.postForEntity("https://notification.example.com/api/send", notificationPayload, Void.class);

        Map<String, Object> response = new HashMap<>();
        response.put("purchaseId", purchase.getId());
        response.put("user", Map.of("id", user.getId(), "email", user.getEmail()));
        response.put("product", Map.of("id", product.getId(), "name", product.getName()));
        response.put("quantity", quantity);
        response.put("status", purchase.getStatus().name());
        return response;
    }
}
