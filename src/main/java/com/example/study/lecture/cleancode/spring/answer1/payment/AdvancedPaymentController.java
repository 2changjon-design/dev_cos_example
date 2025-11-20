package com.example.study.lecture.cleancode.spring.answer1.payment;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/spring/answer1/payments")
public class AdvancedPaymentController {

    private final AdvancedPaymentService paymentService;

    public AdvancedPaymentController(AdvancedPaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/{purchaseId}/settle")
    public ResponseEntity<PaymentResponse> settle(
            @PathVariable Long purchaseId,
            @RequestBody PaymentRequest request
    ) {
        return ResponseEntity.ok(paymentService.settle(purchaseId, request));
    }
}
