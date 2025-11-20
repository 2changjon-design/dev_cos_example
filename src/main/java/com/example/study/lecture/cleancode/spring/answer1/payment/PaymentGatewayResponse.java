package com.example.study.lecture.cleancode.spring.answer1.payment;

public record PaymentGatewayResponse(
        boolean success,
        String transactionId,
        String message
) {
    public static PaymentGatewayResponse ok(String transactionId) {
        return new PaymentGatewayResponse(true, transactionId, "OK");
    }

    public static PaymentGatewayResponse fail(String message) {
        return new PaymentGatewayResponse(false, null, message);
    }
}
