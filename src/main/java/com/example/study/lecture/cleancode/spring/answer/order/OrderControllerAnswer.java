package com.example.study.lecture.cleancode.spring.answer.order;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/spring/answer/orders")
public class OrderControllerAnswer {

    private final OrderServiceAnswer orderService;

    public OrderControllerAnswer(OrderServiceAnswer orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(@RequestBody OrderRequest request) {
        OrderResponse response = orderService.placeOrder(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long orderId) {
        OrderResponse response = orderService.findOrder(orderId);
        return ResponseEntity.ok(response);
    }
}
