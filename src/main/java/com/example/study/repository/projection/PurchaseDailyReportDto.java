package com.example.study.repository.projection;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
public class PurchaseDailyReportDto {

    private final LocalDate orderDate;
    private final Long orderCount;
    private final BigDecimal totalRevenue;

    @QueryProjection
    public PurchaseDailyReportDto(LocalDate orderDate,
                                  Long orderCount,
                                  BigDecimal totalRevenue) {
        this.orderDate = orderDate;
        this.orderCount = orderCount == null ? 0L : orderCount;
        this.totalRevenue = totalRevenue == null ? BigDecimal.ZERO : totalRevenue;
    }
}
