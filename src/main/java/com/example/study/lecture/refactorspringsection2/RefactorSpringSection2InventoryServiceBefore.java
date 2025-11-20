package com.example.study.lecture.refactorspringsection2;

import com.example.study.common.ServiceException;
import com.example.study.common.ServiceExceptionCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Section2 Spring Service BEFORE
 *
 * 리팩토링 힌트:
 * 1. validate(), loadRecord(), applyAdjustment(), buildResponse() 등으로 메서드를 분리해 SRP를 지키자.
 * 2. 재고가 없을 때 새 레코드를 생성하는 로직은 별도 Factory/Policy로 추출하면 테스트가 쉬워진다 (answer 참고).
 * 3. Domain -> Response 변환은 MapStruct Mapper에 맡겨 서비스 로직을 단순화하자.
 */
@Service
public class RefactorSpringSection2InventoryServiceBefore {

    private final RefactorSpringSection2InventoryRepository inventoryRepository;

    public RefactorSpringSection2InventoryServiceBefore(RefactorSpringSection2InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional
    public RefactorSpringSection2InventoryResponse adjustInventory(RefactorSpringSection2InventoryRequest request) {
        if (request.productId() == null) {
            throw new ServiceException(ServiceExceptionCode.NOT_FOUND_PRODUCT);
        }
        if (request.adjustmentQuantity() == 0) {
            throw new ServiceException(ServiceExceptionCode.INVALID_ORDER_QUANTITY);
        }
        if (request.reason() == null || request.reason().isBlank()) {
            throw new ServiceException(ServiceExceptionCode.INVALID_ORDER_QUANTITY);
        }

        RefactorSpringSection2InventoryRecord record = inventoryRepository.findByProductId(request.productId())
                .orElseGet(() -> new RefactorSpringSection2InventoryRecord(null, request.productId(), 0));

        int newQuantity = record.getQuantity() + request.adjustmentQuantity();
        if (newQuantity < 0) {
            throw new ServiceException(ServiceExceptionCode.INVALID_ORDER_QUANTITY);
        }

        record.adjustQuantity(request.adjustmentQuantity());
        RefactorSpringSection2InventoryRecord saved = inventoryRepository.save(record);
        return RefactorSpringSection2InventoryResponse.from(saved);
    }
}
