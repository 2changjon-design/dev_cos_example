package com.example.study.repository.projection;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

/**
 * 실습: 카테고리별 주문 건수 집계 DTO.
 */
@Getter
public class CategoryOrderCountDto {

    private final String categoryName;
    private final Long orderCount;

    @QueryProjection
    public CategoryOrderCountDto(String categoryName, Long orderCount) {
        this.categoryName = categoryName;
        this.orderCount = orderCount == null ? 0L : orderCount;
    }
}
