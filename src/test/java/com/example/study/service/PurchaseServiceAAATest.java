package com.example.study.service;

// 도메인/예외/DTO 의존성을 가져온다.
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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// AAA 패턴 전용 Mockito 테스트 클래스
@ExtendWith(MockitoExtension.class)
class PurchaseServiceAAATest {

    // 외부 의존성을 모두 Mock으로 교체한다.
    @Mock
    private PurchaseRepository purchaseRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private UserJpaRepository userJpaRepository;

    // Mock이 주입된 서비스 인스턴스를 생성한다.
    @InjectMocks
    private PurchaseService purchaseService;

    private User user;
    private Product product;

    // 각 테스트가 공통으로 사용하는 엔티티를 구성한다.
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

    // Arrange-Act-Assert 순서로 성공 케이스를 설명한다.
    @Test
    void placePurchase_successfullyCreatesPurchaseAndDecreasesStock() {
        // Arrange: 요청 DTO와 save 반환값을 준비하고 Mock에 Stubbing 한다.
        PurchaseRequest request = PurchaseRequest.builder()
                .userId(1L)
                .productId(2L)
                .quantity(3)
                .build();
        Purchase savedPurchase = Purchase.builder()
                .user(user)
                .product(product)
                .quantity(3)
                .unitPrice(product.getPrice())
                .totalPrice(product.getPrice().multiply(BigDecimal.valueOf(3)))
                .status(PurchaseStatus.COMPLETED)
                .build();
        when(userJpaRepository.findById(request.getUserId())).thenReturn(Optional.of(user));
        when(productRepository.findById(request.getProductId())).thenReturn(Optional.of(product));
        when(purchaseRepository.save(any(Purchase.class))).thenReturn(savedPurchase);

        // Act: 테스트 대상 메서드를 호출한다.
        PurchaseResponse response = purchaseService.placePurchase(request);

        // Assert: save 인자, 재고 감소, 응답 필드를 검증한다.
        ArgumentCaptor<Purchase> purchaseCaptor = ArgumentCaptor.forClass(Purchase.class);
        verify(purchaseRepository).save(purchaseCaptor.capture());
        assertThat(product.getStock()).isEqualTo(7);
        assertThat(response.getStatus()).isEqualTo(PurchaseStatus.COMPLETED);
        assertThat(response.getQuantity()).isEqualTo(3);
        assertThat(purchaseCaptor.getValue().getTotalPrice())
                .isEqualByComparingTo(savedPurchase.getTotalPrice());
    }

    // 재고 부족으로 실패할 때의 AAA 시나리오를 다룬다.
    @Test
    void placePurchase_insufficientStock_throwsExceptionAndDoesNotSave() {
        // Arrange: 재고보다 많은 수량을 요청하도록 설정한다.
        PurchaseRequest request = PurchaseRequest.builder()
                .userId(1L)
                .productId(2L)
                .quantity(11)
                .build();
        when(userJpaRepository.findById(request.getUserId())).thenReturn(Optional.of(user));
        when(productRepository.findById(request.getProductId())).thenReturn(Optional.of(product));

        // Act & Assert: 예외 메시지와 save 호출 여부를 검증한다.
        assertThatThrownBy(() -> purchaseService.placePurchase(request))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining(ServiceExceptionCode.INSUFFICIENT_PRODUCT_STOCK.getMessage());
        assertThat(product.getStock()).isEqualTo(10);
        verify(purchaseRepository, never()).save(any(Purchase.class));
    }
}
