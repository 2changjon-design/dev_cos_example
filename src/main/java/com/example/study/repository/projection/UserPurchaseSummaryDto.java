package com.example.study.repository.projection;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class UserPurchaseSummaryDto {

    private final Long userId;
    private final String username;
    private final String email;
    private final Long orderCount;
    private final BigDecimal totalAmount;
    private final LocalDateTime lastOrderAt;

    @QueryProjection
    public UserPurchaseSummaryDto(Long userId,
                                  String username,
                                  String email,
                                  Long orderCount,
                                  BigDecimal totalAmount,
                                  LocalDateTime lastOrderAt) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.orderCount = orderCount == null ? 0L : orderCount;
        this.totalAmount = totalAmount == null ? BigDecimal.ZERO : totalAmount;
        this.lastOrderAt = lastOrderAt;
    }
}
