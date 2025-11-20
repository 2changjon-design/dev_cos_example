package com.example.study.lecture.refactorspringsection1;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RefactorSection1OrderResponse(
        Long orderId,
        Long userId,
        BigDecimal totalPrice,
        LocalDateTime createdAt
) {
    public static RefactorSection1OrderResponse from(RefactorSection1Order order) {
        return new RefactorSection1OrderResponse(
                order.getId(),
                order.getUserId(),
                order.getTotalPrice(),
                order.getCreatedAt()
        );
    }
}
