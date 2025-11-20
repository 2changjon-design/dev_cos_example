package com.example.study.lecture.cleancode.practice;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * 데이터 변환/출력 책임이 뒤섞인 리포팅 예제.
 * - 문자열 연결이 이곳저곳 흩어져 있음
 * - 환불 합계 계산과 출력 포맷 로직이 한 메서드에 섞여 있음
 *
 * TODO(LAB):
 *  1) 합계/건수 계산을 전용 객체나 메서드로 추출하세요.
 *  2) 헤더/본문/요약 렌더링을 별도 메서드로 나누세요.
 *  3) includeSummary 플래그에 따른 분기 처리를 더 읽기 쉽게 정리하세요.
 */
public class BulkRefundReporterPractice {

    public String generateReport(List<RefundRecord> records, boolean includeSummary) {
        if (records == null || records.isEmpty()) {
            return "EMPTY";
        }

        StringBuilder builder = new StringBuilder();
        builder.append("== REFUND REPORT ==\n");
        builder.append("createdAt=").append(Instant.now()).append("\n");

        BigDecimal total = BigDecimal.ZERO;
        int index = 1;
        for (RefundRecord record : records) {
            builder.append(index++).append(". ");
            builder.append(record.userName()).append(" / ");
            builder.append(record.status()).append(" / ");
            builder.append(record.amount()).append("\n");
            total = total.add(record.amount());
        }

        if (includeSummary) {
            builder.append("----\n");
            builder.append("count=").append(records.size()).append("\n");
            builder.append("total=").append(total).append("\n");
        }

        return builder.toString();
    }

    public record RefundRecord(String userName, String status, BigDecimal amount) {
    }
}
