package com.example.study.lecture.cleancode.spring.answer4;

public record CommerceOrderRequest(
        String name,
        String email,
        Long productId,
        Integer quantity
) {

    public void validate() {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email은 필수입니다.");
        }
        if (productId == null) {
            throw new IllegalArgumentException("productId는 필수입니다.");
        }
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("quantity는 1 이상이어야 합니다.");
        }
    }
}
