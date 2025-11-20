package com.example.study.lecture.refactor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Section2 Advanced2 정답 코드.
 */
public final class Section2Advanced2RefactSolution {

    private Section2Advanced2RefactSolution() {
        throw new IllegalStateException("Utility class");
    }

    // ============================================================
    // 1) 메서드 추출 - 정산 배치
    // ============================================================
    public static class SettlementBatchAfter {

        public Section2Advanced2RefactExamples.SettlementReport run(List<Section2Advanced2RefactExamples.SettlementRequest> requests) {
            BigDecimal totalAmount = BigDecimal.ZERO;
            List<String> errors = new ArrayList<>();

            for (Section2Advanced2RefactExamples.SettlementRequest request : requests) {
                if (!isValid(request, errors)) {
                    continue;
                }
                totalAmount = totalAmount.add(calculateNetAmount(request));
            }
            return new Section2Advanced2RefactExamples.SettlementReport(totalAmount, errors);
        }

        private boolean isValid(Section2Advanced2RefactExamples.SettlementRequest request, List<String> errors) {
            if (request.amount().compareTo(BigDecimal.ZERO) <= 0) {
                errors.add(request.id() + ": invalid amount");
                return false;
            }
            if (!"KRW".equals(request.currency())) {
                errors.add(request.id() + ": unsupported currency");
                return false;
            }
            return true;
        }

        private BigDecimal calculateNetAmount(Section2Advanced2RefactExamples.SettlementRequest request) {
            BigDecimal fee = request.amount().multiply(BigDecimal.valueOf(0.015));
            return request.amount().subtract(fee);
        }
    }

    // ============================================================
    // 2) 책임 분리 - API 키 회전
    // ============================================================
    public static class ApiKeyRotationAfter {
        private final KeyGenerator generator;
        private final KeyStore keyStore;
        private final KeyNotifier notifier;
        private final KeyLogger logger;

        public ApiKeyRotationAfter(KeyGenerator generator, KeyStore keyStore, KeyNotifier notifier, KeyLogger logger) {
            this.generator = generator;
            this.keyStore = keyStore;
            this.notifier = notifier;
            this.logger = logger;
        }

        public void rotate(String serviceId) {
            String apiKey = generator.generate(serviceId);
            keyStore.store(serviceId, apiKey);
            notifier.notify(serviceId, apiKey);
            logger.log(serviceId, apiKey);
        }
    }

    public interface KeyGenerator {
        String generate(String serviceId);
    }

    public interface KeyStore {
        void store(String serviceId, String apiKey);
    }

    public interface KeyNotifier {
        void notify(String serviceId, String apiKey);
    }

    public static class KeyLogger {
        public void log(String serviceId, String apiKey) {
            System.out.printf("[API-KEY] service=%s key=%s%n", serviceId, apiKey);
        }
    }

    // ============================================================
    // 3) Rename - 분석 통계
    // ============================================================
    public static class AnalyticsStatsAfter {
        private int totalEvents;
        private int flaggedEvents;

        public void recordEvent(int eventCount, boolean flagged) {
            totalEvents += eventCount;
            if (flagged) {
                flaggedEvents++;
            }
        }

        public int getTotalEvents() {
            return totalEvents;
        }

        public int getFlaggedEvents() {
            return flaggedEvents;
        }
    }
}
