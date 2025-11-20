package com.example.study.lecture.cleancode.spring.answer4;

import java.math.BigDecimal;

public record CommerceOrderResponse(
        Long purchaseId,
        Long userId,
        String userEmail,
        Long productId,
        String productName,
        Integer quantity,
        BigDecimal totalPrice,
        String status
) {
}
