package com.example.study.controller.dto;

import com.example.study.PurchaseStatus;
import com.example.study.service.dto.OrderResultDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderResponse {
    Long orderId;
    Long userId;
    Long productId;
    Integer quantity;
    BigDecimal unitPrice;
    BigDecimal totalPrice;
    PurchaseStatus status;
    LocalDateTime orderedAt;

    public static OrderResponse from(OrderResultDto resultDto) {
        return OrderResponse.builder()
                .orderId(resultDto.getOrderId())
                .userId(resultDto.getUserId())
                .productId(resultDto.getProductId())
                .quantity(resultDto.getQuantity())
                .unitPrice(resultDto.getUnitPrice())
                .totalPrice(resultDto.getTotalPrice())
                .status(resultDto.getStatus())
                .orderedAt(resultDto.getPurchasedAt())
                .build();
    }
}

