package com.example.study.lecture.cleancode.answer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * PurchaseCancellationPractice를 단일 책임 원칙에 맞게 분리한 서비스.
 * 영속 조회/검증/재고 복구/응답 조립을 각각의 메서드로 나눠,
 * 장애 원인 파악과 단위 테스트 범위를 명확히 했다.
 */
public class PurchaseCancelServiceAnswer {

    private final PurchaseRepository purchaseRepository;
    private final PurchaseProductRepository purchaseProductRepository;
    private final ProductRepository productRepository;

    public PurchaseCancelServiceAnswer(
            PurchaseRepository purchaseRepository,
            PurchaseProductRepository purchaseProductRepository,
            ProductRepository productRepository
    ) {
        this.purchaseRepository = purchaseRepository;
        this.purchaseProductRepository = purchaseProductRepository;
        this.productRepository = productRepository;
    }

    /**
     * 취소 플로우를 오케스트레이션하는 유일한 단일 진입점.
     * 각 단계 실패 시 즉시 예외를 던져 롤백이나 재시도를 단순화했다.
     */
    public PurchaseCancelResponse cancelPurchase(Long purchaseId, Long userId) {
        Purchase purchase = findPurchase(purchaseId);
        validateOwner(purchase, userId);
        ensurePendingStatus(purchase);

        List<PurchaseProduct> purchaseProducts = purchaseProductRepository.findByPurchaseId(purchaseId);
        List<CancelledProduct> cancelledProducts = restoreProducts(purchaseProducts);

        purchase.markCanceled();
        purchaseRepository.save(purchase);

        return buildResponse(purchase, cancelledProducts);
    }

    /**
     * 존재하지 않는 주문을 초기에 차단해 이후 로직에서 null 체크를 반복하지 않도록 한다.
     */
    private Purchase findPurchase(Long purchaseId) {
        return purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new IllegalArgumentException("구매 내역을 찾을 수 없습니다."));
    }

    /**
     * 사용자 검증을 별도로 분리하면 향후 운영자 권한 같은 예외 규칙을 추가하기 쉽다.
     */
    private void validateOwner(Purchase purchase, Long userId) {
        if (!purchase.isOwnedBy(userId)) {
            throw new IllegalStateException("다른 사용자의 주문은 취소할 수 없습니다.");
        }
    }

    /**
     * 상태 검증을 메서드로 만들면 상태 Enum 도입 시에도 이 부분만 수정하면 된다.
     */
    private void ensurePendingStatus(Purchase purchase) {
        if (!purchase.isPending()) {
            throw new IllegalStateException("이미 처리된 주문은 취소할 수 없습니다.");
        }
    }

    /**
     * 재고 복구와 응답 생성을 분리해 I/O와 계산 로직을 독립적으로 테스트할 수 있다.
     */
    private List<CancelledProduct> restoreProducts(List<PurchaseProduct> purchaseProducts) {
        List<CancelledProduct> cancelledProducts = new ArrayList<>();
        for (PurchaseProduct purchaseProduct : purchaseProducts) {
            Product product = productRepository.findById(purchaseProduct.productId())
                    .orElseThrow(() -> new IllegalArgumentException("상품 정보를 찾을 수 없습니다."));

            product.restore(purchaseProduct.quantity());
            productRepository.save(product);

            cancelledProducts.add(new CancelledProduct(
                    product.id(),
                    product.name(),
                    purchaseProduct.quantity(),
                    purchaseProduct.price()
            ));
        }
        return cancelledProducts;
    }

    /**
     * 응답 조립을 메서드로 빼두면 API 포맷 변경 시 비즈니스 로직을 건드리지 않아도 된다.
     */
    private PurchaseCancelResponse buildResponse(Purchase purchase, List<CancelledProduct> cancelledProducts) {
        PurchaseCancelResponse response = new PurchaseCancelResponse();
        response.purchaseId = purchase.id();
        response.status = purchase.status();
        response.cancelledAt = LocalDateTime.now();
        response.products = cancelledProducts;
        response.message = "구매 취소가 완료되었습니다.";
        return response;
    }

    /**
     * 예제 용도로 포트 인터페이스를 명시해, 테스트에서 더블로 대체할 수 있게 했다.
     */
    public interface PurchaseRepository {
        Optional<Purchase> findById(Long id);

        void save(Purchase purchase);
    }

    public interface PurchaseProductRepository {
        List<PurchaseProduct> findByPurchaseId(Long purchaseId);
    }

    public interface ProductRepository {
        Optional<Product> findById(Long productId);

        void save(Product product);
    }

    /**
     * 엔티티 내부 책임(상태 변경, 소유자 확인)을 메서드로 감싸 외부에서 직접 필드를 만지지 않게 한다.
     */
    public static class Purchase {
        private final Long id;
        private final Long userId;
        private String status;

        public Purchase(Long id, Long userId, String status) {
            this.id = id;
            this.userId = userId;
            this.status = status;
        }

        public Long id() {
            return id;
        }

        public String status() {
            return status;
        }

        public boolean isPending() {
            return "PENDING".equals(status);
        }

        public boolean isOwnedBy(Long targetUserId) {
            return userId.equals(targetUserId);
        }

        public void markCanceled() {
            this.status = "CANCELED";
        }
    }

    /**
     * 주문-상품 사이 조인 테이블 역할만 하므로 불변 record로 유지한다.
     */
    public record PurchaseProduct(Long productId, int quantity, BigDecimal price) {
    }

    /**
     * Product 역시 재고 회복 책임만 노출해 도메인 규칙 변경을 한 곳에서 통제한다.
     */
    public static class Product {
        private final Long id;
        private final String name;
        private int stock;

        public Product(Long id, String name, int stock) {
            this.id = id;
            this.name = name;
            this.stock = stock;
        }

        public Long id() {
            return id;
        }

        public String name() {
            return name;
        }

        public void restore(int quantity) {
            this.stock += quantity;
        }
    }

    /**
     * API 응답 DTO. 필드가 많아 빌더 대신 명시적 세터를 사용해 예제 가독성을 높였다.
     */
    public static class PurchaseCancelResponse {
        Long purchaseId;
        String status;
        LocalDateTime cancelledAt;
        List<CancelledProduct> products = new ArrayList<>();
        String message;

        public Long getPurchaseId() {
            return purchaseId;
        }

        public String getStatus() {
            return status;
        }

        public LocalDateTime getCancelledAt() {
            return cancelledAt;
        }

        public List<CancelledProduct> getProducts() {
            return products;
        }

        public String getMessage() {
            return message;
        }
    }

    /**
     * 응답 내 단위 상품 표현을 불변 record로 고정해 UI 계층에서도 안전하게 재사용할 수 있다.
     */
    public record CancelledProduct(
            Long productId,
            String productName,
            int quantity,
            BigDecimal unitPrice
    ) {
        public BigDecimal totalPrice() {
            return unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }
}
