package com.example.study.repository.projection;

import com.example.study.PurchaseStatus;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class PurchaseDetailDto {

    private final Long purchaseId;
    private final Long userId;
    private final String username;
    private final Long productId;
    private final String productName;
    private final Integer quantity;
    private final BigDecimal unitPrice;
    private final BigDecimal totalPrice;
    private final PurchaseStatus status;
    private final LocalDateTime purchasedAt;

    @QueryProjection
    public PurchaseDetailDto(Long purchaseId,
                             Long userId,
                             String username,
                             Long productId,
                             String productName,
                             Integer quantity,
                             BigDecimal unitPrice,
                             BigDecimal totalPrice,
                             PurchaseStatus status,
                             LocalDateTime purchasedAt) {
        this.purchaseId = purchaseId;
        this.userId = userId;
        this.username = username;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
        this.status = status;
        this.purchasedAt = purchasedAt;
    }
}
