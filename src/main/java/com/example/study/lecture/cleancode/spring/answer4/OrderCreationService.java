package com.example.study.lecture.cleancode.spring.answer4;

import com.example.study.PurchaseStatus;
import com.example.study.entity.Product;
import com.example.study.entity.Purchase;
import com.example.study.entity.User;
import com.example.study.repository.PurchaseRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class OrderCreationService {

    private final PurchaseRepository purchaseRepository;

    public OrderCreationService(PurchaseRepository purchaseRepository) {
        this.purchaseRepository = purchaseRepository;
    }

    public Purchase createOrder(User user, Product product, int quantity) {
        Purchase purchase = Purchase.builder()
                .user(user)
                .product(product)
                .quantity(quantity)
                .unitPrice(product.getPrice())
                .totalPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity)))
                .status(PurchaseStatus.COMPLETED)
                .build();
        return purchaseRepository.save(purchase);
    }
}
