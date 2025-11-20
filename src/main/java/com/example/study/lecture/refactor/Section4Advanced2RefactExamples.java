package com.example.study.lecture.refactor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * SECTION 4 Advanced 2: 서비스 분리 + 풍부한 도메인 심화.
 */
public final class Section4Advanced2RefactExamples {

    private Section4Advanced2RefactExamples() {
        throw new IllegalStateException("Utility class");
    }

    // ============================================================
    // 1) Fulfillment Workflow (Before)
    // ============================================================
    public static class FulfillmentWorkflowBefore {
        private final ShipmentService shipmentService;
        private final InvoiceService invoiceService;

        public FulfillmentWorkflowBefore(ShipmentService shipmentService, InvoiceService invoiceService) {
            this.shipmentService = shipmentService;
            this.invoiceService = invoiceService;
        }

        public Order fulfill(Order order) {
            if (order.getStatus() != OrderStatus.PAID) {
                throw new IllegalStateException("결제 완료 주문만 처리 가능");
            }

            shipmentService.book(order.getId(), order.getAddress());
            order.setStatus(OrderStatus.FULFILLED);
            order.setFulfilledAt(LocalDateTime.now());

            invoiceService.issue(order.getId(), order.getTotalPrice());
            order.setInvoiceIssued(true);

            return order;
        }
    }

    public static class FulfillmentWorkflowAfter {
        // TODO: 배송 예약/인보이스 발급/주문 상태 변경을 각 도메인 메서드로 이동하세요.
    }

    // ============================================================
    // 2) Refund Workflow (Before)
    // ============================================================
    public static class RefundWorkflowBefore {
        private final PaymentGateway paymentGateway;
        private final InventoryService inventoryService;

        public RefundWorkflowBefore(PaymentGateway paymentGateway,
                                    InventoryService inventoryService) {
            this.paymentGateway = paymentGateway;
            this.inventoryService = inventoryService;
        }

        public Order refund(Order order) {
            if (order.getStatus() != OrderStatus.FULFILLED) {
                throw new IllegalStateException("배송 완료 주문만 환불 가능합니다.");
            }

            paymentGateway.refund(order.getId(), order.getTotalPrice());
            for (OrderLine line : order.getLines()) {
                inventoryService.restore(line.productId(), line.quantity());
            }
            order.setStatus(OrderStatus.REFUNDED);
            order.setRefundedAt(LocalDateTime.now());
            return order;
        }
    }

    public static class RefundWorkflowAfter {
        // TODO: 환불 검증/결제/재고 복구/상태 변경을 협력 객체로 분리하세요.
    }

    // ============================================================
    // Support types
    // ============================================================
    public static class Order {
        private final Long id;
        private final Long userId;
        private final String address;
        private final List<OrderLine> lines = new ArrayList<>();
        private BigDecimal totalPrice = BigDecimal.ZERO;
        private OrderStatus status = OrderStatus.PAID;
        private boolean invoiceIssued;
        private LocalDateTime fulfilledAt;
        private LocalDateTime refundedAt;

        public Order(Long id, Long userId, String address) {
            this.id = id;
            this.userId = userId;
            this.address = address;
        }

        public Long getId() {
            return id;
        }

        public Long getUserId() {
            return userId;
        }

        public String getAddress() {
            return address;
        }

        public List<OrderLine> getLines() {
            return lines;
        }

        public BigDecimal getTotalPrice() {
            return totalPrice;
        }

        public OrderStatus getStatus() {
            return status;
        }

        public void setStatus(OrderStatus status) {
            this.status = status;
        }

        public boolean isInvoiceIssued() {
            return invoiceIssued;
        }

        public void setInvoiceIssued(boolean invoiceIssued) {
            this.invoiceIssued = invoiceIssued;
        }

        public LocalDateTime getFulfilledAt() {
            return fulfilledAt;
        }

        public void setFulfilledAt(LocalDateTime fulfilledAt) {
            this.fulfilledAt = fulfilledAt;
        }

        public LocalDateTime getRefundedAt() {
            return refundedAt;
        }

        public void setRefundedAt(LocalDateTime refundedAt) {
            this.refundedAt = refundedAt;
        }

        public void addLine(OrderLine line) {
            lines.add(line);
            totalPrice = totalPrice.add(line.unitPrice().multiply(BigDecimal.valueOf(line.quantity())));
        }
    }

    public record OrderLine(Long productId, BigDecimal unitPrice, int quantity) {
    }

    public enum OrderStatus {
        PAID, FULFILLED, REFUNDED
    }

    public interface ShipmentService {
        void book(Long orderId, String address);
    }

    public interface InvoiceService {
        void issue(Long orderId, BigDecimal amount);
    }

    public interface PaymentGateway {
        void refund(Long orderId, BigDecimal amount);
    }

    public interface InventoryService {
        void restore(Long productId, int quantity);
    }
}
