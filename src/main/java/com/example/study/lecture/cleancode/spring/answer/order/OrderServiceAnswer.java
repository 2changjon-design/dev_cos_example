package com.example.study.lecture.cleancode.spring.answer.order;

import com.example.study.PurchaseStatus;
import com.example.study.entity.Product;
import com.example.study.entity.Purchase;
import com.example.study.entity.User;
import com.example.study.repository.ProductRepository;
import com.example.study.repository.PurchaseRepository;
import com.example.study.repository.UserJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderServiceAnswer {

    private final UserJpaRepository userRepository;
    private final ProductRepository productRepository;
    private final PurchaseRepository purchaseRepository;
    private final OrderMapperAnswer orderMapper;

    public OrderServiceAnswer(
            UserJpaRepository userRepository,
            ProductRepository productRepository,
            PurchaseRepository purchaseRepository,
            OrderMapperAnswer orderMapper
    ) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.purchaseRepository = purchaseRepository;
        this.orderMapper = orderMapper;
    }

    @Transactional
    public OrderResponse placeOrder(OrderRequest request) {
        request.validate();
        User user = loadUser(request.userId());
        Product product = loadProduct(request.productId());

        product.decreaseStock(request.quantity());
        Product updatedProduct = productRepository.save(product);

        Purchase purchase = orderMapper.toPurchase(user, updatedProduct, request.quantity());
        purchase = purchaseRepository.save(purchase);
        return orderMapper.toResponse(purchase);
    }

    @Transactional(readOnly = true)
    public OrderResponse findOrder(Long orderId) {
        Purchase purchase = purchaseRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));
        return orderMapper.toResponse(purchase);
    }

    private User loadUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    private Product loadProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
    }
}
