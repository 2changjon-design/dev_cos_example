package com.example.study.lecture.cleancode.spring.answer1.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(
        Long purchaseId,
        BigDecimal amount,
        String transactionId,
        String status,
        LocalDateTime paidAt
) {
}
