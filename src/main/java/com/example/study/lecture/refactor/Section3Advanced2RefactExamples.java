package com.example.study.lecture.refactor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * SECTION 3 Advanced 2: IDE 리팩토링 도구 심화 예제.
 */
public final class Section3Advanced2RefactExamples {

    private Section3Advanced2RefactExamples() {
        throw new IllegalStateException("Utility class");
    }

    // ============================================================
    // 1) Extract Method: 영업 커미션 계산
    // ============================================================
    public static class CommissionCalculatorBefore {

        public BigDecimal calculate(List<SalesRecord> records) {
            BigDecimal totalCommission = BigDecimal.ZERO;
            for (SalesRecord record : records) {
                BigDecimal commission = record.amount().multiply(BigDecimal.valueOf(0.12));
                if (record.amount().compareTo(BigDecimal.valueOf(100_000)) > 0) {
                    commission = commission.add(BigDecimal.valueOf(5_000));
                }
                if (record.region().equals("OVERSEAS")) {
                    commission = commission.add(BigDecimal.valueOf(7_500));
                }
                totalCommission = totalCommission.add(commission);
            }
            return totalCommission;
        }
    }

    public static class CommissionCalculatorAfter {
        // TODO: commission 계산 로직을 별도 메서드로 추출해보세요.
    }

    public record SalesRecord(BigDecimal amount, String region) {
    }

    // ============================================================
    // 2) Rename/Introduce Variable: 복잡한 조건
    // ============================================================
    public static class FeatureFlagCheckerBefore {

        public boolean enabled(UserContext ctx) {
            return ctx.role().equals("ADMIN") && ctx.country().equals("KR") && ctx.loginCount() > 5;
        }
    }

    public static class FeatureFlagCheckerAfter {
        // TODO: 의미 있는 변수명/메서드명으로 바꿔보세요.
    }

    public record UserContext(String role, String country, int loginCount) {
    }

    // ============================================================
    // 3) Extract Class for IDE demo: 보고서 빌더
    // ============================================================
    public static class ReportBuilderBefore {
        private final List<String> lines = new ArrayList<>();

        public String build(List<ProjectStatus> statuses) {
            lines.add("=== Report ===");
            for (ProjectStatus status : statuses) {
                lines.add(status.projectName() + ": " + status.progress() + "%");
            }
            lines.add("Total Projects: " + statuses.size());
            return String.join("\n", lines);
        }
    }

    public static class ReportBuilderAfter {
        // TODO: lines 리스트를 별도 클래스로 추출해보세요.
    }

    public record ProjectStatus(String projectName, int progress) {
    }
}
