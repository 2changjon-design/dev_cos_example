package com.example.study.lecture.refactorspringsection3answer;

import com.example.study.common.ServiceException;
import com.example.study.common.ServiceExceptionCode;
import com.example.study.lecture.refactorspringsection3.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class RefactorSpringSection3ProductServiceAfterAnswer {

    private final RefactorSpringSection3ProductRepository productRepository;
    private final RefactorSpringSection3ProductMapperAfterAnswer mapper;

    public RefactorSpringSection3ProductServiceAfterAnswer(
            RefactorSpringSection3ProductRepository productRepository,
            RefactorSpringSection3ProductMapperAfterAnswer mapper
    ) {
        this.productRepository = productRepository;
        this.mapper = mapper;
    }

    @Transactional
    public RefactorSpringSection3ProductResponse createProduct(RefactorSpringSection3ProductRequest request) {
        validatePrice(request.price());
        RefactorSpringSection3Product product = mapper.toEntity(request);
        RefactorSpringSection3Product saved = productRepository.save(product);
        return mapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<RefactorSpringSection3ProductResponse> listProducts() {
        return productRepository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional
    public RefactorSpringSection3ProductResponse deactivateProduct(Long productId) {
        RefactorSpringSection3Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_PRODUCT));
        product.deactivate();
        RefactorSpringSection3Product saved = productRepository.save(product);
        return mapper.toResponse(saved);
    }

    @Transactional
    public RefactorSpringSection3ProductResponse updateProduct(Long productId, RefactorSpringSection3ProductRequest request) {
        validatePrice(request.price());
        RefactorSpringSection3Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_PRODUCT));
        mapper.updateEntityFromRequest(request, product);
        RefactorSpringSection3Product saved = productRepository.save(product);
        return mapper.toResponse(saved);
    }

    private void validatePrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ServiceException(ServiceExceptionCode.INVALID_ORDER_QUANTITY);
        }
    }
}
