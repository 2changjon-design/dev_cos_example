package com.example.study.service;

import com.example.study.PurchaseStatus;
import com.example.study.common.ServiceException;
import com.example.study.common.ServiceExceptionCode;
import com.example.study.entity.Category;
import com.example.study.entity.Product;
import com.example.study.entity.Purchase;
import com.example.study.entity.User;
import com.example.study.repository.ProductRepository;
import com.example.study.repository.PurchaseRepository;
import com.example.study.repository.UserJpaRepository;
import com.example.study.service.dto.OrderCreateServiceDto;
import com.example.study.service.dto.OrderResultDto;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// GWT 패턴으로 OrderService를 검증한다.
@ExtendWith(MockitoExtension.class)
class OrderServiceGWTTest {

    @Mock
    private PurchaseRepository purchaseRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private UserJpaRepository userJpaRepository;
    @InjectMocks
    private OrderService orderService;

    private User user;
    private Product product;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .name("사용자")
                .email("order@example.com")
                .passwordHash("hash")
                .build();
        ReflectionTestUtils.setField(user, "id", 100L);

        Category category = Category.builder()
                .name("카테고리")
                .parent(null)
                .build();
        product = Product.builder()
                .category(category)
                .name("상품")
                .description("설명")
                .price(BigDecimal.valueOf(50_000))
                .stock(20)
                .build();
        ReflectionTestUtils.setField(product, "id", 200L);
    }

    @Test
    void givenValidInput_whenCreateOrder_thenReturnsResult() {
        // Given: 유효한 입력과 저장 결과를 세팅
        OrderCreateServiceDto input = OrderCreateServiceDto.builder()
                .userId(100L)
                .productId(200L)
                .quantity(5)
                .build();
        Purchase saved = Purchase.builder()
                .user(user)
                .product(product)
                .quantity(5)
                .unitPrice(product.getPrice())
                .totalPrice(product.getPrice().multiply(BigDecimal.valueOf(5)))
                .status(PurchaseStatus.PENDING)
                .build();
        ReflectionTestUtils.setField(saved, "id", 301L);
        ReflectionTestUtils.setField(saved, "purchasedAt", LocalDateTime.now());
        when(userJpaRepository.findById(input.getUserId())).thenReturn(Optional.of(user));
        when(productRepository.findById(input.getProductId())).thenReturn(Optional.of(product));
        when(purchaseRepository.save(any(Purchase.class))).thenReturn(saved);

        // When: 주문을 생성한다.
        OrderResultDto result = orderService.createOrder(input);

        // Then: 재고/응답/호출 여부를 검증한다.
        assertThat(product.getStock()).isEqualTo(15);
        assertThat(result.getQuantity()).isEqualTo(5);
        assertThat(result.getTotalPrice()).isEqualByComparingTo(saved.getTotalPrice());
        verify(purchaseRepository).save(any(Purchase.class));
    }

    @Test
    void givenInvalidQuantity_whenCreateOrder_thenThrows() {
        // Given: 수량이 0이라 잘못된 입력을 만든다.
        OrderCreateServiceDto input = OrderCreateServiceDto.builder()
                .userId(100L)
                .productId(200L)
                .quantity(0)
                .build();
        when(userJpaRepository.findById(input.getUserId())).thenReturn(Optional.of(user));
        when(productRepository.findById(input.getProductId())).thenReturn(Optional.of(product));

        // When & Then: INVALID_ORDER_QUANTITY 예외를 검증한다.
        assertThatThrownBy(() -> orderService.createOrder(input))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining(ServiceExceptionCode.INVALID_ORDER_QUANTITY.getMessage());
        assertThat(product.getStock()).isEqualTo(20);
        verify(purchaseRepository, never()).save(any(Purchase.class));
    }
}
