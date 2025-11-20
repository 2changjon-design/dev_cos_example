package com.example.study.lecture.cleancode.practice;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 주문 취소 책임이 한 메서드에 몰려 있는 예제.
 * - 검증/재고 복구/상태 변경/응답 구성/로그 기록이 모두 섞여 있다.
 * - 예외 발생 시 상태 전이를 일관성 있게 관리하지 못한다.
 *
 * TODO(LAB):
 *  1) cancel 메서드 내부 책임(검증/재고 복구/응답 생성/로그)을 메서드로 분리하세요.
 *  2) 취소 오케스트레이션 역할과 실무 로직을 별도 서비스로 나누세요.
 *  3) 실패 시 상태 기록 및 감사 로그 정책을 정리하세요.
 */
public class PurchaseCancellationPractice {

    private final PurchaseRepository purchaseRepository;
    private final PurchaseProductRepository purchaseProductRepository;
    private final ProductRepository productRepository;
    private final AuditTrail auditTrail;

    public PurchaseCancellationPractice(
            PurchaseRepository purchaseRepository,
            PurchaseProductRepository purchaseProductRepository,
            ProductRepository productRepository,
            AuditTrail auditTrail
    ) {
        this.purchaseRepository = purchaseRepository;
        this.purchaseProductRepository = purchaseProductRepository;
        this.productRepository = productRepository;
        this.auditTrail = auditTrail;
    }

    public PurchaseCancelResponse cancel(Long pId, Long uId) {
        if (pId == null || uId == null) {
            throw new IllegalArgumentException("need purchaseId and userId");
        }

        auditTrail.write("cancel start : " + pId);

        Purchase purchase = purchaseRepository.findById(pId)
                .orElseThrow(() -> new IllegalArgumentException("purchase not found"));

        if (!uId.equals(purchase.userId())) {
            throw new IllegalStateException("user mismatch");
        }

        if (!"PENDING".equals(purchase.status())) {
            purchase.setStatus("FAILED");
            purchaseRepository.save(purchase);
            throw new IllegalStateException("cannot cancel");
        }

        purchase.setStatus("CANCELING");
        purchaseRepository.save(purchase);

        List<PurchaseProduct> purchaseProducts = purchaseProductRepository.findByPurchaseId(pId);

        List<CancelledProduct> cancelledProducts = new ArrayList<>();
        for (PurchaseProduct purchaseProduct : purchaseProducts) {
            Product product = productRepository.findById(purchaseProduct.productId())
                    .orElseThrow(() -> new IllegalArgumentException("product not found"));
            int recovered = product.stock() + purchaseProduct.quantity();
            productRepository.save(product.withStock(recovered));
            cancelledProducts.add(new CancelledProduct(
                    product.id(),
                    product.name(),
                    purchaseProduct.quantity(),
                    purchaseProduct.price(),
                    purchaseProduct.price().multiply(BigDecimal.valueOf(purchaseProduct.quantity()))
            ));
        }

        PurchaseCancelResponse response = new PurchaseCancelResponse();
        response.purchaseId = purchase.id();
        response.status = "CANCELED";
        response.cancelledAt = LocalDateTime.now();
        response.products = cancelledProducts;
        response.message = "구매 취소가 완료되었습니다.";

        purchase.setStatus(response.status);
        purchaseRepository.save(purchase);
        auditTrail.write("cancel complete : " + pId);
        return response;
    }

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

    public interface AuditTrail {
        void write(String message);
    }

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

        public Long userId() {
            return userId;
        }

        public String status() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    public record PurchaseProduct(Long productId, int quantity, BigDecimal price) {
    }

    public record Product(Long id, String name, int stock) {
        public Product withStock(int value) {
            return new Product(id, name, value);
        }
    }

    public static class PurchaseCancelResponse {
        Long purchaseId;
        String status;
        LocalDateTime cancelledAt;
        List<CancelledProduct> products = new ArrayList<>();
        String message;
    }

    public record CancelledProduct(
            Long productId,
            String productName,
            int quantity,
            BigDecimal unitPrice,
            BigDecimal totalPrice
    ) {
    }
}
