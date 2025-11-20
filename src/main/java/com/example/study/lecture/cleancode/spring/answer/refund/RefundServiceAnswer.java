package com.example.study.lecture.cleancode.spring.answer.refund;

import com.example.study.entity.Purchase;
import com.example.study.repository.PurchaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RefundServiceAnswer {

    private final PurchaseRepository purchaseRepository;
    private final RefundValidator refundValidator;
    private final RefundNotifier refundNotifier;
    private final RefundAuditLogger refundAuditLogger;

    public RefundServiceAnswer(
            PurchaseRepository purchaseRepository,
            RefundValidator refundValidator,
            RefundNotifier refundNotifier,
            RefundAuditLogger refundAuditLogger
    ) {
        this.purchaseRepository = purchaseRepository;
        this.refundValidator = refundValidator;
        this.refundNotifier = refundNotifier;
        this.refundAuditLogger = refundAuditLogger;
    }

    @Transactional
    public RefundResponse processRefund(Long purchaseId, RefundRequest request) {
        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new IllegalArgumentException("구매 내역을 찾을 수 없습니다."));

        refundValidator.validate(purchase, request);

        purchase.markRefunded();
        purchaseRepository.save(purchase);

        refundNotifier.notifyRefund(purchase, request.reason());
        refundAuditLogger.appendSuccess(purchaseId, request.reason());

        return new RefundResponse(purchase.getId(), purchase.getStatus().name(), request.reason());
    }
}
