package com.example.study.lecture.refactor;

import com.example.study.lecture.refactor.Section3AdvancedRefactExamples.CustomerTier;
import com.example.study.lecture.refactor.Section3AdvancedRefactExamples.MonthlyReport;
import com.example.study.lecture.refactor.Section3AdvancedRefactExamples.RateRow;
import com.example.study.lecture.refactor.Section3AdvancedRefactExamples.SalesRecord;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Section 3 Advanced 실습 정답 모음.
 */
public final class Section3AdvancedRefactSolution {

    private Section3AdvancedRefactSolution() {
        throw new IllegalStateException("Utility class");
    }

    // ============================================================
    // ADVANCED EXAMPLE 1 - SOLUTION
    // ============================================================
    public static class MonthlyReportGeneratorAfter {

        public MonthlyReport generate(List<SalesRecord> records, LocalDate start, LocalDate end) {
            List<SalesRecord> filteredRecords = filterRecords(records, start, end);
            BigDecimal totalSales = calculateTotalSales(filteredRecords);
            Map<String, BigDecimal> categoryTotals = aggregateCategoryTotals(filteredRecords);
            String reportContents = buildReportContents(start, end, categoryTotals, totalSales);
            return new MonthlyReport(reportContents, totalSales, categoryTotals);
        }

        private List<SalesRecord> filterRecords(List<SalesRecord> records, LocalDate start, LocalDate end) {
            List<SalesRecord> filtered = new ArrayList<>();
            for (SalesRecord record : records) {
                if (!record.date().isBefore(start) && !record.date().isAfter(end)) {
                    filtered.add(record);
                }
            }
            return filtered;
        }

        private BigDecimal calculateTotalSales(List<SalesRecord> records) {
            return records.stream()
                    .map(this::lineTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        private Map<String, BigDecimal> aggregateCategoryTotals(List<SalesRecord> records) {
            Map<String, BigDecimal> totals = new HashMap<>();
            for (SalesRecord record : records) {
                totals.merge(record.category(), lineTotal(record), BigDecimal::add);
            }
            return totals;
        }

        private BigDecimal lineTotal(SalesRecord record) {
            return record.unitPrice().multiply(BigDecimal.valueOf(record.quantity()));
        }

        private String buildReportContents(
                LocalDate start,
                LocalDate end,
                Map<String, BigDecimal> categoryTotals,
                BigDecimal totalSales
        ) {
            StringBuilder builder = new StringBuilder();
            builder.append("Report Period: ").append(start).append(" ~ ").append(end).append("\n");
            categoryTotals.forEach((category, amount) ->
                    builder.append(category).append(": ").append(amount).append("\n")
            );
            builder.append("Total: ").append(totalSales);
            return builder.toString();
        }
    }

    // ============================================================
    // ADVANCED EXAMPLE 2 - SOLUTION
    // ============================================================
    public static class RateTableBuilderAfter {
        private static final BigDecimal VIP_RATE = BigDecimal.valueOf(0.08);
        private static final BigDecimal GOLD_RATE = BigDecimal.valueOf(0.10);
        private static final BigDecimal SILVER_RATE = BigDecimal.valueOf(0.13);
        private static final BigDecimal DEFAULT_RATE = BigDecimal.valueOf(0.15);

        private static final BigDecimal HIGH_SPEND_THRESHOLD = BigDecimal.valueOf(1_000_000);
        private static final BigDecimal MID_SPEND_THRESHOLD = BigDecimal.valueOf(500_000);
        private static final BigDecimal HIGH_SPEND_DISCOUNT = BigDecimal.valueOf(50_000);
        private static final BigDecimal MID_SPEND_DISCOUNT = BigDecimal.valueOf(20_000);

        public List<RateRow> build(List<CustomerTier> tiers) {
            List<RateRow> rows = new ArrayList<>();
            for (CustomerTier tier : tiers) {
                rows.add(buildRateRow(tier));
            }
            return rows;
        }

        private RateRow buildRateRow(CustomerTier tier) {
            BigDecimal rate = determineRate(tier.type());
            BigDecimal maxDiscount = determineMaxDiscount(tier.monthlySpend());
            return new RateRow(tier.type(), rate, maxDiscount);
        }

        private BigDecimal determineRate(String type) {
            return switch (type) {
                case "VIP" -> VIP_RATE;
                case "GOLD" -> GOLD_RATE;
                case "SILVER" -> SILVER_RATE;
                default -> DEFAULT_RATE;
            };
        }

        private BigDecimal determineMaxDiscount(BigDecimal monthlySpend) {
            if (monthlySpend.compareTo(HIGH_SPEND_THRESHOLD) > 0) {
                return HIGH_SPEND_DISCOUNT;
            }
            if (monthlySpend.compareTo(MID_SPEND_THRESHOLD) > 0) {
                return MID_SPEND_DISCOUNT;
            }
            return BigDecimal.ZERO;
        }
    }
}
