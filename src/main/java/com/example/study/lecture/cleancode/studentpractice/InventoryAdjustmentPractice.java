package com.example.study.lecture.cleancode.studentpractice;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * 학생 전용 과제 1.
 * 재고 보정, 가격 업데이트, 감사 로그가 한 메서드에 뒤섞여 있다.
 *
 * TODO(STUDENT):
 *  1) `adjust` 메서드의 책임(입력 검증, 수량 계산, 가격 업데이트, 감사 로그)을 나누어 SRP를 적용하세요.
 *  2) `Map<String, Object>` 반환 대신, 의미 있는 DTO/record를 만들어 응답을 표현하세요.
 *  3) 중복되는 메시지/상수는 별도 상수 또는 enum으로 추출하세요.
 */
public class InventoryAdjustmentPractice {

    private final AuditWriter auditWriter;

    public InventoryAdjustmentPractice(AuditWriter auditWriter) {
        this.auditWriter = auditWriter;
    }

    public Map<String, Object> adjust(String sku, Integer quantity, BigDecimal price, boolean manual) {
        Map<String, Object> result = new HashMap<>();
        if (sku == null || sku.isBlank()) {
            result.put("status", "FAIL");
            result.put("message", "SKU required");
            return result;
        }
        if (quantity == null) {
            result.put("status", "FAIL");
            result.put("message", "quantity required");
            return result;
        }

        auditWriter.write("adjust start : " + sku);

        int adj = quantity;
        if (manual) {
            adj = quantity - 1;
        } else {
            adj = quantity + 2;
        }

        BigDecimal newPrice = price;
        if (price != null) {
            if (manual) {
                newPrice = price.multiply(BigDecimal.valueOf(1.1));
            } else {
                newPrice = price.multiply(BigDecimal.valueOf(0.95));
            }
        }

        result.put("status", "OK");
        result.put("sku", sku);
        result.put("quantity", adj);
        result.put("price", newPrice);
        result.put("manual", manual);
        result.put("loggedAt", Instant.now());

        auditWriter.write("adjust finish : " + sku + ", qty=" + adj + ", price=" + newPrice);
        return result;
    }

    public interface AuditWriter {
        void write(String message);
    }
}
