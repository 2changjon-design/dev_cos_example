package com.example.study.lecture.cleancode.spring.answer.refund;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/spring/answer/refunds")
public class RefundControllerAnswer {

    private final RefundServiceAnswer refundService;

    public RefundControllerAnswer(RefundServiceAnswer refundService) {
        this.refundService = refundService;
    }

    @PostMapping("/{purchaseId}")
    public ResponseEntity<RefundResponse> refund(
            @PathVariable Long purchaseId,
            @RequestBody RefundRequest request
    ) {
        return ResponseEntity.ok(refundService.processRefund(purchaseId, request));
    }
}
