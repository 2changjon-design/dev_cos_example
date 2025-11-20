package com.example.study.lecture.refactorspringsection2;

import java.util.Optional;

public interface RefactorSpringSection2InventoryRepository {

    RefactorSpringSection2InventoryRecord save(RefactorSpringSection2InventoryRecord record);

    Optional<RefactorSpringSection2InventoryRecord> findByProductId(Long productId);
}
