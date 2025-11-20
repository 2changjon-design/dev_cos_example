package com.example.study.lecture.cleancode.spring.answer.refund;

import com.example.study.PurchaseStatus;
import com.example.study.entity.Purchase;
import org.springframework.stereotype.Component;

@Component
public class RefundValidator {

    public void validate(Purchase purchase, RefundRequest request) {
        if (purchase.getStatus() == PurchaseStatus.REFUNDED) {
            throw new IllegalStateException("이미 환불된 주문입니다.");
        }
        if (request.reason() == null || request.reason().isBlank()) {
            throw new IllegalArgumentException("환불 사유는 필수입니다.");
        }
    }
}
