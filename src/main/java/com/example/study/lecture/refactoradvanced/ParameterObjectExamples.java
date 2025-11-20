package com.example.study.lecture.refactoradvanced;

/**
 * Parameter Object 리팩토링 예제
 */
public final class ParameterObjectExamples {

    private ParameterObjectExamples() {
        throw new IllegalStateException("Utility class");
    }

    public static class PaymentServiceBefore {
        public void pay(Long userId,
                        String cardNumber,
                        String cardOwner,
                        String cvc,
                        String currency,
                        Integer installmentMonths,
                        String discountCode,
                        boolean enableReward) {
            // 복잡한 파라미터 집합
        }
    }

    public record CardInfo(String cardNumber, String cardOwner, String cvc) {
    }

    public record PaymentCommand(Long userId,
                                 CardInfo cardInfo,
                                 String currency,
                                 Integer installmentMonths,
                                 String discountCode,
                                 boolean enableReward) {
    }

    public static class PaymentServiceAfter {
        public void pay(PaymentCommand command) {
            // TODO: command 객체를 활용해 카드, 통화, 할인, 적립 여부 등을 한꺼번에 처리하자.
        }
    }
}
