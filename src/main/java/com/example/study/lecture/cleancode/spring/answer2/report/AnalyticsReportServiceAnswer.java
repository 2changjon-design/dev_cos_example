package com.example.study.lecture.cleancode.spring.answer2.report;

import com.example.study.repository.PurchaseQueryRepository;
import com.example.study.repository.projection.PurchaseDetailDto;
import com.example.study.service.dto.PurchaseSearchCondition;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AnalyticsReportServiceAnswer {

    private final PurchaseQueryRepository purchaseQueryRepository;
    private final PurchaseReportMapper reportMapper;

    public AnalyticsReportServiceAnswer(
            PurchaseQueryRepository purchaseQueryRepository,
            PurchaseReportMapper reportMapper
    ) {
        this.purchaseQueryRepository = purchaseQueryRepository;
        this.reportMapper = reportMapper;
    }

    @Transactional(readOnly = true)
    public AnalyticsReportResponse getReport(PurchaseReportRequest request) {
        PurchaseSearchCondition condition = PurchaseSearchCondition.builder()
                .status(request.status())
                .purchasedAfter(request.from())
                .purchasedBefore(request.to())
                .build();

        List<PurchaseDetailDto> details = purchaseQueryRepository.searchPurchaseDetails(condition);
        List<PurchaseDetailDto> latest = details.stream()
                .limit(request.limit())
                .toList();

        PurchaseStatistics statistics = PurchaseStatistics.from(latest);
        List<PurchaseSummaryResponse> items = latest.stream()
                .map(reportMapper::toSummaryResponse)
                .toList();

        return new AnalyticsReportResponse(
                statistics.count(),
                statistics.totalAmount(),
                statistics.averageAmount(),
                items
        );
    }
}
