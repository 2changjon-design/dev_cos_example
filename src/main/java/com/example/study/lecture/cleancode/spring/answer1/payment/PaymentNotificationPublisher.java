package com.example.study.lecture.cleancode.spring.answer1.payment;

public interface PaymentNotificationPublisher {
    void publish(PaymentNotification notification);
}
