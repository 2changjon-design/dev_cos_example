package com.example.study.lecture.cleancode.spring.answer2.report;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/spring/answer2/reports")
public class AnalyticsReportControllerAnswer {

    private final AnalyticsReportServiceAnswer reportService;

    public AnalyticsReportControllerAnswer(AnalyticsReportServiceAnswer reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/purchases")
    public ResponseEntity<AnalyticsReportResponse> purchaseReport(PurchaseReportRequest request) {
        return ResponseEntity.ok(reportService.getReport(request));
    }
}
