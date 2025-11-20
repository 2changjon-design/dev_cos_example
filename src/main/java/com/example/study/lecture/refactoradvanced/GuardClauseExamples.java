package com.example.study.lecture.refactoradvanced;

import com.example.study.common.ServiceException;
import com.example.study.common.ServiceExceptionCode;

/**
 * Guard Clause 리팩토링 예제
 *
 * BEFORE:
 *  - 중첩 if/else 로직이 길어 가독성이 떨어진다.
 *  - 예외 상황을 early return으로 처리하도록 리팩토링해보자.
 *
 * AFTER:
 *  - TODO: guard clause 형태로 변환한다 (answer 참고).
 */
public final class GuardClauseExamples {

    private GuardClauseExamples() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * BEFORE: deeply nested validation code
     */
    public static class GuardClauseBeforeService {

        public void processOrder(Long userId,
                                 Integer quantity,
                                 Integer availableStock,
                                 Integer creditLimit,
                                 String shippingCountry) {
            if (userId != null) {
                if (quantity != null) {
                    if (quantity > 0) {
                        if (availableStock != null && availableStock >= quantity) {
                            if (creditLimit != null && creditLimit >= quantity * 10) {
                                if (!"CN".equalsIgnoreCase(shippingCountry)) {
                                    if (quantity < 100) {
                                        placeOrder(userId, quantity);
                                    } else {
                                        throw new ServiceException(ServiceExceptionCode.INVALID_ORDER_QUANTITY);
                                    }
                                } else {
                                    throw new ServiceException(ServiceExceptionCode.INVALID_ORDER_QUANTITY);
                                }
                            } else {
                                throw new ServiceException(ServiceExceptionCode.INVALID_ORDER_QUANTITY);
                            }
                        } else {
                            throw new ServiceException(ServiceExceptionCode.OUT_OF_STOCK_PRODUCT);
                        }
                    } else {
                        throw new ServiceException(ServiceExceptionCode.INVALID_ORDER_QUANTITY);
                    }
                } else {
                    throw new ServiceException(ServiceExceptionCode.INVALID_ORDER_QUANTITY);
                }
            } else {
                throw new ServiceException(ServiceExceptionCode.NOT_FOUND_USER);
            }
        }

        private void placeOrder(Long userId, Integer quantity) {
            // 주문 로직
        }
    }

    /**
     * AFTER Placeholder:
     *  - TODO: guard clause를 활용해 예외 상황을 즉시 반환하고 happy path만 남기자.
     *  - 예: validateUser(userId), validateQuantity(quantity) 등을 도입해도 좋다.
     */
    public static class GuardClauseAfterService {
        // 실습 시 구현
    }
}
