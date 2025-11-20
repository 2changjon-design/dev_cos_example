package com.example.study.lecture.refactorspringsection1;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record RefactorSection1OrderRequest(
        @NotNull Long userId,
        @NotNull List<OrderLineRequest> lines
) {
    public record OrderLineRequest(
            @NotNull Long productId,
            @Min(1) int quantity,
            @NotNull BigDecimal unitPrice
    ) {
    }
}
