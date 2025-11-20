package com.example.study.lecture.refactorspringsection1;

import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 강의용 인메모리 구현체.
 * 실제 프로젝트에서는 JPA Repository 등으로 교체하면 된다.
 */
@Repository
public class RefactorSection1OrderRepositoryImpl implements RefactorSection1OrderRepository {

    private final AtomicLong sequence = new AtomicLong(1L);
    private final Map<Long, RefactorSection1Order> storage = new ConcurrentHashMap<>();

    @Override
    public RefactorSection1Order save(RefactorSection1Order order) {
        Long orderId = order.getId() != null ? order.getId() : sequence.getAndIncrement();
        RefactorSection1Order persisted = new RefactorSection1Order(orderId, order.getUserId());
        for (RefactorSection1OrderLine line : order.getLines()) {
            persisted.addLine(line);
        }
        storage.put(orderId, persisted);
        return persisted;
    }

    @Override
    public Optional<RefactorSection1Order> findById(Long orderId) {
        return Optional.ofNullable(storage.get(orderId));
    }
}
