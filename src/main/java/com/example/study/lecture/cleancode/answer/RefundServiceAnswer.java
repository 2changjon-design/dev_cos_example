package com.example.study.lecture.cleancode.answer;

/**
 * RefundServicePractice의 네이밍을 개선한 버전.
 * 메서드/변수 이름만으로도 "부분 환불을 어떻게 처리하는지"를 이해할 수 있게 의도를 드러냈다.
 */
public class RefundServiceAnswer {

    /**
     * 오케스트레이션 책임만 남겨두고 세부 계산/저장을 헬퍼 메서드로 분리해 가독성을 높였다.
     */
    public void processPartialRefund(Long purchaseId, Double refundAmount) {
        validateRequest(purchaseId, refundAmount);

        double remainingAmount = calculateRemainingAmount(purchaseId, refundAmount);
        saveRefundDetails(purchaseId, refundAmount, remainingAmount);
    }

    /**
     * 입력 검증 실패 시 조기에 예외를 던져 이후 비즈니스 로직에 불필요한 분기를 만들지 않는다.
     */
    private void validateRequest(Long purchaseId, Double refundAmount) {
        if (purchaseId == null) {
            throw new IllegalArgumentException("purchaseId는 필수입니다.");
        }
        if (refundAmount == null || refundAmount <= 0) {
            throw new IllegalArgumentException("refundAmount는 0보다 커야 합니다.");
        }
    }

    /**
     * 남은 금액 계산을 메서드로 분리하면 향후 DB 조회나 복잡한 계산이 들어가도 시그니처를 유지할 수 있다.
     */
    private double calculateRemainingAmount(Long purchaseId, Double refundAmount) {
        return 100.0 - refundAmount;
    }

    /**
     * 영속화 작업을 별도 메서드로 둬 I/O 로직 교체(예: 이벤트 발행) 시 영향을 최소화했다.
     */
    private void saveRefundDetails(Long purchaseId, Double refundAmount, Double remainingAmount) {
        // 데이터 저장 로직
    }
}
