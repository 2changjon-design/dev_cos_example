package com.example.study.lecture.cleancode.spring.answer.order;

import java.math.BigDecimal;

public record OrderResponse(
        Long orderId,
        Long userId,
        Long productId,
        Integer quantity,
        BigDecimal totalPrice,
        String status
) {
}
