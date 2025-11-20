package com.example.study.lecture.cleancode.answer;

/**
 * 오케스트레이션 책임만 담당하도록 리팩토링한 PurchaseService.
 * 실제 취소 로직은 하위 서비스로 위임하고, 여기서는 입력 검증·로그·예외 흐름만 다룬다.
 */
public class PurchaseServiceAnswer {

    private final PurchaseCancelServiceAnswer purchaseCancelService;
    private final AuditTrail auditTrail;

    public PurchaseServiceAnswer(
            PurchaseCancelServiceAnswer purchaseCancelService,
            AuditTrail auditTrail
    ) {
        this.purchaseCancelService = purchaseCancelService;
        this.auditTrail = auditTrail;
    }

    /**
     * 요청 검증 → 감사 로그 → 도메인 서비스 호출 → 실패 처리 순서를 명시적으로 보여준다.
     */
    public PurchaseCancelServiceAnswer.PurchaseCancelResponse cancel(PurchaseCancelRequest request) {
        request.validate();
        auditTrail.write("cancel request start : purchase=" + request.purchaseId());
        try {
            PurchaseCancelServiceAnswer.PurchaseCancelResponse response =
                    purchaseCancelService.cancelPurchase(request.purchaseId(), request.userId());
            auditTrail.write("cancel success : purchase=" + response.getPurchaseId());
            return response;
        } catch (RuntimeException exception) {
            auditTrail.write("cancel fail : purchase=" + request.purchaseId() + ", reason=" + exception.getMessage());
            throw exception;
        }
    }

    /**
     * record를 사용해 DTO를 간결하게 유지하고, 내부에 자체 검증 로직을 둬 서비스가 깔끔해지도록 했다.
     */
    public record PurchaseCancelRequest(Long purchaseId, Long userId) {
        public void validate() {
            if (purchaseId == null) {
                throw new IllegalArgumentException("purchaseId가 필요합니다.");
            }
            if (userId == null) {
                throw new IllegalArgumentException("userId가 필요합니다.");
            }
        }
    }

    /**
     * 부가기능을 인터페이스로 추상화해 동기/비동기 구현을 자유롭게 교체할 수 있도록 했다.
     */
    public interface AuditTrail {
        void write(String message);
    }
}
