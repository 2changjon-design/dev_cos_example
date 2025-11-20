package com.example.study.lecture.cleancode.spring.answer4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NotificationGatewayStub implements NotificationGateway {

    private static final Logger log = LoggerFactory.getLogger(NotificationGatewayStub.class);

    @Override
    public void notifyOrder(Long purchaseId, String userEmail, Long productId) {
        log.info("[notification] purchase={} user={} product={}", purchaseId, userEmail, productId);
    }
}
