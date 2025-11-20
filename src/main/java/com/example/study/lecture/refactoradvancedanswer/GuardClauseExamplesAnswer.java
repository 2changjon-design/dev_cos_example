package com.example.study.lecture.refactoradvancedanswer;

import com.example.study.common.ServiceException;
import com.example.study.common.ServiceExceptionCode;

/**
 * Guard Clause After Answer.
 */
public final class GuardClauseExamplesAnswer {

    private GuardClauseExamplesAnswer() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Guard Clause를 적용한 최종 버전.
     * - 예외 상황을 early return으로 처리해 happy path(주문 로직)만 아래에 남게 한다.
     * - 각 검증이 별도 메서드로 분리돼 테스트 범위가 좁아지고, 가독성이 향상된다.
     */
    public static class GuardClauseAfterService {

        public void processOrder(Long userId,
                                 Integer quantity,
                                 Integer availableStock,
                                 Integer creditLimit,
                                 String shippingCountry) {
            validateUser(userId);
            validateQuantity(quantity);
            validateStock(availableStock, quantity);
            validateCreditLimit(creditLimit, quantity);
            validateShippingCountry(shippingCountry);
            placeOrder(userId, quantity);
        }

        private void validateUser(Long userId) {
            if (userId == null) {
                throw new ServiceException(ServiceExceptionCode.NOT_FOUND_USER);
            }
        }

        private void validateQuantity(Integer quantity) {
            if (quantity == null || quantity <= 0 || quantity >= 100) {
                throw new ServiceException(ServiceExceptionCode.INVALID_ORDER_QUANTITY);
            }
        }

        private void validateStock(Integer stock, Integer quantity) {
            if (stock == null || stock < quantity) {
                throw new ServiceException(ServiceExceptionCode.OUT_OF_STOCK_PRODUCT);
            }
        }

        private void validateCreditLimit(Integer creditLimit, Integer quantity) {
            if (creditLimit == null || creditLimit < quantity * 10) {
                throw new ServiceException(ServiceExceptionCode.INVALID_ORDER_QUANTITY);
            }
        }

        private void validateShippingCountry(String shippingCountry) {
            if ("CN".equalsIgnoreCase(shippingCountry)) {
                throw new ServiceException(ServiceExceptionCode.INVALID_ORDER_QUANTITY);
            }
        }

        private void placeOrder(Long userId, Integer quantity) {
            // happy path 처리
        }
    }
}
