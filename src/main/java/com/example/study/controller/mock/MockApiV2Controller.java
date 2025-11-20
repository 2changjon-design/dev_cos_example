package com.example.study.controller.mock;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 상품 관련 Postman 실습을 위한 Mock API v2.
 * v1보다 확장된 CRUD/재고 조정 시나리오를 제공한다.
 */
@RestController
@RequestMapping("/mock/v2")
public class MockApiV2Controller {

    private final AtomicLong productIdSequence = new AtomicLong(1);
    private final Map<Long, MockProduct> products = new ConcurrentHashMap<>();

    public MockApiV2Controller() {
        addProduct("샘플 노트북", new BigDecimal("1500000"), 15);
        addProduct("샘플 무선 마우스", new BigDecimal("45000"), 50);
        addProduct("샘플 모니터", new BigDecimal("320000"), 8);
    }

    @GetMapping("/products")
    public MockApiResponse<List<MockProductResponse>> findProducts() {
        List<MockProductResponse> response = new ArrayList<>();
        products.values().forEach(product -> response.add(product.toResponse()));
        return MockApiResponse.success(response);
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<MockApiResponse<MockProductResponse>> findProduct(
            @PathVariable long productId
    ) {
        MockProduct product = products.get(productId);
        if (product == null) {
            return notFound();
        }
        return ResponseEntity.ok(MockApiResponse.success(product.toResponse()));
    }

    @PostMapping("/products")
    public ResponseEntity<MockApiResponse<MockProductResponse>> createProduct(
            @Valid @RequestBody ProductCreateRequest request
    ) {
        long id = productIdSequence.getAndIncrement();
        MockProduct product = new MockProduct(
                id,
                request.name(),
                request.price(),
                request.stock()
        );
        products.put(id, product);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(MockApiResponse.success(product.toResponse()));
    }

    @PatchMapping("/products/{productId}/stock")
    public ResponseEntity<MockApiResponse<MockProductResponse>> adjustStock(
            @PathVariable long productId,
            @Valid @RequestBody StockAdjustRequest request
    ) {
        MockProduct product = products.get(productId);
        if (product == null) {
            return notFound();
        }

        int nextStock = product.stock + request.quantityDelta();
        if (nextStock < 0) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(MockApiResponse.failure(
                            new MockErrorResponse("INSUFFICIENT_STOCK", "재고를 음수로 만들 수 없습니다.")
                    ));
        }

        product.stock = nextStock;
        product.updatedAt = LocalDateTime.now();
        return ResponseEntity.ok(MockApiResponse.success(product.toResponse()));
    }

    @DeleteMapping("/products/{productId}")
    public ResponseEntity<MockApiResponse<MockProductResponse>> deleteProduct(
            @PathVariable long productId
    ) {
        MockProduct product = products.get(productId);
        if (product == null) {
            return notFound();
        }

        if (product.deleted) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(MockApiResponse.failure(
                            new MockErrorResponse("ALREADY_DELETED", "이미 삭제된 상품입니다.")
                    ));
        }

        product.deleted = true;
        product.updatedAt = LocalDateTime.now();
        return ResponseEntity.ok(MockApiResponse.success(product.toResponse()));
    }

    private ResponseEntity<MockApiResponse<MockProductResponse>> notFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(MockApiResponse.failure(
                        new MockErrorResponse("NOT_FOUND_PRODUCT", "상품을 찾을 수 없습니다.")
                ));
    }

    private void addProduct(String name, BigDecimal price, int stock) {
        long id = productIdSequence.getAndIncrement();
        products.put(id, new MockProduct(id, name, price, stock));
    }

    public record ProductCreateRequest(
            @NotBlank(message = "상품명을 입력해주세요.")
            String name,
            @Positive(message = "가격은 1원 이상이어야 합니다.")
            BigDecimal price,
            @Min(value = 0, message = "재고는 0 이상이어야 합니다.")
            int stock
    ) {
    }

    public record StockAdjustRequest(
            int quantityDelta
    ) {
    }

    public record MockApiResponse<T>(
            boolean result,
            T data,
            MockErrorResponse error
    ) {
        public static <T> MockApiResponse<T> success(T data) {
            return new MockApiResponse<>(true, data, null);
        }

        public static <T> MockApiResponse<T> failure(MockErrorResponse error) {
            return new MockApiResponse<>(false, null, error);
        }
    }

    public record MockErrorResponse(
            String errorCode,
            String message
    ) {
    }

    public record MockProductResponse(
            long productId,
            String name,
            BigDecimal price,
            int stock,
            boolean deleted,
            LocalDateTime updatedAt
    ) {
    }

    private static class MockProduct {
        private final long id;
        private final String name;
        private final BigDecimal price;
        private int stock;
        private boolean deleted;
        private LocalDateTime updatedAt;

        private MockProduct(long id, String name, BigDecimal price, int stock) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.stock = stock;
            this.deleted = false;
            this.updatedAt = LocalDateTime.now();
        }

        private MockProductResponse toResponse() {
            return new MockProductResponse(id, name, price, stock, deleted, updatedAt);
        }
    }
}
