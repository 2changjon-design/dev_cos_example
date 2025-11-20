package com.example.study.lecture.cleancode.spring.answer1.payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PaymentGatewayStub implements PaymentGateway {

    private static final Logger log = LoggerFactory.getLogger(PaymentGatewayStub.class);

    @Override
    public PaymentGatewayResponse pay(PaymentGatewayRequest request) {
        log.info("[stub-gateway] pay {}", request);
        return PaymentGatewayResponse.ok(UUID.randomUUID().toString());
    }
}
