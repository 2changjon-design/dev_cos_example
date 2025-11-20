package com.example.study.lecture.refactor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * SECTION 2 Advanced 2: 메서드 추출 + 책임 분리 + Rename 심화 예제.
 */
public final class Section2Advanced2RefactExamples {

    private Section2Advanced2RefactExamples() {
        throw new IllegalStateException("Utility class");
    }

    // ============================================================
    // 1) 메서드 추출: 정산 배치 작업
    // ============================================================
    public static class SettlementBatchBefore {

        public SettlementReport run(List<SettlementRequest> requests) {
            BigDecimal totalAmount = BigDecimal.ZERO;
            List<String> errors = new ArrayList<>();

            for (SettlementRequest request : requests) {
                if (request.amount().compareTo(BigDecimal.ZERO) <= 0) {
                    errors.add(request.id() + ": invalid amount");
                    continue;
                }
                if (!request.currency().equals("KRW")) {
                    errors.add(request.id() + ": unsupported currency");
                    continue;
                }
                BigDecimal fee = request.amount().multiply(BigDecimal.valueOf(0.015));
                BigDecimal net = request.amount().subtract(fee);
                totalAmount = totalAmount.add(net);
            }
            return new SettlementReport(totalAmount, errors);
        }
    }

    public static class SettlementBatchAfter {
        // TODO: 검증/수수료 계산/합계를 private 메서드로 분리해보세요.
    }

    public record SettlementRequest(String id, BigDecimal amount, String currency) {
    }

    public record SettlementReport(BigDecimal totalAmount, List<String> errors) {
    }

    // ============================================================
    // 2) 책임 분리: API 키 회전
    // ============================================================
    public static class ApiKeyRotationBefore {
        private final ApiKeyRepository repository;
        private final NotificationService notificationService;

        public ApiKeyRotationBefore(ApiKeyRepository repository,
                                    NotificationService notificationService) {
            this.repository = repository;
            this.notificationService = notificationService;
        }

        public void rotate(String serviceId) {
            String apiKey = repository.generate(serviceId);
            repository.store(serviceId, apiKey);
            notificationService.notify(serviceId, "API key rotated: " + apiKey);
            System.out.printf("[API-KEY] service=%s key=%s%n", serviceId, apiKey);
        }
    }

    public static class ApiKeyRotationAfter {
        // TODO: 키 생성, 저장, 알림, 감사 로그를 개별 컴포넌트로 분리해보세요.
    }

    public interface ApiKeyRepository {
        String generate(String serviceId);

        void store(String serviceId, String apiKey);
    }

    public interface NotificationService {
        void notify(String serviceId, String message);
    }

    // ============================================================
    // 3) Rename: 분석 통계
    // ============================================================
    public static class AnalyticsStatsBefore {
        private int a;
        private int b;

        public void calc(int n, boolean flag) {
            a += n;
            if (flag) {
                b++;
            }
        }

        public int getA() {
            return a;
        }

        public int getB() {
            return b;
        }
    }

    public static class AnalyticsStatsAfter {
        // TODO: 의미 있는 변수 이름(예: totalEvents, flaggedEvents)으로 바꿔보세요.
    }
}
