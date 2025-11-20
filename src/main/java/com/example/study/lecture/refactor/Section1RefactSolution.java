package com.example.study.lecture.refactor;

import java.math.BigDecimal;

/**
 * Section 1 실습 정답 코드.
 */
public final class Section1RefactSolution {

    private Section1RefactSolution() {
        throw new IllegalStateException("Utility class");
    }

    // ============================================================
    // 1) 메서드 추출 - 배송비 계산
    // ============================================================
    public static class ShippingCostServiceAfter {

        public void processStandardOrder(Section1RefactExamples.ShippingOrder order) {
            BigDecimal cost = calculateBaseShipping(order);
            order.setShippingCost(cost);
        }

        public void processExpressOrder(Section1RefactExamples.ShippingOrder order) {
            BigDecimal cost = calculateBaseShipping(order).add(BigDecimal.valueOf(5.0));
            order.setShippingCost(cost);
        }

        private BigDecimal calculateBaseShipping(Section1RefactExamples.ShippingOrder order) {
            double shippingCost = order.getPrice().doubleValue() * 0.12;
            if (order.getWeight() > 15) {
                shippingCost += 7.5;
            }
            return BigDecimal.valueOf(shippingCost);
        }
    }

    // ============================================================
    // 2) 단일 책임 원칙 - 주문 처리
    // ============================================================
    public static class OrderProcessorAfter {
        private final OrderValidator validator;
        private final DiscountPolicy discountPolicy;
        private final PaymentService paymentService;
        private final Section1RefactExamples.NotificationSender notificationSender;

        public OrderProcessorAfter(OrderValidator validator,
                                   DiscountPolicy discountPolicy,
                                   PaymentService paymentService,
                                   Section1RefactExamples.NotificationSender notificationSender) {
            this.validator = validator;
            this.discountPolicy = discountPolicy;
            this.paymentService = paymentService;
            this.notificationSender = notificationSender;
        }

        public void process(Section1RefactExamples.PurchaseOrder order) {
            validator.validate(order);
            BigDecimal payAmount = discountPolicy.applyDiscount(order.totalPrice());
            paymentService.pay(order.id(), payAmount);
            notificationSender.send(order.email(), "[주문 완료] 결제가 완료되었습니다.");
        }
    }

    public static class OrderValidator {
        public void validate(Section1RefactExamples.PurchaseOrder order) {
            if (order.totalPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("결제 금액이 0 이하입니다.");
            }
            if (order.email() == null || !order.email().contains("@")) {
                throw new IllegalArgumentException("잘못된 이메일입니다.");
            }
        }
    }

    public static class DiscountPolicy {
        public BigDecimal applyDiscount(BigDecimal totalPrice) {
            if (totalPrice.compareTo(BigDecimal.valueOf(300_000)) > 0) {
                return totalPrice.multiply(BigDecimal.valueOf(0.9));
            }
            return totalPrice;
        }
    }

    public static class PaymentService {
        private final Section1RefactExamples.PaymentGateway paymentGateway;

        public PaymentService(Section1RefactExamples.PaymentGateway paymentGateway) {
            this.paymentGateway = paymentGateway;
        }

        public void pay(Long orderId, BigDecimal amount) {
            paymentGateway.pay(orderId, amount);
            System.out.printf("[PAYMENT] order=%s amount=%s%n", orderId, amount);
        }
    }

    // ============================================================
    // 3) 이름 짓기 - 가독성 향상
    // ============================================================
    public static class UserStatsAfter {
        private int totalLoginCount;
        private int highValueLoginCount;

        public void recordLogin(int sessionDurationMinutes) {
            totalLoginCount += sessionDurationMinutes;
            if (sessionDurationMinutes > 10) {
                highValueLoginCount++;
            }
        }

        public int getTotalLoginDuration() {
            return totalLoginCount;
        }

        public int getHighValueLoginCount() {
            return highValueLoginCount;
        }
    }
}
