package com.example.study.lecture.cleancode.answer;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * 계산/포맷 책임을 분리해 테스트와 확장을 쉽게 만든 리포터.
 * Report 생성 흐름(헤더 → 본문 → 요약)을 고정해두면
 * 개별 단계만 교체하거나 확장하면서 안정적으로 리포트를 개선할 수 있다.
 */
public class BulkRefundReporterAnswer {

    /**
     * 컨트롤 플로 전체를 여기서만 다루고, 계산은 {@link RefundStatistics}에 위임한다.
     * 이렇게 하면 호출부는 옵션만 넘기고, 메서드는 "리포트를 어느 순서로 조합하는가"에 집중할 수 있다.
     */
    public String generateReport(List<RefundRecord> records, boolean includeSummary) {
        if (records == null || records.isEmpty()) {
            return "EMPTY";
        }

        RefundStatistics stats = RefundStatistics.from(records);
        StringBuilder builder = new StringBuilder();
        builder.append(header());
        builder.append(renderLines(records));
        if (includeSummary) {
            builder.append(renderSummary(stats));
        }
        return builder.toString();
    }

    /**
     * 헤더 생성을 메서드로 빼면 테스트 시점을 주입하거나 포맷을 재사용하기 쉽다.
     */
    private String header() {
        return "== REFUND REPORT ==\ncreatedAt=" + Instant.now() + "\n";
    }

    /**
     * 라인 렌더링을 분리해두면 정렬, 포맷 변경 등의 요구사항을 다른 로직과 섞지 않고 처리할 수 있다.
     */
    private String renderLines(List<RefundRecord> records) {
        StringBuilder builder = new StringBuilder();
        int index = 1;
        for (RefundRecord record : records) {
            builder.append(index++)
                    .append(". ")
                    .append(record.userName())
                    .append(" / ")
                    .append(record.status())
                    .append(" / ")
                    .append(record.amount())
                    .append("\n");
        }
        return builder.toString();
    }

    /**
     * 집계 결과가 있을 때만 요약을 추가해 UI/비즈니스 옵션을 쉽게 조절하도록 한다.
     */
    private String renderSummary(RefundStatistics stats) {
        return "----\ncount=" + stats.count() + "\ntotal=" + stats.totalAmount() + "\n";
    }

    /**
     * String 대신 record를 사용해 필드 의미를 명확히 하고, 표현 계층과 도메인 모델을 분리했다.
     */
    public record RefundRecord(String userName, String status, BigDecimal amount) {
    }

    /**
     * 통계 계산 전용 타입으로 만들면 누적 로직을 재사용할 수 있고, 테스트도 간단해진다.
     */
    public record RefundStatistics(int count, BigDecimal totalAmount) {

        public static RefundStatistics from(List<RefundRecord> records) {
            BigDecimal total = records.stream()
                    .map(RefundRecord::amount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            return new RefundStatistics(records.size(), total);
        }
    }
}
