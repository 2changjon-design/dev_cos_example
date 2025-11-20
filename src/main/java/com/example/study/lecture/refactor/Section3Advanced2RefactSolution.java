package com.example.study.lecture.refactor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Section3 Advanced2 정답 코드.
 */
public final class Section3Advanced2RefactSolution {

    private Section3Advanced2RefactSolution() {
        throw new IllegalStateException("Utility class");
    }

    // ============================================================
    // 1) Extract Method - 커미션 계산
    // ============================================================
    public static class CommissionCalculatorAfter {

        public BigDecimal calculate(List<Section3Advanced2RefactExamples.SalesRecord> records) {
            BigDecimal totalCommission = BigDecimal.ZERO;
            for (Section3Advanced2RefactExamples.SalesRecord record : records) {
                totalCommission = totalCommission.add(calculateCommission(record));
            }
            return totalCommission;
        }

        private BigDecimal calculateCommission(Section3Advanced2RefactExamples.SalesRecord record) {
            BigDecimal commission = record.amount().multiply(BigDecimal.valueOf(0.12));
            if (record.amount().compareTo(BigDecimal.valueOf(100_000)) > 0) {
                commission = commission.add(BigDecimal.valueOf(5_000));
            }
            if ("OVERSEAS".equals(record.region())) {
                commission = commission.add(BigDecimal.valueOf(7_500));
            }
            return commission;
        }
    }

    // ============================================================
    // 2) Rename/Introduce Variable - Feature Flag
    // ============================================================
    public static class FeatureFlagCheckerAfter {

        public boolean isFeatureEnabled(Section3Advanced2RefactExamples.UserContext context) {
            boolean admin = "ADMIN".equals(context.role());
            boolean locatedInKorea = "KR".equals(context.country());
            boolean isActiveUser = context.loginCount() > 5;
            return admin && locatedInKorea && isActiveUser;
        }
    }

    // ============================================================
    // 3) Extract Class - Report Builder
    // ============================================================
    public static class ReportBuilderAfter {

        public String build(List<Section3Advanced2RefactExamples.ProjectStatus> statuses) {
            ReportLines lines = new ReportLines();
            lines.add("=== Report ===");
            for (Section3Advanced2RefactExamples.ProjectStatus status : statuses) {
                lines.add(status.projectName() + ": " + status.progress() + "%");
            }
            lines.add("Total Projects: " + statuses.size());
            return lines.join();
        }
    }

    public static class ReportLines {
        private final StringBuilder builder = new StringBuilder();

        public void add(String line) {
            if (builder.length() > 0) {
                builder.append("\n");
            }
            builder.append(line);
        }

        public String join() {
            return builder.toString();
        }
    }
}
