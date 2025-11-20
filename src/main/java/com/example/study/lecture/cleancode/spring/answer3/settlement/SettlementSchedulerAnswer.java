package com.example.study.lecture.cleancode.spring.answer3.settlement;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SettlementSchedulerAnswer {

    private final SettlementServiceAnswer settlementService;

    public SettlementSchedulerAnswer(SettlementServiceAnswer settlementService) {
        this.settlementService = settlementService;
    }

    @Scheduled(fixedDelayString = "${settlement.job.delay:60000}")
    public void run() {
        settlementService.settleBatch();
    }
}
