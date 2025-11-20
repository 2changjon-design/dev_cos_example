package com.example.study.lecture.cleancode.spring.answer.product;

import com.example.study.entity.Product;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ProductMapperAnswer {

    public Product toEntity(CreateProductRequest request) {
        return Product.builder()
                .category(request.category())
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .stock(request.stock())
                .build();
    }

    public ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock()
        );
    }
}
