package com.example.study.lecture.refactorspringsection1;

import java.util.Optional;

public interface RefactorSection1OrderRepository {

    RefactorSection1Order save(RefactorSection1Order order);

    Optional<RefactorSection1Order> findById(Long orderId);
}
