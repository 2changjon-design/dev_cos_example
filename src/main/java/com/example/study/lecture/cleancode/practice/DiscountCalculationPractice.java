package com.example.study.lecture.cleancode.practice;

/**
 * 과도한 파라미터와 조건문이 얽힌 할인 계산 예제.
 * Parameter Object 도입과 의미 있는 메서드 추출을 라이브로 보여줄 때 사용한다.
 *
 * TODO(LAB):
 *  1) 모든 파라미터를 담는 컨텍스트 객체(레코드/클래스)를 만들어 calc 시그니처를 단순화하세요.
 *  2) 할인/쿠폰/포인트/배송/세금/주말 로직을 단계별 메서드로 분리하세요.
 *  3) 금액이 음수가 되지 않도록 하는 규칙을 의미 있는 이름으로 감싸세요.
 */
public class DiscountCalculationPractice {

    public double calc(
            double price,
            double discountRate,
            double membershipRate,
            boolean hasCoupon,
            double couponValue,
            int loyaltyPoint,
            boolean includeShipping,
            double shippingCost,
            boolean taxIncluded,
            double taxRate,
            boolean weekendPromotion
    ) {
        double result = price;
        if (discountRate > 0) {
            result -= price * discountRate;
        }
        if (membershipRate > 0) {
            result -= price * membershipRate;
        }
        if (hasCoupon) {
            result -= couponValue;
        }
        if (loyaltyPoint > 0) {
            result -= loyaltyPoint * 0.1;
        }
        if (includeShipping) {
            result += shippingCost;
        }
        if (!taxIncluded) {
            result += result * taxRate;
        }
        if (weekendPromotion) {
            result -= 5;
        }
        if (result < 0) {
            result = 0;
        }
        return result;
    }
}
