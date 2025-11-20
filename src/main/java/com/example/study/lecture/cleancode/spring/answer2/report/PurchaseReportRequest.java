package com.example.study.lecture.cleancode.spring.answer2.report;

import com.example.study.PurchaseStatus;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public record PurchaseReportRequest(
        PurchaseStatus status,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
        Integer limit
) {

    public PurchaseReportRequest {
        if (limit == null || limit <= 0) {
            limit = 5;
        }
    }
}
