package com.example.study.mapper;

import com.example.study.controller.dto.ProductRequestDto;
import com.example.study.controller.dto.ProductResponseDto;
import com.example.study.controller.dto.ProductSummaryResponseDto;
import com.example.study.entity.Product;
import com.example.study.repository.projection.ProductSummaryDto;
import com.example.study.service.dto.ProductServiceInputDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = false))
public interface ProductMapper {

    @Mapping(target = "description", expression = "java(normalizeDescription(req.getDescription()))")
    ProductServiceInputDto toService(ProductRequestDto req);

    @Mapping(target = "categoryId", expression = "java(extractCategoryId(product))")
    @Mapping(target = "stockStatus", expression = "java(toStockStatus(product.getStock()))")
    @Mapping(target = "displayName", ignore = true)
    ProductResponseDto toResponse(Product product);

    List<ProductResponseDto> toResponseList(List<Product> products);

    ProductSummaryResponseDto toSummaryResponse(ProductSummaryDto dto);

    List<ProductSummaryResponseDto> toSummaryResponseList(List<ProductSummaryDto> dtos);

    default String normalizeDescription(String description) {
        if (description == null) {
            return null;
        }
        String trimmed = description.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    default Long extractCategoryId(Product product) {
        return product != null && product.getCategory() != null
                ? product.getCategory().getId()
                : null;
    }

    default String toStockStatus(Integer stock) {
        if (stock == null) {
            return "UNKNOWN";
        }
        return stock > 0 ? "AVAILABLE" : "SOLD_OUT";
    }

    @AfterMapping
    default void applyResponseDefaults(Product product,
                                       @MappingTarget ProductResponseDto.ProductResponseDtoBuilder builder) {
        if (product == null) {
            builder.displayName("정보 미확인 상품");
            builder.description("상품 정보가 존재하지 않습니다.");
            return;
        }

        if (product.getDescription() == null || product.getDescription().isBlank()) {
            builder.description("상품 설명이 준비 중입니다.");
        }

        String name = product.getName() == null ? "이름 미정 상품" : product.getName();
        Long productId = product.getId();
        builder.displayName(productId != null ? "[" + productId + "] " + name : name);
    }
}
