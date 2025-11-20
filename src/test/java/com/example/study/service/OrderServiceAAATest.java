package com.example.study.service; // 테스트 클래스가 위치한 패키지 선언

import com.example.study.PurchaseStatus; // 주문 상태를 검증하기 위한 enum 임포트
import com.example.study.common.ServiceException; // 예외 타입 임포트
import com.example.study.common.ServiceExceptionCode; // 예외 코드 enum 임포트
import com.example.study.entity.Category; // 카테고리 엔티티 임포트
import com.example.study.entity.Product; // 상품 엔티티 임포트
import com.example.study.entity.Purchase; // 구매 엔티티 임포트
import com.example.study.entity.User; // 사용자 엔티티 임포트
import com.example.study.repository.ProductRepository; // 상품 리포지토리 Mock 생성을 위한 임포트
import com.example.study.repository.PurchaseRepository; // 구매 리포지토리 Mock 생성을 위한 임포트
import com.example.study.repository.UserJpaRepository; // 사용자 리포지토리 Mock 생성을 위한 임포트
import com.example.study.service.dto.OrderCreateServiceDto; // 주문 생성 입력 DTO 임포트
import com.example.study.service.dto.OrderResultDto; // 주문 결과 DTO 임포트
import java.math.BigDecimal; // 금액 계산용 BigDecimal 임포트
import java.time.LocalDateTime; // 주문 시간값 설정용 임포트
import java.util.Optional; // Optional 사용을 위한 임포트
import org.junit.jupiter.api.BeforeEach; // @BeforeEach 어노테이션 임포트
import org.junit.jupiter.api.Test; // @Test 어노테이션 임포트
import org.junit.jupiter.api.extension.ExtendWith; // @ExtendWith 어노테이션 임포트
import org.mockito.ArgumentCaptor; // 인자 캡처를 위한 ArgumentCaptor 임포트
import org.mockito.InjectMocks; // @InjectMocks 어노테이션 임포트
import org.mockito.Mock; // @Mock 어노테이션 임포트
import org.mockito.junit.jupiter.MockitoExtension; // Mockito 확장 임포트
import org.springframework.test.util.ReflectionTestUtils; // 필드 주입을 위한 ReflectionTestUtils 임포트

import static org.assertj.core.api.Assertions.assertThat; // AssertJ 단언 메서드 임포트
import static org.assertj.core.api.Assertions.assertThatThrownBy; // 예외 단언 메서드 임포트
import static org.mockito.ArgumentMatchers.any; // any 매처 임포트
import static org.mockito.Mockito.never; // never 검증 임포트
import static org.mockito.Mockito.verify; // verify 호출 임포트
import static org.mockito.Mockito.when; // when 스텁 메서드 임포트

// OrderService AAA 패턴 테스트
@ExtendWith(MockitoExtension.class) // Mockito 확장을 적용해 Mock 초기화를 수행
class OrderServiceAAATest { // OrderService AAA 테스트 클래스 선언

    @Mock
    private PurchaseRepository purchaseRepository; // PurchaseRepository Mock 필드
    @Mock
    private ProductRepository productRepository; // ProductRepository Mock 필드
    @Mock
    private UserJpaRepository userJpaRepository; // UserJpaRepository Mock 필드
    @InjectMocks
    private OrderService orderService; // 테스트 대상 OrderService 인스턴스

    private User user; // 공통으로 사용할 사용자 엔티티
    private Product product; // 공통으로 사용할 상품 엔티티

    @BeforeEach // 각 테스트 전에 실행되는 설정 메서드
    void setUp() { // 테스트 준비 로직 정의 시작
        user = User.builder() // 사용자 엔티티 빌더 시작
                .name("사용자") // 사용자 이름 설정
                .email("order@example.com") // 이메일 설정
                .passwordHash("hash") // 패스워드 해시 설정
                .build(); // 빌더 완료
        ReflectionTestUtils.setField(user, "id", 100L); // 사용자 ID를 100으로 설정

        Category category = Category.builder() // 카테고리 엔티티 빌더 시작
                .name("카테고리") // 카테고리 이름 설정
                .parent(null) // 상위 카테고리 없음
                .build(); // 빌더 완료
        product = Product.builder() // 상품 엔티티 빌더 시작
                .category(category) // 앞서 만든 카테고리 지정
                .name("상품") // 상품명 지정
                .description("설명") // 설명 입력
                .price(BigDecimal.valueOf(50_000)) // 단가 50,000 지정
                .stock(20) // 재고 20개 설정
                .build(); // 빌더 완료
        ReflectionTestUtils.setField(product, "id", 200L); // 상품 ID를 200으로 지정
    }

