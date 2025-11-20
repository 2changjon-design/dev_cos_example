package com.example.study.lecture.refactor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Section4 Advanced2 정답 코드.
 */
public final class Section4Advanced2RefactSolution {

    private Section4Advanced2RefactSolution() {
        throw new IllegalStateException("Utility class");
    }

    // ============================================================
    // 1) Fulfillment Workflow
    // ============================================================
    public static class FulfillmentWorkflowAfter {
        private final Section4Advanced2RefactExamples.ShipmentService shipmentService;
        private final Section4Advanced2RefactExamples.InvoiceService invoiceService;

        public FulfillmentWorkflowAfter(Section4Advanced2RefactExamples.ShipmentService shipmentService,
                                        Section4Advanced2RefactExamples.InvoiceService invoiceService) {
            this.shipmentService = shipmentService;
            this.invoiceService = invoiceService;
        }

        public Section4Advanced2RefactExamples.Order fulfill(Section4Advanced2RefactExamples.Order order) {
            OrderRules.ensurePaid(order);
            shipmentService.book(order.getId(), order.getAddress());
            OrderRules.markFulfilled(order);
            invoiceService.issue(order.getId(), order.getTotalPrice());
            OrderRules.markInvoiceIssued(order);
            return order;
        }
    }

    // ============================================================
    // 2) Refund Workflow
    // ============================================================
    public static class RefundWorkflowAfter {
        private final Section4Advanced2RefactExamples.PaymentGateway paymentGateway;
        private final Section4Advanced2RefactExamples.InventoryService inventoryService;

        public RefundWorkflowAfter(Section4Advanced2RefactExamples.PaymentGateway paymentGateway,
                                   Section4Advanced2RefactExamples.InventoryService inventoryService) {
            this.paymentGateway = paymentGateway;
            this.inventoryService = inventoryService;
        }

        public Section4Advanced2RefactExamples.Order refund(Section4Advanced2RefactExamples.Order order) {
            OrderRules.ensureFulfilled(order);
            paymentGateway.refund(order.getId(), order.getTotalPrice());
            restoreInventory(order);
            OrderRules.markRefunded(order);
            return order;
        }

        private void restoreInventory(Section4Advanced2RefactExamples.Order order) {
            for (Section4Advanced2RefactExamples.OrderLine line : order.getLines()) {
                inventoryService.restore(line.productId(), line.quantity());
            }
        }
    }

    // ============================================================
    // Order Domain Helper
    // ============================================================
    public static final class OrderRules {
        private OrderRules() {
            throw new IllegalStateException("Utility class");
        }

        public static void ensurePaid(Section4Advanced2RefactExamples.Order order) {
            if (order.getStatus() != Section4Advanced2RefactExamples.OrderStatus.PAID) {
                throw new IllegalStateException("결제 완료 주문만 처리 가능합니다.");
            }
        }

        public static void ensureFulfilled(Section4Advanced2RefactExamples.Order order) {
            if (order.getStatus() != Section4Advanced2RefactExamples.OrderStatus.FULFILLED) {
                throw new IllegalStateException("배송 완료 주문만 환불 가능합니다.");
            }
        }

        public static void markFulfilled(Section4Advanced2RefactExamples.Order order) {
            order.setStatus(Section4Advanced2RefactExamples.OrderStatus.FULFILLED);
            order.setFulfilledAt(LocalDateTime.now());
        }

        public static void markInvoiceIssued(Section4Advanced2RefactExamples.Order order) {
            order.setInvoiceIssued(true);
        }

        public static void markRefunded(Section4Advanced2RefactExamples.Order order) {
            order.setStatus(Section4Advanced2RefactExamples.OrderStatus.REFUNDED);
            order.setRefundedAt(LocalDateTime.now());
        }
    }
}
