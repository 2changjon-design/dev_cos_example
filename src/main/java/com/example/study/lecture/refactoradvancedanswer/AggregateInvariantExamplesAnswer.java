package com.example.study.lecture.refactoradvancedanswer;

import java.math.BigDecimal;

/**
 * Aggregate invariant After Answer.
 */
public final class AggregateInvariantExamplesAnswer {

    private AggregateInvariantExamplesAnswer() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 불변성을 지키는 Account 예제.
     * - setter 대신 의미 있는 동작(credit/debit) 메서드만 노출한다.
     * - 잔액 부족/음수 금액 등의 검증을 내부에서 수행해 도메인 규칙을 명시적으로 표현한다.
     */
    public static class Account {
        private BigDecimal balance = BigDecimal.ZERO;

        public void credit(BigDecimal amount) {
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("amount must be positive");
            }
            balance = balance.add(amount);
        }

        public void debit(BigDecimal amount) {
            if (amount.compareTo(balance) > 0) {
                throw new IllegalStateException("Insufficient balance");
            }
            balance = balance.subtract(amount);
        }

        public BigDecimal getBalance() {
            return balance;
        }
    }
}
