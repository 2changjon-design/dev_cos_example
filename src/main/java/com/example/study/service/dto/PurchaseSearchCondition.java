package com.example.study.service.dto;

import com.example.study.PurchaseStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PurchaseSearchCondition {
    Long userId;
    Long productId;
    PurchaseStatus status;
    LocalDateTime purchasedAfter;
    LocalDateTime purchasedBefore;
    Integer minQuantity;
    Integer maxQuantity;
}
