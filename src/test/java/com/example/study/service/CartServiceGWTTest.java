package com.example.study.service;

import com.example.study.service.dto.CartSummary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CartServiceGWTTest {

    // 테스트 대상 서비스 인스턴스를 보관한다.
    private CartService cartService;

    @BeforeEach
    void setUp() {
        // Given: 각 테스트 전 새로운 CartService를 준비한다.
        cartService = new CartService();
    }

    @Test
    @DisplayName("[GWT] 동일 아이템을 두 번 추가하면 수량이 누적된다")
    void givenExistingItem_whenAddAgain_thenQuantityAccumulates() {
        // Given: 최초 아이템을 장바구니에 넣는다.
        cartService.addItem("책", 5000, 1); // 첫 추가

        // When: 동일 아이템을 다시 추가한다.
        CartSummary summary = cartService.addItem("책", 5000, 2); // 두 번째 추가 결과

        // Then: 총 수량이 3인지 검증한다.
        assertThat(summary.totalQuantity()).isEqualTo(3); // 수량 누적 검증
    }

    @Test
    @DisplayName("[GWT] 빈 이름으로 추가하면 예외가 발생한다")
    void givenBlankName_whenAddItem_thenThrows() {
        // Given: 공백 이름을 준비한다.
        String blankName = ""; // 공백 문자열

        // When & Then: IllegalArgumentException 여부를 확인한다.
        assertThatThrownBy(() -> cartService.addItem(blankName, 1000, 1)) // 공백 이름 추가 시도
                .isInstanceOf(IllegalArgumentException.class); // 예외 타입 검증
    }
}
