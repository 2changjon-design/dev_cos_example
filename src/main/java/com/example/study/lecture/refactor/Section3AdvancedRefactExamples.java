package com.example.study.lecture.refactor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * SECTION 3 Advanced: IntelliJ 리팩토링 도구를 심화해서 다뤄볼 수 있는 예제 모음.
 * - 대량의 변수 Rename, Extract Method, Introduce Parameter Object 등을 연습한다.
 */
public final class Section3AdvancedRefactExamples {

    private Section3AdvancedRefactExamples() {
        throw new IllegalStateException("Utility class");
    }

    // ============================================================
    // ADVANCED EXAMPLE 1: 월간 리포트 생성기
    // ============================================================

    /**
     * SECTION 3 ADVANCED - BEFORE:
     * - 변수 이름이 모호하고, 복잡한 데이터 집계가 한 메서드에 몰려 있다.
     * - Rename, Extract Method, Introduce Local Extension 연습용.
     */
    public static class MonthlyReportGeneratorBefore {

        public MonthlyReport generate(List<SalesRecord> records, LocalDate start, LocalDate end) {
            List<SalesRecord> filtered = new ArrayList<>();
            for (SalesRecord r : records) {
                if (!r.date().isBefore(start) && !r.date().isAfter(end)) {
                    filtered.add(r);
                }
            }

            BigDecimal t = BigDecimal.ZERO;
            Map<String, BigDecimal> categoryTotals = new java.util.HashMap<>();
            for (SalesRecord f : filtered) {
                BigDecimal amount = f.unitPrice().multiply(BigDecimal.valueOf(f.quantity()));
                t = t.add(amount);
                categoryTotals.merge(f.category(), amount, BigDecimal::add);
            }

            StringBuilder sb = new StringBuilder();
            sb.append("Report Period: ").append(start).append(" ~ ").append(end).append("\n");
            for (Map.Entry<String, BigDecimal> entry : categoryTotals.entrySet()) {
                sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
            sb.append("Total: ").append(t);

            return new MonthlyReport(sb.toString(), t, categoryTotals);
        }
    }

    /**
     * SECTION 3 ADVANCED - AFTER Placeholder:
     * TODO: 필터링/합계 계산/문자열 생성 메서드를 추출하고, 변수명을 명확히 바꿔보자.
     */
    public static class MonthlyReportGeneratorAfter {
        // 학습자가 IDE 리팩토링 도구로 직접 구현.
    }

    // ============================================================
    // ADVANCED EXAMPLE 2: 요율 테이블 빌더
    // ============================================================

    /**
     * SECTION 3 ADVANCED - BEFORE:
     * - 상수, 반복문, switch 문이 얽혀 있어 리팩토링이 어려운 구조.
     * - Extract Constant, Replace Temp with Query, Rename을 연습한다.
     */
    public static class RateTableBuilderBefore {

        public List<RateRow> build(List<CustomerTier> tiers) {
            List<RateRow> rows = new ArrayList<>();
            for (CustomerTier tier : tiers) {
                BigDecimal rate;
                switch (tier.type()) {
                    case "VIP" -> rate = BigDecimal.valueOf(0.08);
                    case "GOLD" -> rate = BigDecimal.valueOf(0.1);
                    case "SILVER" -> rate = BigDecimal.valueOf(0.13);
                    default -> rate = BigDecimal.valueOf(0.15);
                }

                BigDecimal maxDiscount = BigDecimal.ZERO;
                if (tier.monthlySpend().compareTo(BigDecimal.valueOf(1_000_000)) > 0) {
                    maxDiscount = BigDecimal.valueOf(50_000);
                } else if (tier.monthlySpend().compareTo(BigDecimal.valueOf(500_000)) > 0) {
                    maxDiscount = BigDecimal.valueOf(20_000);
                }

                rows.add(new RateRow(tier.type(), rate, maxDiscount));
            }
            return rows;
        }
    }

    /**
     * SECTION 3 ADVANCED - AFTER Placeholder:
     * TODO: 요율/할인 계산을 별도 메서드로 추출하고, 상수 이름을 명확히 정의해보자.
     */
    public static class RateTableBuilderAfter {
        // 학습자가 직접 작성.
    }

    // ============================================================
    // SUPPORTING TYPES
    // ============================================================

    public record SalesRecord(String category, BigDecimal unitPrice, int quantity, LocalDate date) {
    }

    public record MonthlyReport(String contents, BigDecimal total, Map<String, BigDecimal> categoryTotals) {
    }

    public record CustomerTier(String type, BigDecimal monthlySpend) {
    }

    public record RateRow(String tierType, BigDecimal rate, BigDecimal maxDiscount) {
    }
}
