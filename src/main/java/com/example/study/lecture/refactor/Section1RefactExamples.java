package com.example.study.lecture.refactor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * SECTION 1 실습 예제
 *
 * 구성:
 * 1) 메서드 추출 (Method Extraction)
 * 2) 단일 책임 원칙 적용 (Single Responsibility Principle)
 * 3) 명확한 이름 짓기 (Rename)
 *
 */
public final class Section1RefactExamples {

    private Section1RefactExamples() {
        throw new IllegalStateException("Utility class");
    }

    // ============================================================
    // 1) 메서드 추출 - 배송비 계산
    // ============================================================

    /**
     * 동일한 배송비 계산 로직이 두 메서드에 복제되어 있다.
     * 목표: 배송비 계산을 별도 메서드로 추출해 중복을 제거한다.
     */
    public static class ShippingCostServiceBefore {

        public void processStandardOrder(ShippingOrder order) {
            double shippingCost = order.getPrice().doubleValue() * 0.12;
            if (order.getWeight() > 15) {
                shippingCost += 7.5;
            }
            order.setShippingCost(BigDecimal.valueOf(shippingCost));
        }

        public void processExpressOrder(ShippingOrder order) {
            double shippingCost = order.getPrice().doubleValue() * 0.12;
            if (order.getWeight() > 15) {
                shippingCost += 7.5;
            }
            shippingCost += 5.0; // 익스프레스 추가비용
            order.setShippingCost(BigDecimal.valueOf(shippingCost));
        }
    }

    /**
     * 실습 후 작성할 클래스 (해답은 Section1RefactSolution 참고).
     */
    public static class ShippingCostServiceAfter {
        // TODO: 메서드 추출 결과를 직접 구현해보세요.
    }

    public static class ShippingOrder {
        private final BigDecimal price;
        private final double weight;
        private final LocalDate requestedDate;
        private BigDecimal shippingCost = BigDecimal.ZERO;

        public ShippingOrder(BigDecimal price, double weight, LocalDate requestedDate) {
            this.price = price;
            this.weight = weight;
            this.requestedDate = requestedDate;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public double getWeight() {
            return weight;
        }

        public LocalDate getRequestedDate() {
            return requestedDate;
        }

        public void setShippingCost(BigDecimal shippingCost) {
            this.shippingCost = shippingCost;
        }

        public BigDecimal getShippingCost() {
            return shippingCost;
        }
    }

    // ============================================================
    // 2) 단일 책임 원칙 - 주문 처리
    // ============================================================

    /**
     * 하나의 메서드가 검증/할인/결제 로그/알림까지 모두 담당한다.
     * 목표: 책임을 축소시키고 필요한 경우 협력 클래스로 분리한다.
     */
    public static class OrderProcessorBefore {
        private final PaymentGateway paymentGateway;
        private final NotificationSender notificationSender;

        public OrderProcessorBefore(PaymentGateway paymentGateway,
                                    NotificationSender notificationSender) {
            this.paymentGateway = paymentGateway;
            this.notificationSender = notificationSender;
        }

        public void process(PurchaseOrder order) {
            if (order.totalPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("결제 금액이 0 이하입니다.");
            }

            if (!order.email().contains("@")) {
                throw new IllegalArgumentException("잘못된 이메일입니다.");
            }

            BigDecimal discounted = order.totalPrice();
            if (order.totalPrice().compareTo(BigDecimal.valueOf(300_000)) > 0) {
                discounted = discounted.multiply(BigDecimal.valueOf(0.9));
            }

            paymentGateway.pay(order.id(), discounted);
            System.out.printf("[PAYMENT] order=%s amount=%s%n", order.id(), discounted);
            notificationSender.send(order.email(), "[주문 완료] 결제가 완료되었습니다.");
        }
    }

    public static class OrderProcessorAfter {
        // TODO: 검증/할인/결제/알림을 역할별로 나눈 버전을 직접 작성해보세요.
    }

    public record PurchaseOrder(Long id, BigDecimal totalPrice, String email) {
    }

    public interface PaymentGateway {
        void pay(Long orderId, BigDecimal amount);
    }

    public interface NotificationSender {
        void send(String email, String message);
    }

    // ============================================================
    // 3) 이름 짓기 - 가독성 향상
    // ============================================================

    /**
     * 의도가 드러나지 않는 이름을 가진 예제.
     * 목표: 변수/메서드 이름을 명확하게 바꾸고, 의미 있는 도메인 용어를 사용한다.
     */
    public static class UserStatsBefore {
        private int c1;
        private int c2;

        public void calc(int n) {
            c1 += n;
            if (n > 10) {
                c2++;
            }
        }

        public int getC1() {
            return c1;
        }

        public int getC2() {
            return c2;
        }
    }

    public static class UserStatsAfter {
        // TODO: 의미 있는 필드/메서드 이름으로 다시 작성해보세요.
    }
}
