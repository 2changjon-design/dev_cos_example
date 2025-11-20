package com.example.study.lecture.cleancode.spring.answer4;

public interface NotificationGateway {
    void notifyOrder(Long purchaseId, String userEmail, Long productId);
}
