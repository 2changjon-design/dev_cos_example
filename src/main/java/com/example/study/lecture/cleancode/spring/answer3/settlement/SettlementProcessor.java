package com.example.study.lecture.cleancode.spring.answer3.settlement;

import com.example.study.entity.Purchase;
import org.springframework.stereotype.Component;

@Component
public class SettlementProcessor {

    private final SettlementGateway settlementGateway;
    private final SettlementLogger settlementLogger;

    public SettlementProcessor(SettlementGateway settlementGateway, SettlementLogger settlementLogger) {
        this.settlementGateway = settlementGateway;
        this.settlementLogger = settlementLogger;
    }

    public void process(Purchase purchase) {
        try {
            settlementGateway.settle(purchase.getId());
            purchase.markCompleted();
            settlementLogger.success(purchase.getId());
        } catch (RuntimeException exception) {
            settlementLogger.fail(purchase.getId(), exception);
            throw exception;
        }
    }
}
