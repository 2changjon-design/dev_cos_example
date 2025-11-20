package com.example.study.lecture.refactorspringsection2;

import java.time.LocalDateTime;

public class RefactorSpringSection2InventoryRecord {
    private Long id;
    private final Long productId;
    private int quantity;
    private LocalDateTime updatedAt = LocalDateTime.now();

    public RefactorSpringSection2InventoryRecord(Long id, Long productId, int quantity) {
        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void adjustQuantity(int adjustment) {
        this.quantity += adjustment;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
