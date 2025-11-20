package com.example.study.lecture.cleancode.spring.answer4;

import com.example.study.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class StockValidator {

    public void ensureEnough(Product product, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity는 1 이상이어야 합니다.");
        }
        if (product.getStock() < quantity) {
            throw new IllegalStateException("재고가 부족합니다.");
        }
    }
}
