package com.example.study.lecture.cleancode.spring.answer.refund;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class RefundAuditLogger {

    public void appendSuccess(Long purchaseId, String reason) {
        System.out.printf("[AUDIT] refund success purchase=%s reason=%s at=%s%n",
                purchaseId, reason, LocalDateTime.now());
    }
}
