package com.example.study.lecture.refactor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Section1 Advanced2 정답 코드.
 */
public final class Section1Advanced2RefactSolution {

    private Section1Advanced2RefactSolution() {
        throw new IllegalStateException("Utility class");
    }

    // ============================================================
    // Advanced2 - 1) 메서드 추출
    // ============================================================
    public static class ReceiptFormatterAfter {

        public String format(Section1Advanced2RefactExamples.Receipt receipt) {
            BigDecimal subtotal = calculateSubtotal(receipt);
            BigDecimal discountedTotal = applyDiscount(subtotal, receipt.discountRate());
            return buildReceiptText(receipt, subtotal, discountedTotal);
        }

        private BigDecimal calculateSubtotal(Section1Advanced2RefactExamples.Receipt receipt) {
            BigDecimal subtotal = BigDecimal.ZERO;
            for (Section1Advanced2RefactExamples.ReceiptItem item : receipt.items()) {
                subtotal = subtotal.add(lineTotal(item));
            }
            return subtotal;
        }

        private BigDecimal lineTotal(Section1Advanced2RefactExamples.ReceiptItem item) {
            return item.price().multiply(BigDecimal.valueOf(item.quantity()));
        }

        private BigDecimal applyDiscount(BigDecimal subtotal, double discountRate) {
            return discountRate <= 0 ? subtotal : subtotal.subtract(subtotal.multiply(BigDecimal.valueOf(discountRate)));
        }

        private String buildReceiptText(Section1Advanced2RefactExamples.Receipt receipt,
                                        BigDecimal subtotal,
                                        BigDecimal discountedTotal) {
            StringBuilder builder = new StringBuilder();
            builder.append("== Receipt ==\n");
            for (Section1Advanced2RefactExamples.ReceiptItem item : receipt.items()) {
                builder.append(item.productName())
                        .append(" x ").append(item.quantity())
                        .append(" = ").append(lineTotal(item)).append("\n");
            }
            if (discountedTotal.compareTo(subtotal) < 0) {
                builder.append("Discount: -").append(subtotal.subtract(discountedTotal)).append("\n");
            }
            builder.append("Total: ").append(discountedTotal).append("\n");
            builder.append("Issued At: ").append(LocalDateTime.now());
            return builder.toString();
        }
    }

    // ============================================================
    // Advanced2 - 2) 단일 책임
    // ============================================================
    public static class InventorySyncAfter {
        private final StockValidator validator;
        private final Section1Advanced2RefactExamples.WarehouseApi warehouseApi;
        private final Section1Advanced2RefactExamples.AuditTrail auditTrail;
        private final SyncLogger syncLogger;

        public InventorySyncAfter(StockValidator validator,
                                  Section1Advanced2RefactExamples.WarehouseApi warehouseApi,
                                  Section1Advanced2RefactExamples.AuditTrail auditTrail,
                                  SyncLogger syncLogger) {
            this.validator = validator;
            this.warehouseApi = warehouseApi;
            this.auditTrail = auditTrail;
            this.syncLogger = syncLogger;
        }

        public void sync(Section1Advanced2RefactExamples.ProductStock stock) {
            validator.validate(stock);
            warehouseApi.push(stock.productId(), stock.quantity());
            auditTrail.write("SYNC " + stock.productId() + " qty=" + stock.quantity());
            syncLogger.log(stock);
        }
    }

    public static class StockValidator {
        public void validate(Section1Advanced2RefactExamples.ProductStock stock) {
            if (stock.quantity() < 0) {
                throw new IllegalArgumentException("수량이 음수입니다.");
            }
        }
    }

    public static class SyncLogger {
        public void log(Section1Advanced2RefactExamples.ProductStock stock) {
            System.out.printf("[InventorySync] product=%s qty=%d%n", stock.productId(), stock.quantity());
        }
    }

    // ============================================================
    // Advanced2 - 3) 명확한 이름
    // ============================================================
    public static class SubscriptionStatsAfter {
        private int totalSubscribedUsers;
        private int premiumUsers;
        private int standardUsers;

        public void recordSubscription(boolean subscribed, boolean premium) {
            if (subscribed) {
                totalSubscribedUsers++;
            }
            if (premium) {
                premiumUsers++;
            } else {
                standardUsers++;
            }
        }

        public int getTotalSubscribedUsers() {
            return totalSubscribedUsers;
        }

        public int getPremiumUsers() {
            return premiumUsers;
        }

        public int getStandardUsers() {
            return standardUsers;
        }
    }
}
