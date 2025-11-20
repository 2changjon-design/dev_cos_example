package com.example.study.lecture.refactorspringsection1;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Spring Boot 리팩토링 실습용 Order 엔티티(Mock).
 */
public class RefactorSection1Order {

    private Long id;
    private Long userId;
    private final List<RefactorSection1OrderLine> lines = new ArrayList<>();
    private BigDecimal totalPrice = BigDecimal.ZERO;
    private LocalDateTime createdAt = LocalDateTime.now();

    public RefactorSection1Order(Long id, Long userId) {
        this.id = id;
        this.userId = userId;
    }

    public void addLine(RefactorSection1OrderLine line) {
        lines.add(line);
        totalPrice = totalPrice.add(
                line.unitPrice().multiply(BigDecimal.valueOf(line.quantity()))
        );
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public List<RefactorSection1OrderLine> getLines() {
        return lines;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
