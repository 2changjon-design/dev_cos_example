package com.example.study.lecture.cleancode.spring.answer.order;

public record OrderRequest(Long userId, Long productId, Integer quantity) {

    public void validate() {
        if (userId == null) {
            throw new IllegalArgumentException("userId는 필수입니다.");
        }
        if (productId == null) {
            throw new IllegalArgumentException("productId는 필수입니다.");
        }
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("quantity는 1 이상이어야 합니다.");
        }
    }
}
