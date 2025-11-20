package com.example.study.repository.projection;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * 실습: 카테고리별 상품 조회 DTO.
 */
@Getter
public class CategoryProductDto {

    private final String categoryName;
    private final String productName;
    private final BigDecimal price;
    private final Integer stock;

    @QueryProjection
    public CategoryProductDto(String categoryName,
                              String productName,
                              BigDecimal price,
                              Integer stock) {
        this.categoryName = categoryName;
        this.productName = productName;
        this.price = price;
        this.stock = stock;
    }
}
