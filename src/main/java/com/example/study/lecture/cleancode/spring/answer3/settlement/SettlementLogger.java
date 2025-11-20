package com.example.study.lecture.cleancode.spring.answer3.settlement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SettlementLogger {

    private static final Logger log = LoggerFactory.getLogger(SettlementLogger.class);

    public void success(Long purchaseId) {
        log.info("[settlement] success purchase={}", purchaseId);
    }

    public void fail(Long purchaseId, Exception exception) {
        log.warn("[settlement] fail purchase={} reason={}", purchaseId, exception.getMessage());
    }
}
