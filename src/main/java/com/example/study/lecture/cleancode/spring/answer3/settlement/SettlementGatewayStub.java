package com.example.study.lecture.cleancode.spring.answer3.settlement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SettlementGatewayStub implements SettlementGateway {

    private static final Logger log = LoggerFactory.getLogger(SettlementGatewayStub.class);

    @Override
    public void settle(Long purchaseId) {
        log.info("[stub-settlement] purchase={} settled", purchaseId);
    }
}
