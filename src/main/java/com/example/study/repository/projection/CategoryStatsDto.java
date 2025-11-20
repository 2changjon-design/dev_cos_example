package com.example.study.repository.projection;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class CategoryStatsDto {

    private final Long categoryId;
    private final String categoryName;
    private final Long productCount;
    private final Long childCount;

    @QueryProjection
    public CategoryStatsDto(Long categoryId,
                            String categoryName,
                            Long productCount,
                            Long childCount) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.productCount = productCount == null ? 0L : productCount;
        this.childCount = childCount == null ? 0L : childCount;
    }
}
