package com.example.study.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDto {

    @NotNull(message = "카테고리 ID는 필수입니다.")
    Long categoryId;

    @NotBlank(message = "상품명은 필수입니다.")
    String name;

    String description;

    @NotNull(message = "가격은 필수입니다")
    @Positive(message = "가격이 양수여야 합니다")
    BigDecimal price;

    @NotNull(message = "재고는 필수입니다")
    @PositiveOrZero(message = "재고는 0 이상이어야 합니다")
    Integer stock;
}
