package com.example.study.lecture.cleancode.spring.answer1.payment;

import java.time.LocalDateTime;

public record PaymentNotification(
        Long purchaseId,
        Long userId,
        String transactionId,
        LocalDateTime notifiedAt
) {
}
