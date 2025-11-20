package com.example.study.lecture.refactor;

import java.math.BigDecimal;

/**
 * 학습 섹션 3: IntelliJ IDEA 리팩토링 도구 사용법을 코드와 TODO 주석으로 안내한다.
 */
public final class Section3RefactExamples {

    private Section3RefactExamples() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * SECTION 3 - BEFORE: IDE Extract Method 적용 전 코드.
     */
    public static class ShippingCostRefactBefore {

        public void process(PurchaseRefactForIDE purchase) {
            double shippingCost = purchase.price().doubleValue() * 0.1;
            if (purchase.weight() > 10) {
                shippingCost += 5.0;
            }
            purchase.setShippingCost(BigDecimal.valueOf(shippingCost));
        }
    }

    /**
     * SECTION 3 - AFTER (Placeholder): Extract Method 결과를 직접 작성해보자.
     */
    public static class ShippingCostRefactAfter {
        // TODO: calculateAndSetShippingCost 메서드를 만들어보자.
    }

    /**
     * SECTION 3 - Rename BEFORE.
     */
    public static class RenameShortcutRefactBefore {

        public int countPurchases(int q) {
            return Math.max(q, 0);
        }
    }

    /**
     * SECTION 3 - Rename AFTER (Placeholder).
     */
    public static class RenameShortcutRefactAfter {
        // TODO: 변수명을 remainingPurchases로 변경한 버전을 구현해보자.
    }

    public static class PurchaseRefactForIDE {
        private final BigDecimal price;
        private final double weight;
        private BigDecimal shippingCost = BigDecimal.ZERO;

        public PurchaseRefactForIDE(BigDecimal price, double weight) {
            this.price = price;
            this.weight = weight;
        }

        public BigDecimal price() {
            return price;
        }

        public double weight() {
            return weight;
        }

        public void setShippingCost(BigDecimal shippingCost) {
            this.shippingCost = shippingCost;
        }
    }
}
