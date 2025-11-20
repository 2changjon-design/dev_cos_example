package com.example.study.service;

import com.example.study.PurchaseStatus;
import com.example.study.common.ServiceException;
import com.example.study.common.ServiceExceptionCode;
import com.example.study.controller.dto.PurchaseRequest;
import com.example.study.controller.dto.PurchaseResponse;
import com.example.study.entity.Category;
import com.example.study.entity.Product;
import com.example.study.entity.Purchase;
import com.example.study.entity.User;
import com.example.study.repository.ProductRepository;
import com.example.study.repository.PurchaseRepository;
import com.example.study.repository.UserJpaRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Given-When-Then 서술 방식 전용 테스트 클래스
@ExtendWith(MockitoExtension.class)
class PurchaseServiceGWTTest {

    @Mock
    private PurchaseRepository purchaseRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private UserJpaRepository userJpaRepository;
    @InjectMocks
    private PurchaseService purchaseService;

    private User user;
    private Product product;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .name("테스트 사용자")
                .email("test@example.com")
                .passwordHash("hash")
                .build();
        Category category = Category.builder()
                .name("노트북")
                .parent(null)
                .build();
        product = Product.builder()
                .category(category)
                .name("테스트 노트북")
                .description("사양: i7 / 16GB")
                .price(BigDecimal.valueOf(1_500_000))
                .stock(10)
                .build();
    }

    @Test
    void givenValidRequest_whenPlacePurchase_thenReturnsResponse() {
        // Given: 정상 주문을 위한 요청과 저장 결과를 준비한다.
        PurchaseRequest request = PurchaseRequest.builder()
                .userId(1L)
                .productId(2L)
                .quantity(2)
                .build();
        Purchase savedPurchase = Purchase.builder()
                .user(user)
                .product(product)
                .quantity(2)
                .unitPrice(product.getPrice())
                .totalPrice(product.getPrice().multiply(BigDecimal.valueOf(2)))
                .status(PurchaseStatus.COMPLETED)
                .build();
        when(userJpaRepository.findById(request.getUserId())).thenReturn(Optional.of(user));
        when(productRepository.findById(request.getProductId())).thenReturn(Optional.of(product));
        when(purchaseRepository.save(any(Purchase.class))).thenReturn(savedPurchase);

        // When: 서비스를 호출한다.
        PurchaseResponse response = purchaseService.placePurchase(request);

        // Then: 재고 감소와 응답 필드를 검증한다.
        assertThat(product.getStock()).isEqualTo(8);
        assertThat(response.getQuantity()).isEqualTo(2);
        assertThat(response.getTotalPrice()).isEqualByComparingTo(savedPurchase.getTotalPrice());
        verify(purchaseRepository).save(any(Purchase.class));
    }

    @Test
    void givenQuantityExceedingStock_whenPlacePurchase_thenThrowsException() {
        // Given: 재고보다 큰 수량을 요청한다.
        PurchaseRequest request = PurchaseRequest.builder()
                .userId(1L)
                .productId(2L)
                .quantity(99)
                .build();
        when(userJpaRepository.findById(request.getUserId())).thenReturn(Optional.of(user));
        when(productRepository.findById(request.getProductId())).thenReturn(Optional.of(product));

        // When & Then: ServiceException 발생과 save 미호출을 확인한다.
        assertThatThrownBy(() -> purchaseService.placePurchase(request))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining(ServiceExceptionCode.INSUFFICIENT_PRODUCT_STOCK.getMessage());
        assertThat(product.getStock()).isEqualTo(10);
        verify(purchaseRepository, never()).save(any(Purchase.class));
    }
}
