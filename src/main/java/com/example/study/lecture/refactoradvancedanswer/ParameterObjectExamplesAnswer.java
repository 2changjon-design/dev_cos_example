package com.example.study.lecture.refactoradvancedanswer;

import com.example.study.lecture.refactoradvanced.ParameterObjectExamples.CardInfo;
import com.example.study.lecture.refactoradvanced.ParameterObjectExamples.PaymentCommand;

/**
 * Parameter Object After Answer.
 */
public final class ParameterObjectExamplesAnswer {

    private ParameterObjectExamplesAnswer() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 파라미터 객체를 이용해 결제 API를 명확히 표현한 버전.
     * - PaymentCommand 안에 userId, CardInfo, currency가 모두 캡슐화돼 메서드 시그니처가 단순해진다.
     * - 새로운 필드가 추가되더라도 Command 객체만 수정하면 되어 OCP에 가깝다.
     */
    public static class PaymentServiceAfter {

        public void pay(PaymentCommand command) {
            Long userId = command.userId();
            CardInfo card = command.cardInfo();
            logPayment(userId, card.cardNumber(), command.currency());
            applyDiscount(command.discountCode());
            if (Boolean.TRUE.equals(command.enableReward())) {
                addReward(userId);
            }
            // 결제 처리 로직 (할부 개월 수 등 추가 정보를 command에서 바로 사용)
        }

        private void logPayment(Long userId, String maskedCard, String currency) {
            // logging
        }

        private void applyDiscount(String discountCode) {
            // 할인 코드 처리
        }

        private void addReward(Long userId) {
            // 리워드 적립
        }
    }
}
