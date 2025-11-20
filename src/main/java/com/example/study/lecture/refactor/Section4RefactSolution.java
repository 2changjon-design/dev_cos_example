package com.example.study.lecture.refactor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Section 4 실습 정답: 서비스 분리와 풍부한 도메인 모델.
 */
public final class Section4RefactSolution {

    private Section4RefactSolution() {
        throw new IllegalStateException("Utility class");
    }

    public static class PurchaseRefactServiceAfter {
        private final PurchaseProcessRefactServiceAfter processService;
        private final UserRefactRepository userRefactRepository;

        public PurchaseRefactServiceAfter(PurchaseProcessRefactServiceAfter processService,
                                          UserRefactRepository userRefactRepository) {
            this.processService = processService;
            this.userRefactRepository = userRefactRepository;
        }

        public PurchaseRefact purchase(PurchaseRefactRequest request) {
            UserRefact user = userRefactRepository.findByIdOrThrow(request.userId());
            return processService.process(user, request.items());
        }
    }

    public static class PurchaseProcessRefactServiceAfter {
        private final PurchaseRefactRepository purchaseRefactRepository;
        private final PurchaseItemRefactRepository purchaseItemRefactRepository;
        private final ProductRefactRepository productRefactRepository;

        public PurchaseProcessRefactServiceAfter(PurchaseRefactRepository purchaseRefactRepository,
                                                 PurchaseItemRefactRepository purchaseItemRefactRepository,
                                                 ProductRefactRepository productRefactRepository) {
            this.purchaseRefactRepository = purchaseRefactRepository;
            this.purchaseItemRefactRepository = purchaseItemRefactRepository;
            this.productRefactRepository = productRefactRepository;
        }

        public PurchaseRefact process(UserRefact user, List<PurchaseItemRefactRequest> requests) {
            PurchaseRefact purchase = purchaseRefactRepository.save(PurchaseRefact.start(user));
            List<PurchaseItemRefact> items = new ArrayList<>();
            for (PurchaseItemRefactRequest request : requests) {
                ProductRefact product = productRefactRepository.findByIdOrThrow(request.productId());
                product.ensureStock(request.quantity());
                product.reduceStock(request.quantity());
                items.add(PurchaseItemRefact.create(product, request.quantity()));
            }
            purchase.attachItems(items);
            purchase.complete();
            purchaseItemRefactRepository.saveAll(items);
            return purchase;
        }
    }

    // ===== Supporting Domain =====
    public static class PurchaseRefact {
        private final UserRefact user;
        private List<PurchaseItemRefact> items = new ArrayList<>();
        private PurchaseRefactStatus status = PurchaseRefactStatus.PENDING;
        private BigDecimal totalPrice = BigDecimal.ZERO;

        private PurchaseRefact(UserRefact user) {
            this.user = user;
        }

        public static PurchaseRefact start(UserRefact user) {
            return new PurchaseRefact(user);
        }

        public void setItems(List<PurchaseItemRefact> items) {
            this.items = items;
        }

        public void setStatus(PurchaseRefactStatus status) {
            this.status = status;
        }

        public void setTotalPrice(BigDecimal totalPrice) {
            this.totalPrice = totalPrice;
        }

        public void attachItems(List<PurchaseItemRefact> newItems) {
            this.items = new ArrayList<>(newItems);
            recalculateTotalPrice();
        }

        public void complete() {
            if (items.isEmpty()) {
                throw new IllegalStateException("아이템이 필요합니다.");
            }
            this.status = PurchaseRefactStatus.COMPLETED;
        }

        private void recalculateTotalPrice() {
            this.totalPrice = items.stream()
                    .map(PurchaseItemRefact::lineTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
    }

    public static class PurchaseItemRefact {
        private final ProductRefact product;
        private final int quantity;

        private PurchaseItemRefact(ProductRefact product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }

        public static PurchaseItemRefact create(ProductRefact product, int quantity) {
            return new PurchaseItemRefact(product, quantity);
        }

        public BigDecimal lineTotal() {
            return product.getPrice().multiply(BigDecimal.valueOf(quantity));
        }
    }

    public static class ProductRefact {
        private final Long id;
        private final BigDecimal price;
        private int stock;

        public ProductRefact(Long id, BigDecimal price, int stock) {
            this.id = id;
            this.price = price;
            this.stock = stock;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public int getStock() {
            return stock;
        }

        public void ensureStock(int quantity) {
            if (quantity > stock) {
                throw new IllegalStateException("재고 부족");
            }
        }

        public void reduceStock(int quantity) {
            this.stock -= quantity;
        }
    }

    public static class UserRefact {
        private final Long id;
        private final String name;

        public UserRefact(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    public record PurchaseRefactRequest(Long userId, List<PurchaseItemRefactRequest> items) {
    }

    public record PurchaseItemRefactRequest(Long productId, int quantity) {
    }

    public interface UserRefactRepository {
        UserRefact findByIdOrThrow(Long id);
    }

    public interface ProductRefactRepository {
        ProductRefact findByIdOrThrow(Long id);
    }

    public interface PurchaseRefactRepository {
        PurchaseRefact save(PurchaseRefact purchase);
    }

    public interface PurchaseItemRefactRepository {
        void saveAll(List<PurchaseItemRefact> items);
    }

    public enum PurchaseRefactStatus {
        PENDING, COMPLETED, CANCELED
    }
}
