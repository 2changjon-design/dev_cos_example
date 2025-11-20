package com.example.study.lecture.cleancode.spring.answer.product;

import com.example.study.entity.Category;

import java.math.BigDecimal;

public record CreateProductRequest(
        Category category,
        String name,
        String description,
        BigDecimal price,
        Integer stock
) {

    public void validate() {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("상품명은 필수입니다.");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("가격은 0보다 커야 합니다.");
        }
        if (stock == null || stock < 0) {
            throw new IllegalArgumentException("재고는 0 이상이어야 합니다.");
        }
    }
}
