package com.example.study.service.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductSearchCondition {
    String name;
    BigDecimal minPrice;
    BigDecimal maxPrice;
    Long categoryId;
    Integer minStock;
    Integer maxStock;
}
