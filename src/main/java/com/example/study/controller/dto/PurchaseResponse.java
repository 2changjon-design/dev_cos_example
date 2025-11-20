package com.example.study.controller.dto;

import com.example.study.PurchaseStatus;
import com.example.study.entity.Purchase;
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
public class PurchaseResponse {

    Long purchaseId;
    Long userId;
    Long productId;
    Integer quantity;
    BigDecimal unitPrice;
    BigDecimal totalPrice;
    PurchaseStatus status;
    LocalDateTime purchasedAt;

    public static PurchaseResponse fromEntity(Purchase purchase) {
        return PurchaseResponse.builder()
                .purchaseId(purchase.getId())
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
