package com.example.study.repository;

import com.example.study.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByName(String name);

    boolean existsByName(String name);

    List<Product> findByCategoryIdOrderByCreatedAtDesc(Long categoryId);

    @Query("SELECT p FROM Product p WHERE p.stock <= :number") // JPQL
    List<Product> findLowStockProducts(@Param("number") int number);

}
