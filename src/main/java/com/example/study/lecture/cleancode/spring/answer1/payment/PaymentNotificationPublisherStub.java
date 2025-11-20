package com.example.study.lecture.cleancode.spring.answer1.payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PaymentNotificationPublisherStub implements PaymentNotificationPublisher {

    private static final Logger log = LoggerFactory.getLogger(PaymentNotificationPublisherStub.class);

    @Override
    public void publish(PaymentNotification notification) {
        log.info("[stub-notify] {}", notification);
    }
}
