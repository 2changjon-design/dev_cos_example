package com.example.study.lecture.refactor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * SECTION 1 Advanced 2: 추가 심화 예제 모음.
 * 주제: 메서드 추출, SRP, 명확한 이름.
 */
public final class Section1Advanced2RefactExamples {

    private Section1Advanced2RefactExamples() {
        throw new IllegalStateException("Utility class");
    }

    // ============================================================
    // Advanced2 - 1) 메서드 추출: 영수증 포맷터
    // ============================================================
    public static class ReceiptFormatterBefore {

        public String format(Receipt receipt) {
            StringBuilder builder = new StringBuilder();
            builder.append("== Receipt ==\n");
            BigDecimal total = BigDecimal.ZERO;
            for (ReceiptItem item : receipt.items()) {
                BigDecimal lineTotal = item.price().multiply(BigDecimal.valueOf(item.quantity()));
                builder.append(item.productName()).append(" x ").append(item.quantity())
                        .append(" = ").append(lineTotal).append("\n");
                total = total.add(lineTotal);
            }
            if (receipt.discountRate() > 0) {
                BigDecimal discount = total.multiply(BigDecimal.valueOf(receipt.discountRate()));
                total = total.subtract(discount);
                builder.append("Discount: -").append(discount).append("\n");
            }
            builder.append("Total: ").append(total).append("\n");
            builder.append("Issued At: ").append(LocalDateTime.now());
            return builder.toString();
        }
    }

    public static class ReceiptFormatterAfter {
        // TODO: 항목 포맷팅, 총액 계산, 할인 계산을 메서드로 분리해보세요.
    }

    public record Receipt(List<ReceiptItem> items, double discountRate) {
    }

    public record ReceiptItem(String productName, BigDecimal price, int quantity) {
    }

    // ============================================================
    // Advanced2 - 2) 단일 책임: 재고 동기화
    // ============================================================
    public static class InventorySyncBefore {
        private final WarehouseApi warehouseApi;
        private final AuditTrail auditTrail;

        public InventorySyncBefore(WarehouseApi warehouseApi, AuditTrail auditTrail) {
            this.warehouseApi = warehouseApi;
            this.auditTrail = auditTrail;
        }

        public void sync(ProductStock stock) {
            if (stock.quantity() < 0) {
                throw new IllegalArgumentException("수량이 음수입니다.");
            }
            warehouseApi.push(stock.productId(), stock.quantity());
            auditTrail.write("SYNC " + stock.productId() + " qty=" + stock.quantity());
            System.out.printf("[InventorySync] product=%s qty=%d%n", stock.productId(), stock.quantity());
        }
    }

    public static class InventorySyncAfter {
        // TODO: 검증, API 호출, 감사 로그, 콘솔 로그를 역할별 클래스로 나눠보세요.
    }

    public record ProductStock(String productId, int quantity) {
    }

    public interface WarehouseApi {
        void push(String productId, int quantity);
    }

    public interface AuditTrail {
        void write(String message);
    }

    // ============================================================
    // Advanced2 - 3) 명확한 이름 짓기: 구독 통계
    // ============================================================
    public static class SubscriptionStatsBefore {
        private int u1;
        private int u2;
        private int u3;

        public void add(boolean a, boolean b) {
            if (a) {
                u1++;
            }
            if (b) {
                u2++;
            } else {
                u3++;
            }
        }

        public int getU1() {
            return u1;
        }

        public int getU2() {
            return u2;
        }

        public int getU3() {
            return u3;
        }
    }

    public static class SubscriptionStatsAfter {
        // TODO: 의미를 드러내는 필드/메서드 이름으로 다시 작성해보세요.
    }
}
