package com.example.study.lecture.refactorspringsection1answer;

import com.example.study.lecture.refactorspringsection1.RefactorSection1OrderRequest;
import com.example.study.lecture.refactorspringsection1.RefactorSection1OrderResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Section1 After Controller 정답 예시.
 */
@RestController
@RequestMapping("/lecture/refactor-section1/answers/orders")
public class RefactorSection1OrderControllerAfterAnswer {

    private final RefactorSection1OrderServiceAfterAnswer orderService;

    public RefactorSection1OrderControllerAfterAnswer(RefactorSection1OrderServiceAfterAnswer orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<RefactorSection1OrderResponse> placeOrder(
            @Valid @RequestBody RefactorSection1OrderRequest request
    ) {
        return ResponseEntity.ok(orderService.placeOrder(request));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<RefactorSection1OrderResponse> getOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrder(orderId));
    }
}
