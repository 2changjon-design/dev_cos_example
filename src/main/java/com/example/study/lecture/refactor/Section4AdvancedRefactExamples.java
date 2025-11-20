package com.example.study.lecture.refactor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * SECTION 4 Advanced: 서비스 분리 + 풍부한 도메인 모델을 한 단계 더 확장한 예제.
 */
public final class Section4AdvancedRefactExamples {

    private Section4AdvancedRefactExamples() {
        throw new IllegalStateException("Utility class");
    }

    // ============================================================
    // ADVANCED EXAMPLE 1: 결제 + 포인트 + 알림까지 포함된 주문 흐름
    // ============================================================

    /**
     * SECTION 4 ADVANCED - BEFORE:
     * - 결제 처리, 포인트 적립, 알림 발송을 모두 OrderWorkflowRefactBefore 가 담당한다.
     * - 서비스 분리, 도메인 메서드 도입 연습용.
     */
    public static class OrderWorkflowRefactBefore {
        private final OrderRepository orderRepository;
        private final PaymentGateway paymentGateway;
        private final PointLedger pointLedger;
        private final NotificationSender notificationSender;

        public OrderWorkflowRefactBefore(OrderRepository orderRepository,
                                         PaymentGateway paymentGateway,
                                         PointLedger pointLedger,
                                         NotificationSender notificationSender) {
            this.orderRepository = orderRepository;
            this.paymentGateway = paymentGateway;
            this.pointLedger = pointLedger;
            this.notificationSender = notificationSender;
        }

        public Order process(Order order, PaymentRequest paymentRequest) {
            if (order.getStatus() != OrderStatus.PENDING) {
                throw new IllegalStateException("주문이 이미 처리되었습니다.");
            }

            // 1. 결제 처리
            PaymentReceipt receipt = paymentGateway.pay(paymentRequest);
            order.setPaymentId(receipt.paymentId());
            order.setStatus(OrderStatus.PAID);
            order.setPaidAt(LocalDateTime.now());

            // 2. 포인트 적립
            BigDecimal pointAmount = order.calculateTotalPrice().multiply(BigDecimal.valueOf(0.02));
            pointLedger.record(order.getUserId(), pointAmount);
            order.setEarnedPoint(pointAmount.intValue());

            // 3. 알림 발송
            notificationSender.send(order.getUserId(), "[주문 완료] 결제가 완료되었습니다.");

            return orderRepository.save(order);
        }
    }

    /**
     * SECTION 4 ADVANCED - AFTER Placeholder:
     * TODO: 결제/포인트/알림을 각각의 도메인 서비스로 분리하고, Order 엔티티에 의미 있는 메서드를 부여해보자.
     */
    public static class OrderWorkflowRefactAfter {
        // 학습자가 직접 작성.
    }

    // ============================================================
    // ADVANCED EXAMPLE 2: 환불/재고 복구/포인트 차감
    // ============================================================

    /**
     * SECTION 4 ADVANCED - BEFORE:
     * - 복잡한 환불 로직이 하나의 서비스에 집중되어 있다.
     * - 여러 Aggregate(Order, Inventory, PointLedger)를 조율하는 책임을 분리해보자.
     */
    public static class RefundWorkflowRefactBefore {
        private final OrderRepository orderRepository;
        private final InventoryRestorer inventoryRestorer;
        private final PointLedger pointLedger;

        public RefundWorkflowRefactBefore(OrderRepository orderRepository,
                                          InventoryRestorer inventoryRestorer,
                                          PointLedger pointLedger) {
            this.orderRepository = orderRepository;
            this.inventoryRestorer = inventoryRestorer;
            this.pointLedger = pointLedger;
        }

        public Order refund(Long orderId) {
            Order order = orderRepository.findById(orderId);
            if (order.getStatus() != OrderStatus.PAID) {
                throw new IllegalStateException("결제 완료 주문만 환불 가능합니다.");
            }

            // 재고 복구
            for (OrderLine line : order.getLines()) {
                inventoryRestorer.restore(line.productId(), line.quantity());
            }

            // 포인트 차감
            pointLedger.record(order.getUserId(), BigDecimal.valueOf(-order.getEarnedPoint()));

            order.setStatus(OrderStatus.REFUNDED);
            order.setRefundedAt(LocalDateTime.now());
            return orderRepository.save(order);
        }
    }

    /**
     * SECTION 4 ADVANCED - AFTER Placeholder:
     * TODO: 환불 프로세스를 별도 도메인 서비스로 구성하고, Order 엔티티에 refund 메서드를 도입해보자.
     */
    public static class RefundWorkflowRefactAfter {
        // 학습자가 직접 작성.
    }

    // ============================================================
    // SUPPORTING TYPES
    // ============================================================

    public static class Order {
        private final Long id;
        private final Long userId;
        private final List<OrderLine> lines = new ArrayList<>();
        private OrderStatus status = OrderStatus.PENDING;
        private String paymentId;
        private LocalDateTime paidAt;
        private LocalDateTime refundedAt;
        private int earnedPoint;

        public Order(Long id, Long userId) {
            this.id = id;
            this.userId = userId;
        }

        public Long getId() {
            return id;
        }

        public Long getUserId() {
            return userId;
        }

        public List<OrderLine> getLines() {
            return lines;
        }

        public OrderStatus getStatus() {
            return status;
        }

        public void setStatus(OrderStatus status) {
            this.status = status;
        }

        public void setPaymentId(String paymentId) {
            this.paymentId = paymentId;
        }

        public void setPaidAt(LocalDateTime paidAt) {
            this.paidAt = paidAt;
        }

        public void setRefundedAt(LocalDateTime refundedAt) {
            this.refundedAt = refundedAt;
        }

        public void setEarnedPoint(int earnedPoint) {
            this.earnedPoint = earnedPoint;
        }

        public int getEarnedPoint() {
            return earnedPoint;
        }

        public void addLine(OrderLine line) {
            lines.add(line);
        }

        public BigDecimal calculateTotalPrice() {
            return lines.stream()
                    .map(line -> line.unitPrice().multiply(BigDecimal.valueOf(line.quantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
    }

    public record OrderLine(Long productId, BigDecimal unitPrice, int quantity) {
    }

    public enum OrderStatus {
        PENDING, PAID, REFUNDED
    }

    public interface OrderRepository {
        Order save(Order order);

        Order findById(Long orderId);
    }

    public interface PaymentGateway {
        PaymentReceipt pay(PaymentRequest request);
    }

    public record PaymentRequest(Long orderId, BigDecimal amount, String method) {
    }

    public record PaymentReceipt(String paymentId, boolean success) {
    }

    public interface PointLedger {
        void record(Long userId, BigDecimal point);
    }

    public interface NotificationSender {
        void send(Long userId, String message);
    }

    public interface InventoryRestorer {
        void restore(Long productId, int quantity);
    }
}
