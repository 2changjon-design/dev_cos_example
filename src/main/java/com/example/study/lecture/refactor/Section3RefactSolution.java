package com.example.study.lecture.refactor;

import java.math.BigDecimal;

/**
 * Section 3 실습 정답: IDE 리팩토링 도구 활용 예시.
 */
public final class Section3RefactSolution {

    private Section3RefactSolution() {
        throw new IllegalStateException("Utility class");
    }

    public static class ShippingCostRefactAfter {

        public void process(PurchaseIDEAnswer purchase) {
            calculateAndSetShippingCost(purchase);
        }

        private void calculateAndSetShippingCost(PurchaseIDEAnswer purchase) {
            double shippingCost = purchase.price().doubleValue() * 0.1;
            if (purchase.weight() > 10) {
                shippingCost += 5.0;
            }
            purchase.setShippingCost(BigDecimal.valueOf(shippingCost));
        }
    }

    public static class RenameShortcutRefactAfter {

        public int countPurchases(int remainingPurchases) {
            return Math.max(remainingPurchases, 0);
        }
    }

    public static class PurchaseIDEAnswer {
        private final BigDecimal price;
        private final double weight;
        private BigDecimal shippingCost = BigDecimal.ZERO;

        public PurchaseIDEAnswer(BigDecimal price, double weight) {
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
