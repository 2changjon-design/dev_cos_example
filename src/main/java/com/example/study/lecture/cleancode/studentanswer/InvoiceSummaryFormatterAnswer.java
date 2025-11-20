package com.example.study.lecture.cleancode.studentanswer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class InvoiceSummaryFormatterAnswer {

    public String summarize(List<InvoiceLine> lines, InvoiceType type, LocalDate date) {
        if (lines == null || lines.isEmpty()) {
            return "EMPTY";
        }

        InvoiceAmount amount = InvoiceAmount.from(lines, type);
        return new StringBuilder()
                .append(header(type, date))
                .append(renderLines(lines))
                .append(renderTotals(amount))
                .toString();
    }

    private String header(InvoiceType type, LocalDate date) {
        return "INVOICE TYPE=" + type.name() + "\nDATE=" + date + "\n";
    }

    private String renderLines(List<InvoiceLine> lines) {
        StringBuilder builder = new StringBuilder();
        for (InvoiceLine line : lines) {
            builder.append(line.description())
                    .append(":")
                    .append(line.amount())
                    .append("\n");
        }
        return builder.toString();
    }

    private String renderTotals(InvoiceAmount amount) {
        return """
                TOTAL=%s
                TAX=%s
                GRAND=%s
                """.formatted(
                amount.total().toPlainString(),
                amount.tax().toPlainString(),
                amount.grandTotal().toPlainString()
        );
    }

    public enum InvoiceType {
        EXPORT, INTERNAL, DEFAULT;

        public boolean isTaxExempt() {
            return this == EXPORT;
        }

        public BigDecimal adjustment() {
            return this == INTERNAL ? BigDecimal.valueOf(-5) : BigDecimal.ZERO;
        }
    }

    public record InvoiceAmount(BigDecimal total, BigDecimal tax) {

        public static InvoiceAmount from(List<InvoiceLine> lines, InvoiceType type) {
            BigDecimal subtotal = lines.stream()
                    .map(InvoiceLine::amount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .add(type.adjustment());
            BigDecimal tax = type.isTaxExempt()
                    ? BigDecimal.ZERO
                    : subtotal.multiply(BigDecimal.valueOf(0.1));
            return new InvoiceAmount(subtotal, tax);
        }

        public BigDecimal grandTotal() {
            return total.add(tax);
        }
    }

    public record InvoiceLine(String description, BigDecimal amount) {
    }
}
