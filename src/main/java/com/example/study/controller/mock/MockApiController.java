package com.example.study.controller.mock;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/mock")
public class MockApiController {

    private final AtomicLong userIdSequence = new AtomicLong(1);
    private final AtomicLong productIdSequence = new AtomicLong(1);
    private final Map<Long, MockProduct> products = new ConcurrentHashMap<>();
    private final Map<Long, MockUserResponse> users = new ConcurrentHashMap<>();

    public MockApiController() {
        addProduct("테스트 노트북", 10);
        addProduct("테스트 모니터", 5);
        addProduct("테스트 키보드", 20);
    }

    @GetMapping("/products")
    public MockApiResponse<List<MockProductResponse>> findProducts() {
        List<MockProductResponse> response = new ArrayList<>();
        products.values().forEach(product -> response.add(product.toResponse()));
        return MockApiResponse.success(response);
    }

    @PostMapping("/users")
    public ResponseEntity<MockApiResponse<MockUserResponse>> registerUser(
            @Valid @RequestBody MockUserRequest request
    ) {
        long userId = userIdSequence.getAndIncrement();
        MockUserResponse response = new MockUserResponse(
                userId,
                request.username(),
                request.email(),
                LocalDateTime.now()
        );
        users.put(userId, response);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(MockApiResponse.success(response));
    }

    @DeleteMapping("/products/{productId}")
    public ResponseEntity<MockApiResponse<MockProductResponse>> deleteProduct(
            @PathVariable long productId
    ) {
        MockProduct product = products.get(productId);
        if (product == null) {
            return ResponseEntity.badRequest()
                    .body(MockApiResponse.failure(
                            new MockErrorResponse("NOT_FOUND_PRODUCT", "등록되지 않은 상품입니다.")
                    ));
        }

        if (product.deleted) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(MockApiResponse.failure(
                            new MockErrorResponse("ALREADY_DELETED", "이미 삭제된 상품입니다.")
                    ));
        }

        product.deleted = true;
        return ResponseEntity.ok(MockApiResponse.success(product.toResponse()));
    }

    private void addProduct(String name, int stock) {
        long id = productIdSequence.getAndIncrement();
        products.put(id, new MockProduct(id, name, stock));
    }

    /**
     * 사용자 생성 요청 DTO.
     */
    public record MockUserRequest(
            @NotBlank(message = "username은 필수입니다.")
            String username,
            @Email(message = "email 형식이 올바르지 않습니다.")
            String email,
            @NotBlank(message = "password는 필수입니다.")
            @Size(min = 6, max = 30, message = "password는 6~30자로 입력해주세요.")
            String password
    ) {
    }

    /**
     * 공통 응답 DTO.
     */
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

    /**
     * 에러 응답 DTO.
     */
    public record MockErrorResponse(
            String errorCode,
            String message
    ) {
    }

    /**
     * 사용자 응답 DTO.
     */
    public record MockUserResponse(
            long userId,
            String username,
            String email,
            LocalDateTime createdAt
    ) {
    }

    /**
     * 상품 응답 DTO.
     */
    public record MockProductResponse(
            long productId,
            String name,
            int stock,
            boolean deleted
    ) {
    }

    private static class MockProduct {
        private final long id;
        private final String name;
        private final int stock;
        private boolean deleted;

        private MockProduct(long id, String name, int stock) {
            this.id = id;
            this.name = name;
            this.stock = stock;
            this.deleted = false;
        }

        private MockProductResponse toResponse() {
            return new MockProductResponse(id, name, stock, deleted);
        }
    }
}
