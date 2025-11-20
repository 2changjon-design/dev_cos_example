package com.example.study.repository.projection;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class ProductSalesSummaryDto {

    private final Long productId;
    private final String productName;
    private final Long totalQuantity;
    private final BigDecimal totalRevenue;

    @QueryProjection
    public ProductSalesSummaryDto(Long productId,
                                  String productName,
                                  Long totalQuantity,
                                  BigDecimal totalRevenue) {
        this.productId = productId;
        this.productName = productName;
        this.totalQuantity = totalQuantity == null ? 0L : totalQuantity;
        this.totalRevenue = totalRevenue == null ? BigDecimal.ZERO : totalRevenue;
    }
}