    @Test // JUnit 테스트 메서드 표시
    void createOrder_shouldSavePurchaseAndDecreaseStock() { // 성공 시나리오 테스트 메서드 선언
        // Arrange: 입력 DTO, 저장 결과, Mock 반환값 준비
        OrderCreateServiceDto input = OrderCreateServiceDto.builder() // 주문 입력 DTO 빌더 시작
                .userId(100L) // 사용자 ID 100 지정
                .productId(200L) // 상품 ID 200 지정
                .quantity(4) // 수량 4개 지정
                .build(); // DTO 빌더 완료
        Purchase saved = Purchase.builder() // 저장될 Purchase 엔티티 빌더 시작
                .user(user) // 사용자 엔티티 지정
                .product(product) // 상품 엔티티 지정
                .quantity(4) // 수량 4개
                .unitPrice(product.getPrice()) // 단가 설정
                .totalPrice(product.getPrice().multiply(BigDecimal.valueOf(4))) // 총 금액 계산
                .status(PurchaseStatus.PENDING) // 상태를 PENDING으로 설정
                .build(); // 엔티티 빌더 완료
        ReflectionTestUtils.setField(saved, "id", 300L); // 저장 엔티티 ID 세팅
        ReflectionTestUtils.setField(saved, "purchasedAt", LocalDateTime.now()); // 구매 시간 세팅
        when(userJpaRepository.findById(100L)).thenReturn(Optional.of(user)); // 사용자 조회 Mock 설정
        when(productRepository.findById(200L)).thenReturn(Optional.of(product)); // 상품 조회 Mock 설정
        when(purchaseRepository.save(any(Purchase.class))).thenReturn(saved); // 저장 시 반환값 Mock 설정

        // Act: 서비스 호출
        OrderResultDto result = orderService.createOrder(input); // 서비스 메서드를 호출하여 결과 획득

        // Assert: 재고 감소, 응답 값, save 인자를 검증한다.
        ArgumentCaptor<Purchase> captor = ArgumentCaptor.forClass(Purchase.class); // ArgumentCaptor 생성
        verify(purchaseRepository).save(captor.capture()); // save가 호출되고 인자를 캡처했는지 검증
        assertThat(product.getStock()).isEqualTo(16); // 재고가 16으로 감소했는지 확인
        assertThat(result.getStatus()).isEqualTo(PurchaseStatus.PENDING); // 응답 상태가 PENDING인지 확인
        assertThat(captor.getValue().getTotalPrice()).isEqualTo(saved.getTotalPrice()); // 저장된 총 금액 검증
    }

    @Test // 두 번째 테스트 메서드 표시
    void createOrder_shouldThrowException_whenStockInsufficient() { // 재고 부족 시 테스트
        // Arrange: 재고보다 많은 수량 요청
        OrderCreateServiceDto input = OrderCreateServiceDto.builder() // DTO 빌더 시작
                .userId(100L) // 사용자 ID 설정
                .productId(200L) // 상품 ID 설정
                .quantity(99) // 과다 수량 설정
                .build(); // DTO 빌더 완료
        when(userJpaRepository.findById(100L)).thenReturn(Optional.of(user)); // 사용자 조회 Stubbing
        when(productRepository.findById(200L)).thenReturn(Optional.of(product)); // 상품 조회 Stubbing

        // Act & Assert: 예외 메시지, save 미호출, 재고 유지 확인
        assertThatThrownBy(() -> orderService.createOrder(input)) // 서비스 호출 시 예외 발생 여부 확인
                .isInstanceOf(ServiceException.class) // 예외 타입 검증
                .hasMessageContaining(ServiceExceptionCode.INSUFFICIENT_PRODUCT_STOCK.getMessage()); // 메시지 검증
        assertThat(product.getStock()).isEqualTo(20); // 재고가 그대로인지 확인
        verify(purchaseRepository, never()).save(any(Purchase.class)); // save가 호출되지 않았는지 검증
    }
}
