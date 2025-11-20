package com.example.study.lecture.cleancode.spring.answer.product;

import java.math.BigDecimal;

public record UpdateProductPriceRequest(BigDecimal newPrice) {

    public void validate() {
        if (newPrice == null || newPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("가격은 0보다 커야 합니다.");
        }
    }
}
