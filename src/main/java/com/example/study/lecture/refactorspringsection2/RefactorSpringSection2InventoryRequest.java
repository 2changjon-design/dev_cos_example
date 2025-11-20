package com.example.study.lecture.refactorspringsection2;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RefactorSpringSection2InventoryRequest(
        @NotNull Long productId,
        int adjustmentQuantity,
        @NotBlank String reason
) {
}
