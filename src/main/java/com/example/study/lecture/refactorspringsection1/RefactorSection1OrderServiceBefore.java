package com.example.study.lecture.refactorspringsection1;

import com.example.study.common.ServiceException;
import com.example.study.common.ServiceExceptionCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Spring Service 레이어 리팩토링 실습 (Before).
 *
 * 리팩토링 힌트:
 * 1. "검증 → 주문 생성 → 총액 검증 → 저장 → 응답 변환" 을 단계별 메서드로 쪼개고, After 예제처럼 Validator/Mapper를 도입해보자.
 * 2. Request → Domain 변환은 MapStruct 기반 Mapper에 맡기면 테스트와 재사용이 쉬워진다 (answer 참고).
 * 3. 총액 제한 검증 로직을 별도 메서드(enforceTotalPriceLimit)로 추출해 SRP를 지키자.
 */
@Service
public class RefactorSection1OrderServiceBefore {

    private final RefactorSection1OrderRepository orderRepository;

    public RefactorSection1OrderServiceBefore(RefactorSection1OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public RefactorSection1OrderResponse placeOrder(RefactorSection1OrderRequest request) {
        if (request.userId() == null || request.lines().isEmpty()) {
            throw new ServiceException(ServiceExceptionCode.INVALID_ORDER_QUANTITY);
        }

        RefactorSection1Order order = new RefactorSection1Order(null, request.userId());
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (RefactorSection1OrderRequest.OrderLineRequest lineRequest : request.lines()) {
            if (lineRequest.quantity() <= 0) {
                throw new ServiceException(ServiceExceptionCode.INVALID_ORDER_QUANTITY);
            }
            RefactorSection1OrderLine line = new RefactorSection1OrderLine(
                    lineRequest.productId(), lineRequest.unitPrice(), lineRequest.quantity()
            );
            order.addLine(line);
            totalPrice = totalPrice.add(
                    lineRequest.unitPrice().multiply(BigDecimal.valueOf(lineRequest.quantity()))
            );
        }

        if (totalPrice.compareTo(BigDecimal.valueOf(1_000_000)) > 0) {
            throw new ServiceException(ServiceExceptionCode.INVALID_ORDER_QUANTITY);
        }

        RefactorSection1Order saved = orderRepository.save(order);
        return RefactorSection1OrderResponse.from(saved);
    }
}
