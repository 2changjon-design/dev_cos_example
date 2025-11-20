package com.example.study.lecture.refactorspringsection1answer;

import com.example.study.common.ServiceException;
import com.example.study.common.ServiceExceptionCode;
import com.example.study.lecture.refactorspringsection1.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Section1 After Service 정답 예시.
 */
@Service
public class RefactorSection1OrderServiceAfterAnswer {

    private final RefactorSection1OrderRepository orderRepository;
    private final RefactorSection1OrderMapperAfterAnswer orderMapper;

    public RefactorSection1OrderServiceAfterAnswer(RefactorSection1OrderRepository orderRepository,
                                                   RefactorSection1OrderMapperAfterAnswer orderMapper) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
    }

    @Transactional
    public RefactorSection1OrderResponse placeOrder(RefactorSection1OrderRequest request) {
        validateRequest(request);
        RefactorSection1Order order = new RefactorSection1Order(null, request.userId());
        request.lines().forEach(line ->
                order.addLine(orderMapper.toOrderLine(line))
        );
        enforceTotalPriceLimit(order.getTotalPrice());
        RefactorSection1Order saved = orderRepository.save(order);
        return RefactorSection1OrderResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public RefactorSection1OrderResponse getOrder(Long orderId) {
        RefactorSection1Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_ORDER));
        return RefactorSection1OrderResponse.from(order);
    }

    private void validateRequest(RefactorSection1OrderRequest request) {
        if (request.userId() == null || request.lines().isEmpty()) {
            throw new ServiceException(ServiceExceptionCode.INVALID_ORDER_QUANTITY);
        }
        request.lines().forEach(line -> {
            if (line.quantity() <= 0) {
                throw new ServiceException(ServiceExceptionCode.INVALID_ORDER_QUANTITY);
            }
        });
    }

    private void enforceTotalPriceLimit(BigDecimal totalPrice) {
        if (totalPrice.compareTo(BigDecimal.valueOf(1_000_000)) > 0) {
            throw new ServiceException(ServiceExceptionCode.INVALID_ORDER_QUANTITY);
        }
    }
}
