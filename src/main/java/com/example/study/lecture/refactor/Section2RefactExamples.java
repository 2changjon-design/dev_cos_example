package com.example.study.lecture.refactor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 학습 섹션 2: 메서드 추출, 책임 분리, Rename 리팩토링을 실습할 수 있는 코드 샘플.
 */
public final class Section2RefactExamples {

    private Section2RefactExamples() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * SECTION 2 - BEFORE: Long Method + 다중 책임이 한 곳에 뭉쳐 있는 모습.
     */
    public static class PurchaseRefactOrchestratorBefore {

        public PurchaseRefact purchase(PurchaseRefactRequest request) {
            // SECTION 2 - STEP 1: 구매 생성
            PurchaseRefact purchase = PurchaseRefact.create(request.user());

            BigDecimal totalPrice = BigDecimal.ZERO;
            List<PurchaseItemRefact> purchaseItems = new ArrayList<>();

            // SECTION 2 - STEP 2: 재고 확인 + 항목 생성 + 가격 계산이 모두 한 곳에 존재
            for (PurchaseItemRefactRequest itemRequest : request.items()) {
                ProductRefact product = itemRequest.product();
                if (itemRequest.quantity() > product.getStock()) {
                    throw new IllegalStateException("SECTION 2 - THEORY: 재고 부족 예외");
                }
                product.reduceStock(itemRequest.quantity());
                PurchaseItemRefact item = new PurchaseItemRefact(product, purchase, itemRequest.quantity());
                purchaseItems.add(item);
                totalPrice = totalPrice.add(item.linePrice());
            }

            // SECTION 2 - STEP 3: 총액 계산 + 구매 업데이트까지 이어지는 혼잡한 로직
            purchase.attachItems(purchaseItems);
            purchase.setTotalPrice(totalPrice);
            return purchase;
        }
    }

    /**
     * SECTION 2 - AFTER: 실습 결과를 채울 빈 클래스.
     * TODO: Extract Method를 적용한 버전을 여기 구현해보자.
     */
    public static class PurchaseRefactOrchestratorAfter {
        // 구현은 학습자가 직접 작성합니다.
    }

    /**
     * SECTION 2 - RENAME BEFORE: 의도가 드러나지 않는 이름.
     */
    public static class UserRefactRenameBefore {
        private java.util.Date d1;

        public void proc() {
            // ...
        }
    }

    /**
     * SECTION 2 - RENAME AFTER: 실습 후 Shift+F6 결과를 재현해보는 placeholder.
     */
    public static class UserRefactRenameAfter {
        // TODO: registrationDate 같은 의미 있는 이름을 직접 작성해보자.
    }


    // ===== 학습용 단순 도메인 모델 =====

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
