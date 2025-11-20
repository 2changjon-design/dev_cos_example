package com.example.study.lecture.refactorspringsection2answer;

import com.example.study.common.ServiceException;
import com.example.study.common.ServiceExceptionCode;
import com.example.study.lecture.refactorspringsection2.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Section2 After Service 정답 예시.
 */
@Service
public class RefactorSpringSection2InventoryServiceAfterAnswer {

    private final RefactorSpringSection2InventoryRepository repository;
    private final RefactorSpringSection2InventoryMapperAfterAnswer mapper;

    public RefactorSpringSection2InventoryServiceAfterAnswer(
            RefactorSpringSection2InventoryRepository repository,
            RefactorSpringSection2InventoryMapperAfterAnswer mapper
    ) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public RefactorSpringSection2InventoryResponse adjustInventory(RefactorSpringSection2InventoryRequest request) {
        validate(request);
        RefactorSpringSection2InventoryRecord record = loadRecord(request.productId());
        applyAdjustment(record, request.adjustmentQuantity());
        RefactorSpringSection2InventoryRecord saved = repository.save(record);
        return mapper.toResponse(saved);
    }

    private void validate(RefactorSpringSection2InventoryRequest request) {
        if (request.productId() == null) {
            throw new ServiceException(ServiceExceptionCode.NOT_FOUND_PRODUCT);
        }
        if (request.adjustmentQuantity() == 0) {
            throw new ServiceException(ServiceExceptionCode.INVALID_ORDER_QUANTITY);
        }
        if (request.reason() == null || request.reason().isBlank()) {
            throw new ServiceException(ServiceExceptionCode.INVALID_ORDER_QUANTITY);
        }
    }

    private RefactorSpringSection2InventoryRecord loadRecord(Long productId) {
        return repository.findByProductId(productId)
                .orElseGet(() -> new RefactorSpringSection2InventoryRecord(null, productId, 0));
    }

    private void applyAdjustment(RefactorSpringSection2InventoryRecord record, int adjustmentQuantity) {
        int newQuantity = record.getQuantity() + adjustmentQuantity;
        if (newQuantity < 0) {
            throw new ServiceException(ServiceExceptionCode.INVALID_ORDER_QUANTITY);
        }
        record.adjustQuantity(adjustmentQuantity);
    }
}
