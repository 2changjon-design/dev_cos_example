package com.example.study.lecture.refactor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 학습 섹션 4: 구매 도메인 전체 흐름을 리팩토링해보는 실습 템플릿.
 * TODO: 아래 Practice 클래스들을 사용해 클래스 분리, 풍부한 도메인 모델링, 책임 위임을 직접 구현해보자.
 * 정답 구현은 RefactSolutionSections에만 포함되어 있다.
 */
public final class Section4RefactExamples {

    private Section4RefactExamples() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * SECTION 4 - BEFORE: 하나의 서비스가 모든 책임을 지는 버전.
     */
    public static class PurchaseRefactServiceBefore {
        private final PurchaseRefactRepository purchaseRefactRepository;
        private final PurchaseItemRefactRepository purchaseItemRefactRepository;
        private final ProductRefactRepository productRefactRepository;
        private final UserRefactRepository userRefactRepository;

        public PurchaseRefactServiceBefore(PurchaseRefactRepository purchaseRefactRepository,
                                           PurchaseItemRefactRepository purchaseItemRefactRepository,
                                           ProductRefactRepository productRefactRepository,
                                           UserRefactRepository userRefactRepository) {
            this.purchaseRefactRepository = purchaseRefactRepository;
            this.purchaseItemRefactRepository = purchaseItemRefactRepository;
            this.productRefactRepository = productRefactRepository;
            this.userRefactRepository = userRefactRepository;
        }

        public PurchaseRefact purchase(PurchaseRefactRequest request) {
            UserRefact user = userRefactRepository.findByIdOrThrow(request.userId());
            PurchaseRefact purchase = purchaseRefactRepository.save(PurchaseRefact.start(user));

            BigDecimal totalPrice = BigDecimal.ZERO;
            List<PurchaseItemRefact> items = new ArrayList<>();
            for (PurchaseItemRefactRequest itemRequest : request.items()) {
                ProductRefact product = productRefactRepository.findByIdOrThrow(itemRequest.productId());
                if (itemRequest.quantity() > product.getStock()) {
                    throw new IllegalStateException("재고 부족");
                }
                product.reduceStock(itemRequest.quantity());
                PurchaseItemRefact item = PurchaseItemRefact.create(product, purchase, itemRequest.quantity());
                items.add(item);
                totalPrice = totalPrice.add(item.lineTotal());
            }

            purchase.setItems(items);
            purchase.setTotalPrice(totalPrice);
            purchase.setStatus(PurchaseRefactFlowStatus.COMPLETED);
            purchaseItemRefactRepository.saveAll(items);
            return purchase;
        }
    }

    /**
     * SECTION 4 - AFTER: 실습 결과를 작성할 비어 있는 클래스들.
     * TODO: 서비스 분리와 책임 위임을 아래 클래스에 직접 구현해보자.
     */
    public static class PurchaseRefactServiceAfter {
        // 구현은 학습자가 직접 작성합니다.
    }

    public static class PurchaseProcessRefactServiceAfter {
        // 구현은 학습자가 직접 작성합니다.
    }

    // ===== 연습용 엔티티/밸류 =====

    /**
     * SECTION 4 - ANEMIC ENTITY: 현재는 setter 중심의 빈약한 모델이다.
     * TODO: 상태 전이 메서드(complete/cancel), 금액 재계산 로직 등을 이곳으로 옮겨보자.
     */
    public static class PurchaseRefact {
        private final UserRefact user;
        private List<PurchaseItemRefact> items = new ArrayList<>();
        private PurchaseRefactFlowStatus status = PurchaseRefactFlowStatus.PENDING;
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

        public void setStatus(PurchaseRefactFlowStatus status) {
            this.status = status;
        }

        public void setTotalPrice(BigDecimal totalPrice) {
            this.totalPrice = totalPrice;
        }

        public UserRefact getUser() {
            return user;
        }

        public List<PurchaseItemRefact> getItems() {
            return items;
        }

        public PurchaseRefactFlowStatus getStatus() {
            return status;
        }

        public BigDecimal getTotalPrice() {
            return totalPrice;
        }
    }

    public static class PurchaseItemRefact {
        private final ProductRefact product;
        private final PurchaseRefact purchase;
        private final int quantity;

        private PurchaseItemRefact(ProductRefact product, PurchaseRefact purchase, int quantity) {
            this.product = product;
            this.purchase = purchase;
            this.quantity = quantity;
        }

        public static PurchaseItemRefact create(ProductRefact product, PurchaseRefact purchase, int quantity) {
            return new PurchaseItemRefact(product, purchase, quantity);
        }

        public BigDecimal lineTotal() {
            return product.getPrice().multiply(BigDecimal.valueOf(quantity));
        }

        public ProductRefact getProduct() {
            return product;
        }

        public int getQuantity() {
            return quantity;
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

        public Long getId() {
            return id;
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

    // ===== DTO & Repository Contracts =====

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

    public enum PurchaseRefactFlowStatus {
        PENDING, COMPLETED, CANCELED
    }
}
