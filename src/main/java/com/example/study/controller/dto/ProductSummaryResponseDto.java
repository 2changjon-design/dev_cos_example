package com.example.study.controller.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSummaryResponseDto {
    Long productId;
    String productName;
    String categoryName;
    BigDecimal price;
    Integer stock;
    LocalDateTime createdAt;
}
