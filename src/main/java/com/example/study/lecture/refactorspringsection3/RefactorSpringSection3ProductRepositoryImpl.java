package com.example.study.lecture.refactorspringsection3;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class RefactorSpringSection3ProductRepositoryImpl implements RefactorSpringSection3ProductRepository {

    private final AtomicLong sequence = new AtomicLong(1L);
    private final ConcurrentHashMap<Long, RefactorSpringSection3Product> storage = new ConcurrentHashMap<>();

    @Override
    public RefactorSpringSection3Product save(RefactorSpringSection3Product product) {
        if (product.getId() == null) {
            product.setId(sequence.getAndIncrement());
        }
        storage.put(product.getId(), product);
        return product;
    }

    @Override
    public Optional<RefactorSpringSection3Product> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<RefactorSpringSection3Product> findAll() {
        return new ArrayList<>(storage.values());
    }
}
