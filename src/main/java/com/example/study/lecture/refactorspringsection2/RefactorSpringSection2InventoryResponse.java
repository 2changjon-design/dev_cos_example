package com.example.study.lecture.refactorspringsection2;

import java.time.LocalDateTime;

public record RefactorSpringSection2InventoryResponse(
        Long productId,
        int quantity,
        LocalDateTime updatedAt
) {
    public static RefactorSpringSection2InventoryResponse from(RefactorSpringSection2InventoryRecord record) {
        return new RefactorSpringSection2InventoryResponse(
                record.getProductId(),
                record.getQuantity(),
                record.getUpdatedAt()
        );
    }
}
