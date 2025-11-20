package com.example.study.lecture.cleancode.answer;

/**
 * Parameter Object와 의도 기반 메서드로 재작성한 할인 계산 예제.
 * 단계별 변환을 고정된 파이프라인으로 표현해 각 정책 추가/삭제 시 영향 범위를 최소화했다.
 */
public class DiscountCalculationAnswer {

    /**
     * 계산 순서를 한 메서드에서만 관리해 사이드 이펙트나 중복 계산을 방지한다.
     */
    public double calculate(DiscountContext context) {
        double subtotal = applyRateDiscounts(context);
        subtotal = applyCouponAndPoints(subtotal, context);
        subtotal = includeShipping(subtotal, context);
        subtotal = applyTax(subtotal, context);
        subtotal = applyWeekendPromotion(subtotal, context);
        return Math.max(subtotal, 0);
    }

    /**
     * 비율 할인은 원가 기준으로만 계산되므로 별도 메서드로 묶어 중복 로직을 제거했다.
     */
    private double applyRateDiscounts(DiscountContext context) {
        double result = context.price();
        result -= context.price() * context.discountRate();
        result -= context.price() * context.membershipRate();
        return result;
    }

    /**
     * 쿠폰/포인트 차감은 순서가 중요하므로 하나의 메서드에서 명시적으로 기록한다.
     */
    private double applyCouponAndPoints(double current, DiscountContext context) {
        double result = current;
        if (context.hasCoupon()) {
            result -= context.couponValue();
        }
        result -= context.loyaltyPoint() * 0.1;
        return result;
    }

    /**
     * 배송비 포함 여부를 분기해, 조건이 바뀌더라도 메인 플로우를 건드리지 않도록 했다.
     */
    private double includeShipping(double current, DiscountContext context) {
        if (!context.includeShipping()) {
            return current;
        }
        return current + context.shippingCost();
    }

    /**
     * 세금 포함 여부를 flag로 받고 외부에서 강제하도록 해, 다국가 정책도 쉽게 대응한다.
     */
    private double applyTax(double current, DiscountContext context) {
        if (context.taxIncluded()) {
            return current;
        }
        return current * (1 + context.taxRate());
    }

    /**
     * 주말 프로모션은 가장 마지막에 적용해야 음수 가격을 막을 수 있어 순서를 따로 명시했다.
     */
    private double applyWeekendPromotion(double current, DiscountContext context) {
        if (!context.weekendPromotion()) {
            return current;
        }
        return current - context.weekendDiscountFlat();
    }

    /**
     * 계산에 필요한 값을 묶은 Parameter Object.
     * 파라미터 추가/삭제가 잦은 도메인이라 record를 사용해 불변성과 간결함을 동시에 확보했다.
     */
    public record DiscountContext(
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
            boolean weekendPromotion,
            double weekendDiscountFlat
    ) {
    }
}
