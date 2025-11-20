package com.example.study.lecture.cleancode.spring.answer2.report;

import java.math.BigDecimal;
import java.util.List;

public record AnalyticsReportResponse(
        int count,
        BigDecimal totalAmount,
        BigDecimal averageAmount,
        List<PurchaseSummaryResponse> items
) {
}
