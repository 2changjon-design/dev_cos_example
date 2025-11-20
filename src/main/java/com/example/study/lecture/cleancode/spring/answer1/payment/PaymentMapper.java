package com.example.study.lecture.cleancode.spring.answer1.payment;

import com.example.study.entity.Purchase;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class PaymentMapper {

    public PaymentGatewayRequest toGatewayRequest(Purchase purchase, PaymentRequest request) {
        return new PaymentGatewayRequest(
                purchase.getId(),
                purchase.getTotalPrice(),
                mask(request.cardNo()),
                request.installment() == null ? 1 : request.installment(),
                LocalDateTime.now()
        );
    }

    public PaymentNotification toNotification(Purchase purchase, PaymentGatewayResponse response) {
        return new PaymentNotification(
                purchase.getId(),
                purchase.getUser().getId(),
                response.transactionId(),
                LocalDateTime.now()
        );
    }

    public PaymentResponse toResponse(Purchase purchase, PaymentGatewayResponse response) {
        return new PaymentResponse(
                purchase.getId(),
                purchase.getTotalPrice(),
                response.transactionId(),
                purchase.getStatus().name(),
                LocalDateTime.now()
        );
    }

    private String mask(String cardNo) {
        if (cardNo == null || cardNo.length() < 4) {
            return "****";
        }
        return "****-****-****-" + cardNo.substring(cardNo.length() - 4);
    }
}
