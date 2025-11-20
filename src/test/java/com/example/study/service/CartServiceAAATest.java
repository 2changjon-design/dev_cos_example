package com.example.study.service;

import com.example.study.service.dto.CartSummary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CartServiceAAATest {

    // 테스트 대상 CartService 인스턴스를 선언한다.
    private CartService cartService;

    @BeforeEach
    void setUp() {
        // Arrange: 각 테스트 전에 새로운 CartService를 생성해 상태를 초기화한다.
        cartService = new CartService();
    }

    @Test
    @DisplayName("[AAA] 아이템을 추가하면 총 수량과 금액이 증가한다")
    void addItem_shouldIncreaseSummaryValues() {
        // Arrange: 테스트에 사용할 아이템 이름을 선언한다.
        String itemName = "테스트노트북"; // 테스트에 사용할 아이템 이름
        // Arrange: 단가를 정수로 지정한다.
        int unitPrice = 1000; // 단가를 1,000으로 설정
        // Arrange: 수량을 2개로 설정한다.
        int quantity = 2; // 수량을 2개로 설정

        // Act: CartService의 addItem을 호출해 요약 결과를 받는다.
        CartSummary summary = cartService.addItem(itemName, unitPrice, quantity); // 서비스 호출 후 요약 값을 받는다

        // Assert: 총 수량이 2인지 확인한다.
        assertThat(summary.totalQuantity()).isEqualTo(2); // 총 수량이 2인지 검증
        // Assert: 총 금액이 단가 * 수량인지 검증한다.
        assertThat(summary.totalPrice()).isEqualTo(2000); // 총 금액이 2,000인지 검증
        // Assert: 응답 목록에 동일한 아이템이 존재하는지 확인한다.
        assertThat(summary.items()).anySatisfy(item -> assertThat(item.itemName()).isEqualTo(itemName)); // 응답 목록에 아이템 존재 여부 확인
    }

    @Test
    @DisplayName("[AAA] 존재하지 않는 아이템 삭제 시 예외가 발생한다")
    void removeItem_shouldThrowWhenItemMissing() {
        // Arrange: 장바구니에 아무 것도 넣지 않은 상태를 유지한다.
        // Act & Assert: removeItem 호출 시 IllegalArgumentException 여부를 확인한다.
        assertThatThrownBy(() -> cartService.removeItem("없는아이템")) // 없는 아이템을 삭제 시도
                .isInstanceOf(IllegalArgumentException.class) // 예외 타입 검증
                .hasMessageContaining("Item not found"); // 예외 메시지 검증
    }
}
