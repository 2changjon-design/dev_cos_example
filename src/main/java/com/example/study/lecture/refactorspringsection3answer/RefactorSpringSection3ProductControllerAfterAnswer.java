package com.example.study.lecture.refactorspringsection3answer;

import com.example.study.lecture.refactorspringsection3.RefactorSpringSection3ProductRequest;
import com.example.study.lecture.refactorspringsection3.RefactorSpringSection3ProductResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lecture/refactor-spring-section3/answers/products")
public class RefactorSpringSection3ProductControllerAfterAnswer {

    private final RefactorSpringSection3ProductServiceAfterAnswer productService;

    public RefactorSpringSection3ProductControllerAfterAnswer(
            RefactorSpringSection3ProductServiceAfterAnswer productService
    ) {
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

    @PutMapping("/{productId}")
    public ResponseEntity<RefactorSpringSection3ProductResponse> update(
            @PathVariable Long productId,
            @Valid @RequestBody RefactorSpringSection3ProductRequest request
    ) {
        return ResponseEntity.ok(productService.updateProduct(productId, request));
    }
}
