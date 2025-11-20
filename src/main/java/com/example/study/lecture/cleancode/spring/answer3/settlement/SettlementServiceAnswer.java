package com.example.study.lecture.cleancode.spring.answer3.settlement;

import com.example.study.entity.Purchase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SettlementServiceAnswer {

    private static final int BATCH_SIZE = 20;

    private final PendingPurchaseFinder pendingPurchaseFinder;
    private final SettlementProcessor settlementProcessor;

    public SettlementServiceAnswer(
            PendingPurchaseFinder pendingPurchaseFinder,
            SettlementProcessor settlementProcessor
    ) {
        this.pendingPurchaseFinder = pendingPurchaseFinder;
        this.settlementProcessor = settlementProcessor;
    }

    @Transactional
    public void settleBatch() {
        List<Purchase> pendingPurchases = pendingPurchaseFinder.findBatch(BATCH_SIZE);
        for (Purchase purchase : pendingPurchases) {
            settlementProcessor.process(purchase);
        }
    }
}
