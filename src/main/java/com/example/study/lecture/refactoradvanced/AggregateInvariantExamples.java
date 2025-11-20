package com.example.study.lecture.refactoradvanced;

import java.math.BigDecimal;

/**
 * Aggregate 불변성 리팩토링 예제
 *
 * BEFORE:
 *  - setter로 상태를 직접 변경해 불변성이 깨질 수 있다.
 * AFTER:
 *  - TODO: 의미 있는 도메인 메서드(credit,debit 등)로만 상태 변경을 허용하자.
 */
public final class AggregateInvariantExamples {

    private AggregateInvariantExamples() {
        throw new IllegalStateException("Utility class");
    }

    public static class AccountBefore {
        private BigDecimal balance = BigDecimal.ZERO;

        public void setBalance(BigDecimal balance) {
            this.balance = balance;
        }
    }

    public static class AccountAfter {
        private BigDecimal balance = BigDecimal.ZERO;

        public void credit(BigDecimal amount) {
            balance = balance.add(amount);
        }

        public void debit(BigDecimal amount) {
            if (balance.compareTo(amount) < 0) {
                throw new IllegalStateException("Insufficient");
            }
            balance = balance.subtract(amount);
        }

        public BigDecimal getBalance() {
            return balance;
        }
    }
}
