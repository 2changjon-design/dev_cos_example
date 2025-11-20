package com.example.study.lecture.cleancode.spring.answer2.report;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PurchaseSummaryResponse(
        Long purchaseId,
        Long userId,
        Long productId,
        String status,
        BigDecimal totalPrice,
        LocalDateTime purchasedAt
) {
}
