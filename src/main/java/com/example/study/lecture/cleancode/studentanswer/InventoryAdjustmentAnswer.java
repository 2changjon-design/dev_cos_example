package com.example.study.lecture.cleancode.studentanswer;

import java.math.BigDecimal;
import java.time.Instant;

public class InventoryAdjustmentAnswer {

    private final AuditWriter auditWriter;

    public InventoryAdjustmentAnswer(AuditWriter auditWriter) {
        this.auditWriter = auditWriter;
    }

    public AdjustmentResult adjust(AdjustmentRequest request) {
        request.validate();
        auditWriter.write("adjust start : " + request.sku());

        int adjustedQuantity = calculateQuantity(request.quantity(), request.manual());
        BigDecimal adjustedPrice = adjustPrice(request.price(), request.manual());

        AdjustmentResult result = new AdjustmentResult(
                request.sku(),
                adjustedQuantity,
                adjustedPrice,
                request.manual(),
                Instant.now()
        );

        auditWriter.write("adjust finish : " + request.sku() + ", qty=" + adjustedQuantity + ", price=" + adjustedPrice);
        return result;
    }

    private int calculateQuantity(int quantity, boolean manual) {
        return manual ? quantity - 1 : quantity + 2;
    }

    private BigDecimal adjustPrice(BigDecimal price, boolean manual) {
        if (price == null) {
            return null;
        }
        BigDecimal multiplier = manual ? BigDecimal.valueOf(1.1) : BigDecimal.valueOf(0.95);
        return price.multiply(multiplier);
    }

    public record AdjustmentRequest(String sku, Integer quantity, BigDecimal price, boolean manual) {
        public void validate() {
            if (sku == null || sku.isBlank()) {
                throw new IllegalArgumentException("SKU required");
            }
            if (quantity == null) {
                throw new IllegalArgumentException("quantity required");
            }
        }
    }

    public record AdjustmentResult(
            String sku,
            int quantity,
            BigDecimal price,
            boolean manual,
            Instant loggedAt
    ) {
    }

    public interface AuditWriter {
        void write(String message);
    }
}
