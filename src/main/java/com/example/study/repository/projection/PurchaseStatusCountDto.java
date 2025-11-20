package com.example.study.repository.projection;

import com.example.study.PurchaseStatus;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class PurchaseStatusCountDto {

    private final PurchaseStatus status;
    private final Long count;

    @QueryProjection
    public PurchaseStatusCountDto(PurchaseStatus status, Long count) {
        this.status = status;
        this.count = count == null ? 0L : count;
    }
}
