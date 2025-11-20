package com.example.study.lecture.cleancode.spring.answer1.payment;

import com.example.study.entity.Purchase;
import com.example.study.repository.PurchaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdvancedPaymentService {

    private final PurchaseRepository purchaseRepository;
    private final PaymentGateway paymentGateway;
    private final PaymentNotificationPublisher notificationPublisher;
    private final PaymentMapper paymentMapper;

    public AdvancedPaymentService(
            PurchaseRepository purchaseRepository,
            PaymentGateway paymentGateway,
            PaymentNotificationPublisher notificationPublisher,
            PaymentMapper paymentMapper
    ) {
        this.purchaseRepository = purchaseRepository;
        this.paymentGateway = paymentGateway;
        this.notificationPublisher = notificationPublisher;
        this.paymentMapper = paymentMapper;
    }

    @Transactional
    public PaymentResponse settle(Long purchaseId, PaymentRequest request) {
        request.validate();
        Purchase purchase = loadPurchase(purchaseId);

        PaymentGatewayRequest gatewayRequest = paymentMapper.toGatewayRequest(purchase, request);
        PaymentGatewayResponse gatewayResponse = paymentGateway.pay(gatewayRequest);

        if (!gatewayResponse.success()) {
            throw new IllegalStateException("결제에 실패했습니다: " + gatewayResponse.message());
        }

        purchase.markCompleted();
        purchaseRepository.save(purchase);

        notificationPublisher.publish(paymentMapper.toNotification(purchase, gatewayResponse));

        return paymentMapper.toResponse(purchase, gatewayResponse);
    }

    private Purchase loadPurchase(Long purchaseId) {
        return purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new IllegalArgumentException("구매 내역을 찾을 수 없습니다."));
    }
}
