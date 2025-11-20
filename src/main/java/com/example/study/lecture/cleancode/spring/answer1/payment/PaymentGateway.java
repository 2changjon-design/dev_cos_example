package com.example.study.lecture.cleancode.spring.answer1.payment;

public interface PaymentGateway {
    PaymentGatewayResponse pay(PaymentGatewayRequest request);
}
