package com.example.study.lecture.refactorspringsection1;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Spring MVC Controller 리팩토링 예제 (Before).
 *
 * 리팩토링 힌트:
 * - After Answer에서는 Controller가 Service만 의존하도록 단순화되어 있다. Repository 직접 호출을 제거해보자.
 * - 클래스 책임을 "HTTP 처리"에만 집중시키고, DTO ↔ Domain 변환은 Service/Mapper로 위임한다.
 * - ResponseEntity 빌더/에러 처리도 전역 핸들러나 공통 컴포넌트로 분리해 가독성을 높일 수 있다.
 */
@RestController
@RequestMapping("/lecture/refactor-section1/orders")
public class RefactorSection1OrderControllerBefore {

    private final RefactorSection1OrderServiceBefore orderService;
    private final RefactorSection1OrderRepository orderRepository;

    public RefactorSection1OrderControllerBefore(RefactorSection1OrderServiceBefore orderService,
                                                 RefactorSection1OrderRepository orderRepository) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
    }

    @PostMapping
    public ResponseEntity<RefactorSection1OrderResponse> placeOrder(
            @Valid @RequestBody RefactorSection1OrderRequest request
    ) {
        RefactorSection1OrderResponse response = orderService.placeOrder(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<RefactorSection1OrderResponse> getOrder(@PathVariable Long orderId) {
        RefactorSection1Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        return ResponseEntity.ok(RefactorSection1OrderResponse.from(order));
    }
}
