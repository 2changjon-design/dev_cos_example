package com.example.study.repository.projection;

import com.example.study.RefundStatus;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class RefundDetailDto {

    private final Long refundId;
    private final Long purchaseId;
    private final Long userId;
    private final String username;
    private final Long productId;
    private final String productName;
    private final RefundStatus status;
    private final String reason;
    private final LocalDateTime createdAt;

    @QueryProjection
    public RefundDetailDto(Long refundId,
                           Long purchaseId,
                           Long userId,
                           String username,
                           Long productId,
                           String productName,
                           RefundStatus status,
                           String reason,
                           LocalDateTime createdAt) {
        this.refundId = refundId;
        this.purchaseId = purchaseId;
        this.userId = userId;
        this.username = username;
        this.productId = productId;
        this.productName = productName;
        this.status = status;
        this.reason = reason;
        this.createdAt = createdAt;
    }
}
