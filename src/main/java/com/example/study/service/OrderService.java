package com.example.study.service;

import com.example.study.PurchaseStatus;
import com.example.study.common.ServiceException;
import com.example.study.common.ServiceExceptionCode;
import com.example.study.entity.Product;
import com.example.study.entity.Purchase;
import com.example.study.entity.User;
import com.example.study.repository.ProductRepository;
import com.example.study.repository.PurchaseRepository;
import com.example.study.repository.UserJpaRepository;
import com.example.study.service.dto.OrderCreateServiceDto;
import com.example.study.service.dto.OrderResultDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final PurchaseRepository purchaseRepository;
    private final ProductRepository productRepository;
    private final UserJpaRepository userJpaRepository;

    public OrderResultDto createOrder(OrderCreateServiceDto input) {
        User user = userJpaRepository.findById(input.getUserId())
                .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_USER));

        Product product = productRepository.findById(input.getProductId())
                .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_PRODUCT));

        Integer requestedQuantity = input.getQuantity();
        if (requestedQuantity == null || requestedQuantity <= 0) {
            throw new ServiceException(ServiceExceptionCode.INVALID_ORDER_QUANTITY);
        }
        int orderQuantity = requestedQuantity;
        if (product.getStock() < orderQuantity) {
            throw new ServiceException(ServiceExceptionCode.INSUFFICIENT_PRODUCT_STOCK);
        }

        product.decreaseStock(orderQuantity);
        BigDecimal unitPrice = product.getPrice();
        BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(orderQuantity));

        Purchase saved = purchaseRepository.save(
                Purchase.builder()
                        .user(user)
                        .product(product)
                        .quantity(orderQuantity)
                        .unitPrice(unitPrice)
                        .totalPrice(totalPrice)
                        .status(PurchaseStatus.PENDING)
                        .build()
        );
        return OrderResultDto.fromEntity(saved);
    }
}
