package com.example.study.lecture.cleancode.spring.answer1.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentGatewayRequest(
        Long purchaseId,
        BigDecimal amount,
        String maskedCardNo,
        Integer installment,
        LocalDateTime requestedAt
) {
}
