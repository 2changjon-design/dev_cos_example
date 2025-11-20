package com.example.study.lecture.refactorspringsection3;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record RefactorSpringSection3ProductRequest(
        @NotBlank String name,
        @NotNull @Min(0) BigDecimal price
) {
}
