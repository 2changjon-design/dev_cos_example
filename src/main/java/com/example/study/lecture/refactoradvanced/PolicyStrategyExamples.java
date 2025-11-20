package com.example.study.lecture.refactoradvanced;

import java.math.BigDecimal;

/**
 * Policy/Strategy 리팩토링 예제
 *
 * BEFORE:
 *  - 할인 정책/배송비 정책 등 조건문이 한 클래스에 몰려 있다.
 * AFTER:
 *  - TODO: 정책 인터페이스를 도입하고 구현체로 분리한다.
 */
public final class PolicyStrategyExamples {

    private PolicyStrategyExamples() {
        throw new IllegalStateException("Utility class");
    }

    public static class DiscountCalculatorBefore {

        public BigDecimal calculate(BigDecimal price, String tier, String channel) {
            BigDecimal discounted = price;
            if ("VIP".equals(tier)) {
                discounted = price.multiply(BigDecimal.valueOf(0.8));
            } else if ("GOLD".equals(tier)) {
                discounted = price.multiply(BigDecimal.valueOf(0.9));
            } else if ("SILVER".equals(tier)) {
                discounted = price.multiply(BigDecimal.valueOf(0.95));
            }

            if ("APP".equalsIgnoreCase(channel)) {
                discounted = discounted.multiply(BigDecimal.valueOf(0.97));
            } else if ("PARTNER".equalsIgnoreCase(channel)) {
                discounted = discounted.add(BigDecimal.valueOf(2000));
            }

            return discounted;
        }
    }

    /**
     * AFTER placeholder:
     *  - TODO: DiscountPolicy (tier)와 ChannelPolicy (channel)을 각각 Strategy로 분리하고, Calculator는 두 정책을 조합하도록 만든다.
     */
    public interface DiscountPolicy {
        BigDecimal apply(BigDecimal price);
    }

    public static class PolicyStrategyAfterCalculator {
        private DiscountPolicy policy;

        public PolicyStrategyAfterCalculator(DiscountPolicy policy) {
            this.policy = policy;
        }

        public BigDecimal calculate(BigDecimal price) {
            return policy.apply(price);
        }

        public void changePolicy(DiscountPolicy policy) {
            this.policy = policy;
        }
    }
}
