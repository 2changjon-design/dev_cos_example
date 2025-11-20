package com.example.study.lecture.refactorspringsection2;

import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class RefactorSpringSection2InventoryRepositoryImpl implements RefactorSpringSection2InventoryRepository {

    private final AtomicLong sequence = new AtomicLong(1L);
    private final Map<Long, RefactorSpringSection2InventoryRecord> storage = new ConcurrentHashMap<>();

    @Override
    public RefactorSpringSection2InventoryRecord save(RefactorSpringSection2InventoryRecord record) {
        if (record.getId() == null) {
            record.setId(sequence.getAndIncrement());
        }
        storage.put(record.getProductId(), record);
        return record;
    }

    @Override
    public Optional<RefactorSpringSection2InventoryRecord> findByProductId(Long productId) {
        return Optional.ofNullable(storage.get(productId));
    }
}
