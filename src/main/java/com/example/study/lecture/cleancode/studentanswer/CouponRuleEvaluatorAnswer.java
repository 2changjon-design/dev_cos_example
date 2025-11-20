package com.example.study.lecture.cleancode.studentanswer;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class CouponRuleEvaluatorAnswer {

    private final Map<CouponType, BiFunction<UserProfile, BigDecimal, EvaluationResult>> rules = new EnumMap<>(CouponType.class);

    public CouponRuleEvaluatorAnswer() {
        rules.put(CouponType.VIP, this::evaluateVip);
        rules.put(CouponType.WELCOME, this::evaluateWelcome);
        rules.put(CouponType.POINT, this::evaluatePoint);
    }

    public String evaluate(UserProfile profile, List<String> coupons, BigDecimal cartAmount) {
        StringBuilder builder = new StringBuilder();
        builder.append("USER=").append(profile.userId()).append("\n");
        for (String couponCode : coupons) {
            CouponType type = CouponType.detect(couponCode);
            EvaluationResult result = rules.getOrDefault(type, this::unsupported)
                    .apply(profile, cartAmount);
            builder.append(couponCode)
                    .append(":")
                    .append(result.status())
                    .append(":")
                    .append(result.payload())
                    .append("\n");
        }
        return builder.toString();
    }

    private EvaluationResult evaluateVip(UserProfile profile, BigDecimal cartAmount) {
        if (!profile.grade().equals("VIP") || cartAmount.compareTo(BigDecimal.valueOf(300)) <= 0) {
            return EvaluationResult.fail("grade or amount");
        }
        return EvaluationResult.ok(cartAmount.multiply(BigDecimal.valueOf(0.25)));
    }

    private EvaluationResult evaluateWelcome(UserProfile profile, BigDecimal cartAmount) {
        if (!profile.firstPurchase()) {
            return EvaluationResult.fail("not first");
        }
        BigDecimal discount = BigDecimal.valueOf(20);
        if (cartAmount.compareTo(discount) < 0) {
            discount = cartAmount;
        }
        return EvaluationResult.ok(discount);
    }

    private EvaluationResult evaluatePoint(UserProfile profile, BigDecimal cartAmount) {
        if (profile.point() <= 1000) {
            return EvaluationResult.fail("not enough point");
        }
        return EvaluationResult.ok(BigDecimal.valueOf(profile.point() / 10.0));
    }

    private EvaluationResult unsupported(UserProfile profile, BigDecimal cartAmount) {
        return EvaluationResult.fail("unknown");
    }

    public record EvaluationResult(String status, String payload) {
        private static EvaluationResult ok(BigDecimal payload) {
            return new EvaluationResult("OK", payload.toPlainString());
        }

        private static EvaluationResult fail(String reason) {
            return new EvaluationResult("FAIL", reason);
        }
    }

    public enum CouponType {
        VIP,
        WELCOME,
        POINT,
        UNKNOWN;

        public static CouponType detect(String code) {
            if (code.startsWith("VIP")) return VIP;
            if (code.startsWith("WELCOME")) return WELCOME;
            if (code.startsWith("POINT")) return POINT;
            return UNKNOWN;
        }
    }

    public record UserProfile(String userId, String grade, boolean firstPurchase, int point) {
    }
}
