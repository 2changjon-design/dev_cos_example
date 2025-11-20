package com.example.study.service.dto;

import com.example.study.PurchaseStatus;
import com.example.study.entity.Purchase;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderResultDto {
    Long orderId;
    Long userId;
    Long productId;
    Integer quantity;
    BigDecimal unitPrice;
    BigDecimal totalPrice;
    PurchaseStatus status;
    LocalDateTime purchasedAt;

    public static OrderResultDto fromEntity(Purchase purchase) {
        return OrderResultDto.builder()
                .orderId(purchase.getId())
                .userId(purchase.getUser().getId())
                .productId(purchase.getProduct().getId())
                .quantity(purchase.getQuantity())
                .unitPrice(purchase.getUnitPrice())
                .totalPrice(purchase.getTotalPrice())
                .status(purchase.getStatus())
                .purchasedAt(purchase.getPurchasedAt())
                .build();
    }
}

