package com.example.study.lecture.refactor;

import com.example.study.lecture.refactor.Section4AdvancedRefactExamples.InventoryRestorer;
import com.example.study.lecture.refactor.Section4AdvancedRefactExamples.NotificationSender;
import com.example.study.lecture.refactor.Section4AdvancedRefactExamples.Order;
import com.example.study.lecture.refactor.Section4AdvancedRefactExamples.OrderLine;
import com.example.study.lecture.refactor.Section4AdvancedRefactExamples.OrderRepository;
import com.example.study.lecture.refactor.Section4AdvancedRefactExamples.OrderStatus;
import com.example.study.lecture.refactor.Section4AdvancedRefactExamples.PaymentGateway;
import com.example.study.lecture.refactor.Section4AdvancedRefactExamples.PaymentReceipt;
import com.example.study.lecture.refactor.Section4AdvancedRefactExamples.PaymentRequest;
import com.example.study.lecture.refactor.Section4AdvancedRefactExamples.PointLedger;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Section 4 Advanced 실습 정답.
 */
public final class Section4AdvancedRefactSolution {

    private Section4AdvancedRefactSolution() {
        throw new IllegalStateException("Utility class");
    }

    // ============================================================
    // ADVANCED EXAMPLE 1 - SOLUTION
    // ============================================================
    public static class OrderWorkflowRefactAfter {
        private final OrderRepository orderRepository;
        private final PaymentGateway paymentGateway;
        private final PointService pointService;
        private final NotificationSender notificationSender;

        public OrderWorkflowRefactAfter(OrderRepository orderRepository,
                                        PaymentGateway paymentGateway,
                                        PointService pointService,
                                        NotificationSender notificationSender) {
            this.orderRepository = orderRepository;
            this.paymentGateway = paymentGateway;
            this.pointService = pointService;
            this.notificationSender = notificationSender;
        }

        public Order process(Order order, PaymentRequest paymentRequest) {
            OrderHelper.ensurePending(order);
            PaymentReceipt receipt = paymentGateway.pay(paymentRequest);
            OrderHelper.markPaid(order, receipt.paymentId());
            pointService.addPoints(order);
            notificationSender.send(order.getUserId(), "[주문 완료] 결제가 완료되었습니다.");
            return orderRepository.save(order);
        }
    }

    public static class PointService {
        private final PointLedger pointLedger;

        public PointService(PointLedger pointLedger) {
            this.pointLedger = pointLedger;
        }

        public void addPoints(Order order) {
            BigDecimal pointAmount = order.calculateTotalPrice().multiply(BigDecimal.valueOf(0.02));
            pointLedger.record(order.getUserId(), pointAmount);
            order.setEarnedPoint(pointAmount.intValue());
        }
    }

    // ============================================================
    // ADVANCED EXAMPLE 2 - SOLUTION
    // ============================================================
    public static class RefundWorkflowRefactAfter {
        private final OrderRepository orderRepository;
        private final InventoryRestorer inventoryRestorer;
        private final PointLedger pointLedger;

        public RefundWorkflowRefactAfter(OrderRepository orderRepository,
                                         InventoryRestorer inventoryRestorer,
                                         PointLedger pointLedger) {
            this.orderRepository = orderRepository;
            this.inventoryRestorer = inventoryRestorer;
            this.pointLedger = pointLedger;
        }

        public Order refund(Long orderId) {
            Order order = orderRepository.findById(orderId);
            OrderHelper.ensureRefundable(order);
            restoreInventory(order);
            reversePoints(order);
            OrderHelper.markRefunded(order);
            return orderRepository.save(order);
        }

        private void restoreInventory(Order order) {
            for (OrderLine line : order.getLines()) {
                inventoryRestorer.restore(line.productId(), line.quantity());
            }
        }

        private void reversePoints(Order order) {
            if (order.getEarnedPoint() != 0) {
                pointLedger.record(order.getUserId(), BigDecimal.valueOf(-order.getEarnedPoint()));
            }
        }
    }

    // Helper to encapsulate Order behavior without modifying original class
    public static final class OrderHelper {
        public static void ensurePending(Order order) {
            if (order.getStatus() != OrderStatus.PENDING) {
                throw new IllegalStateException("주문이 이미 처리되었습니다.");
            }
        }

        public static void ensureRefundable(Order order) {
            if (order.getStatus() != OrderStatus.PAID) {
                throw new IllegalStateException("결제 완료 주문만 환불 가능합니다.");
            }
        }

        public static void markPaid(Order order, String paymentId) {
            order.setPaymentId(paymentId);
            order.setStatus(OrderStatus.PAID);
            order.setPaidAt(LocalDateTime.now());
        }

        public static void markRefunded(Order order) {
            order.setStatus(OrderStatus.REFUNDED);
            order.setRefundedAt(LocalDateTime.now());
        }

        private OrderHelper() {
            throw new IllegalStateException("Utility class");
        }
    }
}
