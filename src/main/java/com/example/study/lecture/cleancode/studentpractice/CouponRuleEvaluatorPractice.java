package com.example.study.lecture.cleancode.studentpractice;

import java.math.BigDecimal;
import java.util.List;

/**
 * 학생 전용 과제 2.
 * - 같은 조건이 여기저기 등장하는 쿠폰 평가 로직
 * - 매직 넘버와 하드코딩된 문자열
 *
 * TODO(STUDENT):
 *  1) 조건식을 메서드/enum으로 추출해 가독성을 높이세요.
 *  2) `evaluate` 메서드를 짧은 메서드 여러 개로 분리하고, DRY 원칙을 지키세요.
 *  3) 할인 타입/최대치/메시지를 도메인 모델로 표현하세요.
 */
public class CouponRuleEvaluatorPractice {

    public String evaluate(UserProfile profile, List<String> coupons, BigDecimal cartAmount) {
        StringBuilder builder = new StringBuilder();
        builder.append("USER=").append(profile.userId()).append("\n");
        for (String coupon : coupons) {
            if (coupon.startsWith("VIP")) {
                if (profile.grade().equals("VIP") && cartAmount.compareTo(BigDecimal.valueOf(300)) > 0) {
                    builder.append(coupon).append(":OK:").append(cartAmount.multiply(BigDecimal.valueOf(0.25))).append("\n");
                } else {
                    builder.append(coupon).append(":FAIL:grade or amount\n");
                }
            } else if (coupon.startsWith("WELCOME")) {
                if (profile.firstPurchase()) {
                    BigDecimal discount = BigDecimal.valueOf(20);
                    if (cartAmount.compareTo(discount) < 0) {
                        discount = cartAmount;
                    }
                    builder.append(coupon).append(":OK:").append(discount).append("\n");
                } else {
                    builder.append(coupon).append(":FAIL:not first\n");
                }
            } else if (coupon.startsWith("POINT")) {
                int points = profile.point();
                if (points > 1000) {
                    builder.append(coupon).append(":OK:").append(points / 10).append("\n");
                } else {
                    builder.append(coupon).append(":FAIL:not enough point\n");
                }
            } else {
                builder.append(coupon).append(":FAIL:unknown\n");
            }
        }
        return builder.toString();
    }

    public record UserProfile(String userId, String grade, boolean firstPurchase, int point) {
    }
}
