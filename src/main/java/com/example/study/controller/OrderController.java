package com.example.study.controller;

import com.example.study.common.ApiResponse;
import com.example.study.controller.dto.OrderCreateRequest;
import com.example.study.controller.dto.OrderResponse;
import com.example.study.service.OrderService;
import com.example.study.service.dto.OrderResultDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @Valid @RequestBody OrderCreateRequest request
    ) {
        // TODO 인증이 붙는다면 SecurityContextHolder에서 사용자 정보를 가져오도록 변경합니다.
        OrderResultDto result = orderService.createOrder(request.toServiceDto());
        return ApiResponse.created(OrderResponse.from(result));
    }
}
