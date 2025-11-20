package com.example.study.lecture.cleancode.practice;

import java.time.LocalDate;

/**
 * 깊은 중첩 if 문으로 가득한 검증 로직 예제.
 * 라이브 코딩으로 Guard Clause를 도입하는 데 사용한다.
 *
 * TODO(LAB):
 *  1) 중첩 if 대신 Guard Clause 형태로 전환해 가독성을 높이세요.
 *  2) 검증 항목별 예외 메시지를 메서드로 분리하세요.
 *  3) ValidationResult 사용 방식(예외 vs 값 반환)을 일관되게 정리하세요.
 */
public class LegacyOrderValidatorPractice {

    public ValidationResult approveOrder(Order order) {
        if (order != null) {
            if (!order.isDeleted()) {
                if (order.isPaid()) {
                    if (order.shipmentDate() != null) {
                        if (order.shipmentDate().isAfter(LocalDate.now().minusDays(30))) {
                            if (order.itemCount() > 0) {
                                if (order.amount() >= 100) {
                                    return new ValidationResult(true, "ok");
                                } else {
                                    return new ValidationResult(false, "amount too small");
                                }
                            } else {
                                return new ValidationResult(false, "no items");
                            }
                        } else {
                            return new ValidationResult(false, "shipment too late");
                        }
                    } else {
                        return new ValidationResult(false, "need shipment");
                    }
                } else {
                    return new ValidationResult(false, "need payment");
                }
            } else {
                return new ValidationResult(false, "already deleted");
            }
        }
        return new ValidationResult(false, "order missing");
    }

    public record Order(boolean deleted, boolean paid, LocalDate shipmentDate, int itemCount, double amount) {

        public boolean isDeleted() {
            return deleted;
        }

        public boolean isPaid() {
            return paid;
        }
    }

    public record ValidationResult(boolean approved, String message) {
    }
}
