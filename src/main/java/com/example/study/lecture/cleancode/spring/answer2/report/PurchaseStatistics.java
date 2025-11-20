package com.example.study.lecture.cleancode.spring.answer2.report;

import com.example.study.repository.projection.PurchaseDetailDto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public record PurchaseStatistics(int count, BigDecimal totalAmount, BigDecimal averageAmount) {

    public static PurchaseStatistics from(List<PurchaseDetailDto> details) {
        int count = details.size();
        BigDecimal total = details.stream()
                .map(PurchaseDetailDto::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal average = count == 0
                ? BigDecimal.ZERO
                : total.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
        return new PurchaseStatistics(count, total, average);
    }
}
