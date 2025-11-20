package com.example.study.lecture.refactor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Section 2 실습 정답.
 */
public final class Section2RefactSolution {

    private Section2RefactSolution() {
        throw new IllegalStateException("Utility class");
    }

    public static class PurchaseRefactOrchestratorAfter {

        public PurchaseRefact purchase(PurchaseRefactRequest request) {
            PurchaseRefact purchase = createPurchase(request.user());
            List<PurchaseItemRefact> items = createAndProcessItems(purchase, request.items());
            finalizePurchase(purchase, items);
            return purchase;
        }

        private PurchaseRefact createPurchase(UserRefact user) {
            return PurchaseRefact.create(user);
        }

        private List<PurchaseItemRefact> createAndProcessItems(
                PurchaseRefact purchase,
                List<PurchaseItemRefactRequest> itemRequests
        ) {
            List<PurchaseItemRefact> items = new ArrayList<>();
            for (PurchaseItemRefactRequest itemRequest : itemRequests) {
                validateStock(itemRequest.product(), itemRequest.quantity());
                itemRequest.product().reduceStock(itemRequest.quantity());
                items.add(new PurchaseItemRefact(itemRequest.product(), purchase, itemRequest.quantity()));
            }
            return items;
        }

        private void validateStock(ProductRefact product, int quantity) {
            if (quantity > product.getStock()) {
                throw new IllegalStateException("재고 부족");
            }
        }

        private void finalizePurchase(PurchaseRefact purchase, List<PurchaseItemRefact> items) {
            purchase.attachItems(items);
            purchase.setTotalPrice(calculateTotalPrice(items));
        }

        private BigDecimal calculateTotalPrice(List<PurchaseItemRefact> items) {
            return items.stream()
                    .map(PurchaseItemRefact::linePrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
    }

    public static class UserRefactRenameAfter {
        private java.util.Date registrationDate;

        public void sendLoginConfirmationEmail() {
            // 명확한 메서드 이름으로 행동 설명
        }
    }

    // ===== Section2 Supporting Types =====

    public record PurchaseRefactRequest(UserRefact user, List<PurchaseItemRefactRequest> items) {
    }

    public record PurchaseItemRefactRequest(ProductRefact product, int quantity) {
    }

    public static class UserRefact {
        private final Long id;
        private final String email;

        public UserRefact(Long id, String email) {
            this.id = id;
            this.email = email;
        }

        public Long getId() {
            return id;
        }

        public String getEmail() {
            return email;
        }
    }

    public static class ProductRefact {
        private final String name;
        private final BigDecimal price;
        private int stock;

        public ProductRefact(String name, BigDecimal price, int stock) {
            this.name = name;
            this.price = price;
            this.stock = stock;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public int getStock() {
            return stock;
        }

        public void reduceStock(int quantity) {
            this.stock -= quantity;
        }
    }

    public static class PurchaseRefact {
        private final UserRefact user;
        private List<PurchaseItemRefact> items = new ArrayList<>();
        private BigDecimal totalPrice = BigDecimal.ZERO;

        private PurchaseRefact(UserRefact user) {
            this.user = user;
        }

        public static PurchaseRefact create(UserRefact user) {
            return new PurchaseRefact(user);
        }

        public void attachItems(List<PurchaseItemRefact> items) {
            this.items = items;
        }

        public void setTotalPrice(BigDecimal totalPrice) {
            this.totalPrice = totalPrice;
        }
    }

    public static class PurchaseItemRefact {
        private final ProductRefact product;
        private final PurchaseRefact purchase;
        private final int quantity;

        public PurchaseItemRefact(ProductRefact product, PurchaseRefact purchase, int quantity) {
            this.product = product;
            this.purchase = purchase;
            this.quantity = quantity;
        }

        public BigDecimal linePrice() {
            return product.getPrice().multiply(BigDecimal.valueOf(quantity));
        }
    }
}
