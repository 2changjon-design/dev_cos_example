package com.example.study.lecture.cleancode.spring.answer4;

import com.example.study.entity.Product;
import com.example.study.entity.Purchase;
import com.example.study.entity.User;
import org.springframework.stereotype.Component;

@Component
public class CommerceOrderMapper {

    public CommerceOrderResponse toResponse(Purchase purchase, User user, Product product) {
        return new CommerceOrderResponse(
                purchase.getId(),
                user.getId(),
                user.getEmail(),
                product.getId(),
                product.getName(),
                purchase.getQuantity(),
                purchase.getTotalPrice(),
                purchase.getStatus().name()
        );
    }
}
