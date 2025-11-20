package com.example.study.controller.dto;

import com.example.study.RefundStatus;
import com.example.study.entity.Refund;
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
public class RefundResponse {

    Long refundId;
    Long purchaseId;
    RefundStatus status;
    String reason;
    LocalDateTime refundedAt;

    public static RefundResponse fromEntity(Refund refund) {
        return RefundResponse.builder()
                .refundId(refund.getId())
                .purchaseId(refund.getPurchase().getId())
                .status(refund.getStatus())
                .reason(refund.getReason())
                .refundedAt(refund.getCreatedAt())
                .build();
    }
}
