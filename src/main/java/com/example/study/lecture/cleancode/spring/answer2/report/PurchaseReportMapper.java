package com.example.study.lecture.cleancode.spring.answer2.report;

import com.example.study.repository.projection.PurchaseDetailDto;
import org.springframework.stereotype.Component;

@Component
public class PurchaseReportMapper {

    public PurchaseSummaryResponse toSummaryResponse(PurchaseDetailDto dto) {
        return new PurchaseSummaryResponse(
                dto.getPurchaseId(),
                dto.getUserId(),
                dto.getProductId(),
                dto.getStatus().name(),
                dto.getTotalPrice(),
                dto.getPurchasedAt()
        );
    }
}
