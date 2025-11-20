package com.example.study.service.dto;

import com.example.study.RefundStatus;
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
public class RefundSearchCondition {
    Long purchaseId;
    Long userId;
    RefundStatus status;
    LocalDateTime createdAfter;
    LocalDateTime createdBefore;
}
