package com.example.study.repository.projection;

import com.example.study.RefundStatus;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class RefundStatusCountDto {

    private final RefundStatus status;
    private final Long count;

    @QueryProjection
    public RefundStatusCountDto(RefundStatus status, Long count) {
        this.status = status;
        this.count = count == null ? 0L : count;
    }
}
