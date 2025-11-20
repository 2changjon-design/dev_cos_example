package com.example.study.lecture.cleancode.spring.answer4;

import com.example.study.entity.Product;
import com.example.study.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryService {

    private final ProductRepository productRepository;
    private final StockValidator stockValidator;

    public InventoryService(ProductRepository productRepository, StockValidator stockValidator) {
        this.productRepository = productRepository;
        this.stockValidator = stockValidator;
    }

    @Transactional
    public Product reserveProduct(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
        stockValidator.ensureEnough(product, quantity);
        product.decreaseStock(quantity);
        return product;
    }
}
