package com.example.study.lecture.cleancode.spring.practice;

import com.example.study.PurchaseStatus;
import com.example.study.entity.Product;
import com.example.study.entity.Purchase;
import com.example.study.entity.User;
import com.example.study.repository.ProductRepository;
import com.example.study.repository.PurchaseRepository;
import com.example.study.repository.UserJpaRepository;
import jakarta.transaction.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

/**
 * Spring 연습용 예제 1: controller 한 곳에 모든 책임이 몰린 상태.
 * - 요청 DTO/응답 DTO가 존재하지 않는다.
 * - 서비스/도메인 레이어가 분리되어 있지 않아 테스트하기 어렵다.
 * - 예외/검증/트랜잭션/비즈니스 로직이 한 메서드에 뒤섞여 있다.
 *
 * TODO(SPRING-LAB):
 *  1) Controller- Service- Repository 계층을 분리하고, 트랜잭션을 적절한 레이어로 이동시키세요.
 *  2) 요청/응답 DTO를 만들어 엔티티 노출을 막으세요.
 *  3) 재고 차감/총액 계산 등의 도메인 로직을 별도 컴포넌트로 추출하세요.
 */
@RestController
@RequestMapping("/spring/practice/orders")
public class MonolithOrderControllerPractice {

    private final UserJpaRepository userRepository;
    private final ProductRepository productRepository;
    private final PurchaseRepository purchaseRepository;

    public MonolithOrderControllerPractice(
            UserJpaRepository userRepository,
            ProductRepository productRepository,
            PurchaseRepository purchaseRepository
    ) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.purchaseRepository = purchaseRepository;
    }

    @PostMapping
    @Transactional
    public Purchase createOrder(@RequestBody Map<String, Object> body) {
        Long userId = extractLong(body.get("userId"));
        Long productId = extractLong(body.get("productId"));
        Integer quantity = (Integer) body.getOrDefault("quantity", 1);

        if (userId == null || productId == null) {
            throw new IllegalArgumentException("userId/productId 필요");
        }
        if (quantity == null || quantity <= 0) {
            quantity = 1;
        }

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new IllegalStateException("user 없음");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalStateException("product 없음"));

        if (product.getStock() < quantity) {
            throw new IllegalStateException("재고 부족");
        }

        product.decreaseStock(quantity);
        productRepository.save(product);

        BigDecimal totalPrice = product.getPrice().multiply(BigDecimal.valueOf(quantity));
        Purchase purchase = Purchase.builder()
                .user(userOpt.get())
                .product(product)
                .quantity(quantity)
                .unitPrice(product.getPrice())
                .totalPrice(totalPrice)
                .status(PurchaseStatus.PENDING)
                .build();

        return purchaseRepository.save(purchase);
    }

    @GetMapping("/{id}")
    public Purchase getOrder(@PathVariable Long id) {
        return purchaseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("order 없음"));
    }

    private Long extractLong(Object value) {
        if (value instanceof Number number) {
            return number.longValue();
        }
        if (value instanceof String text && StringUtils.hasText(text)) {
            return Long.parseLong(text);
        }
        return null;
    }
}
