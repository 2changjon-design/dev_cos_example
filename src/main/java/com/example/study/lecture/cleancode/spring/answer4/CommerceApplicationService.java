package com.example.study.lecture.cleancode.spring.answer4;

import com.example.study.entity.Product;
import com.example.study.entity.Purchase;
import com.example.study.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommerceApplicationService {

    private final UserAccountService userAccountService;
    private final InventoryService inventoryService;
    private final OrderCreationService orderCreationService;
    private final NotificationGateway notificationGateway;
    private final CommerceOrderMapper orderMapper;

    public CommerceApplicationService(
            UserAccountService userAccountService,
            InventoryService inventoryService,
            OrderCreationService orderCreationService,
            NotificationGateway notificationGateway,
            CommerceOrderMapper orderMapper
    ) {
        this.userAccountService = userAccountService;
        this.inventoryService = inventoryService;
        this.orderCreationService = orderCreationService;
        this.notificationGateway = notificationGateway;
        this.orderMapper = orderMapper;
    }

    @Transactional
    public CommerceOrderResponse placeOrder(CommerceOrderRequest request) {
        request.validate();

        User user = userAccountService.ensureUser(request.name(), request.email());
        Product product = inventoryService.reserveProduct(request.productId(), request.quantity());
        Purchase purchase = orderCreationService.createOrder(user, product, request.quantity());

        notificationGateway.notifyOrder(purchase.getId(), user.getEmail(), product.getId());

        return orderMapper.toResponse(purchase, user, product);
    }
}
