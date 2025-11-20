package com.example.study.lecture.refactoradvancedanswer;

import com.example.study.lecture.refactoradvanced.PolicyStrategyExamples;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Policy/Strategy After Answer.
 * - 등급 정책과 채널 정책을 각각 Strategy로 분리해 조합 가능하게 만든다.
 */
public final class PolicyStrategyExamplesAnswer {

    private PolicyStrategyExamplesAnswer() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Strategy 구현체 예시.
     * - 등급별 할인율을 Map으로 관리해 if/else 체인을 제거했다.
     * - Calculator는 이 Policy를 주입받아 OCP(Open/Closed Principle)를 만족하게 된다.
     */
    public static class TierBasedDiscountPolicy implements PolicyStrategyExamples.DiscountPolicy {
        private final Map<String, BigDecimal> tierRates = Map.of(
                "VIP", BigDecimal.valueOf(0.8),
                "GOLD", BigDecimal.valueOf(0.9),
                "SILVER", BigDecimal.valueOf(0.95)
        );

        private final String tier;

        public TierBasedDiscountPolicy(String tier) {
            this.tier = tier;
        }

        @Override
        public BigDecimal apply(BigDecimal price) {
            return tierRates.getOrDefault(tier, BigDecimal.ONE).multiply(price);
        }
    }

    public interface ChannelPolicy {
        BigDecimal apply(BigDecimal price);
    }

    public static class ChannelDiscountPolicy implements ChannelPolicy {
        @Override
        public BigDecimal apply(BigDecimal price) {
            return price.multiply(BigDecimal.valueOf(0.97));
        }
    }

    public static class ChannelFeePolicy implements ChannelPolicy {
        @Override
        public BigDecimal apply(BigDecimal price) {
            return price.add(BigDecimal.valueOf(2000));
        }
    }

    public static class DiscountCalculator {
        private final PolicyStrategyExamples.DiscountPolicy tierPolicy;
        private final ChannelPolicy channelPolicy;

        public DiscountCalculator(PolicyStrategyExamples.DiscountPolicy tierPolicy, ChannelPolicy channelPolicy) {
            this.tierPolicy = tierPolicy;
            this.channelPolicy = channelPolicy;
        }

        public BigDecimal calculate(BigDecimal price) {
            return channelPolicy.apply(tierPolicy.apply(price));
        }
    }
}
