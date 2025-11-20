package com.example.study.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductServiceInputDto {
    Long categoryId;
    String name;
    String description;
    BigDecimal price;
    Integer stock;
}
