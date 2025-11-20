package com.example.study.lecture.cleancode.spring.answer1.payment;

public record PaymentRequest(String cardNo, Integer installment) {

    public void validate() {
        if (cardNo == null || cardNo.isBlank()) {
            throw new IllegalArgumentException("cardNo는 필수입니다.");
        }
        if (installment != null && installment < 1) {
            throw new IllegalArgumentException("할부 개월 수는 1 이상이어야 합니다.");
        }
    }
}
