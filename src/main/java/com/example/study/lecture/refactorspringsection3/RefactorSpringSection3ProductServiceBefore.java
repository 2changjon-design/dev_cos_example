package com.example.study.lecture.refactorspringsection3;

import com.example.study.common.ServiceException;
import com.example.study.common.ServiceExceptionCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Section3 서비스 BEFORE.
 *
 * 리팩토링 힌트:
 * 1. 가격 검증, 엔티티 생성/갱신, Response 변환을 각각 메서드나 컴포넌트로 분리하자 (After Answer 참고).
 * 2. MapStruct Mapper를 이용하면 Request↔Entity↔Response 변환을 일관성 있게 처리할 수 있다.
 * 3. Deactivate/Update 같은 도메인 동작은 명시적 도메인 메서드나 정책 클래스로 추출해 테스트 범위를 좁혀보자.
 */
@Service
public class RefactorSpringSection3ProductServiceBefore {

    private final RefactorSpringSection3ProductRepository productRepository;

    public RefactorSpringSection3ProductServiceBefore(RefactorSpringSection3ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public RefactorSpringSection3ProductResponse createProduct(RefactorSpringSection3ProductRequest request) {
        if (request.price().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ServiceException(ServiceExceptionCode.INVALID_ORDER_QUANTITY);
        }
        RefactorSpringSection3Product product = new RefactorSpringSection3Product(null, request.name(), request.price());
        RefactorSpringSection3Product saved = productRepository.save(product);
        return RefactorSpringSection3ProductResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<RefactorSpringSection3ProductResponse> listProducts() {
        return productRepository.findAll().stream()
                .map(RefactorSpringSection3ProductResponse::from)
                .toList();
    }

    @Transactional
    public RefactorSpringSection3ProductResponse deactivateProduct(Long productId) {
        RefactorSpringSection3Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_PRODUCT));
        product.deactivate();
        RefactorSpringSection3Product saved = productRepository.save(product);
        return RefactorSpringSection3ProductResponse.from(saved);
    }
}
