package com.example.study.lecture.cleancode.studentpractice;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 학생 전용 과제 3.
 * - 계산/포맷/필터링이 모두 한 메서드에 존재
 * - 조건 분기가 복잡하며 테스트하기 어렵다.
 *
 * TODO(STUDENT):
 *  1) 계산 로직(총액, 세금, 할인)과 문자열 포맷을 분리하세요.
 *  2) `type`에 따라 다른 규칙을 적용하는 부분을 전략/enum 등으로 정리하세요.
 *  3) 날짜/금액 포맷을 전역 상수 혹은 Formatter로 분리해 재사용성을 높이세요.
 */
public class InvoiceSummaryFormatterPractice {

    public String summarize(List<InvoiceLine> lines, String type, LocalDate date) {
        if (lines == null || lines.isEmpty()) {
            return "EMPTY";
        }

        BigDecimal total = BigDecimal.ZERO;
        BigDecimal tax = BigDecimal.ZERO;
        StringBuilder builder = new StringBuilder();
        builder.append("INVOICE TYPE=").append(type).append("\n");
        builder.append("DATE=").append(date).append("\n");
        for (InvoiceLine line : lines) {
            builder.append(line.description()).append(":").append(line.amount()).append("\n");
            total = total.add(line.amount());
            tax = tax.add(line.amount().multiply(BigDecimal.valueOf(0.1)));
        }

        if ("EXPORT".equals(type)) {
            tax = BigDecimal.ZERO;
        } else if ("INTERNAL".equals(type)) {
            total = total.subtract(BigDecimal.valueOf(5));
        }

        builder.append("TOTAL=").append(total).append("\n");
        builder.append("TAX=").append(tax).append("\n");
        builder.append("GRAND=").append(total.add(tax)).append("\n");
        return builder.toString();
    }

    public record InvoiceLine(String description, BigDecimal amount) {
    }
}
