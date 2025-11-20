package com.example.study.lecture.refactorspringsection3;

import java.math.BigDecimal;

public class RefactorSpringSection3Product {

    private Long id;
    private String name;
    private BigDecimal price;
    private boolean active = true;

    public RefactorSpringSection3Product(Long id, String name, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public boolean isActive() {
        return active;
    }

    public void deactivate() {
        this.active = false;
    }
}
