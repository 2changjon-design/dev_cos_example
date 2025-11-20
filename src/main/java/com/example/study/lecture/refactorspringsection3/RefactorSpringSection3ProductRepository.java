package com.example.study.lecture.refactorspringsection3;

import java.util.List;
import java.util.Optional;

public interface RefactorSpringSection3ProductRepository {

    RefactorSpringSection3Product save(RefactorSpringSection3Product product);

    Optional<RefactorSpringSection3Product> findById(Long id);

    List<RefactorSpringSection3Product> findAll();
}
