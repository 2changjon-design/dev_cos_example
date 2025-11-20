package com.example.study.lecture.refactorspringsection3;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Section3 Controller BEFORE
 *
 * 리팩토링 힌트:
 * - Controller는 Service 한 개만 의존하고, 응답 변환은 Service/Mapper에 위임하자 (answer 참고).
 * - HTTP Method/URI/에러 응답을 일관되게 정의해 문서화를 돕자.
 */
@RestController
@RequestMapping("/lecture/refactor-spring-section3/products")
public class RefactorSpringSection3ProductControllerBefore {

    private final RefactorSpringSection3ProductServiceBefore productService;

    public RefactorSpringSection3ProductControllerBefore(RefactorSpringSection3ProductServiceBefore productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<RefactorSpringSection3ProductResponse> create(
            @Valid @RequestBody RefactorSpringSection3ProductRequest request
    ) {
        return ResponseEntity.ok(productService.createProduct(request));
    }

    @GetMapping
    public ResponseEntity<List<RefactorSpringSection3ProductResponse>> list() {
        return ResponseEntity.ok(productService.listProducts());
    }

    @PatchMapping("/{productId}/deactivate")
    public ResponseEntity<RefactorSpringSection3ProductResponse> deactivate(@PathVariable Long productId) {
        return ResponseEntity.ok(productService.deactivateProduct(productId));
    }
}
