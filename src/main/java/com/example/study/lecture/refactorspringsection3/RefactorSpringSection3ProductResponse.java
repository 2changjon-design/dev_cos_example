package com.example.study.lecture.refactorspringsection3;

import java.math.BigDecimal;

public record RefactorSpringSection3ProductResponse(
        Long id,
        String name,
        BigDecimal price,
        boolean active
) {
    public static RefactorSpringSection3ProductResponse from(RefactorSpringSection3Product product) {
        return new RefactorSpringSection3ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.isActive()
        );
    }
}
