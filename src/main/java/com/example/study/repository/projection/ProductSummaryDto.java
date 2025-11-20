package com.example.study.repository.projection;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class ProductSummaryDto {

    private final Long productId;
    private final String productName;
    private final String categoryName;
    private final BigDecimal price;
    private final Integer stock;
    private final LocalDateTime createdAt;

    @QueryProjection
    public ProductSummaryDto(Long productId,
                             String productName,
                             String categoryName,
                             BigDecimal price,
                             Integer stock,
                             LocalDateTime createdAt) {
        this.productId = productId;
        this.productName = productName;
        this.categoryName = categoryName;
        this.price = price;
        this.stock = stock;
        this.createdAt = createdAt;
    }
}
