package com.example.study.lecture.cleancode.answer;

import java.time.LocalDate;

/**
 * Guard Clause 패턴으로 재작성한 주문 검증 예제.
 * 실패 조건을 초기에 끊어내면 중첩 if 없이 규칙을 빠르게 스캔할 수 있고,
 * 새로운 검증이 추가되어도 한 줄씩만 더하면 되므로 변경 비용이 낮다.
 */
public class LegacyOrderValidatorAnswer {

    /**
     * 검증 순서를 한 곳에 모아 읽기 흐름을 명확히 했다.
     * 예외를 던지는 시점이 곧 정책 변경점이므로, 추가 규칙도 여기 한 줄로 표현한다.
     */
    public ValidationResult approveOrder(Order order) {
        validateOrderPresence(order);
        ensureNotDeleted(order);
        ensurePaid(order);
        ensureShipmentDate(order.shipmentDate());
        ensureRecentShipment(order.shipmentDate());
        ensureHasItems(order);
        ensureAmount(order);
        return ValidationResult.ok();
    }

    /**
     * null 체크를 먼저 수행해 이후 로직에서 널 여부를 고민하지 않도록 한다.
     */
    private void validateOrderPresence(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("order missing");
        }
    }

    /**
     * 삭제 여부를 초기에 막아두면 비즈니스적으로 무의미한 상태 전이를 방지할 수 있다.
     */
    private void ensureNotDeleted(Order order) {
        if (order.isDeleted()) {
            throw new IllegalStateException("already deleted");
        }
    }

    /**
     * 결제 여부는 출고 이전 필수 조건이므로 Guard Clause로 명확히 표현한다.
     */
    private void ensurePaid(Order order) {
        if (!order.isPaid()) {
            throw new IllegalStateException("need payment");
        }
    }

    /**
     * 배송일 누락은 이후 검증에서 NPE를 초래하므로 즉시 실패시킨다.
     */
    private void ensureShipmentDate(LocalDate shipmentDate) {
        if (shipmentDate == null) {
            throw new IllegalStateException("need shipment");
        }
    }

    /**
     * 30일 이전 주문은 정책상 자동 거절되므로 날짜 계산을 별도 메서드로 분리했다.
     */
    private void ensureRecentShipment(LocalDate shipmentDate) {
        if (shipmentDate.isBefore(LocalDate.now().minusDays(30))) {
            throw new IllegalStateException("shipment too late");
        }
    }

    /**
     * 주문 수량이 0 이하인 케이스는 사전 조건 위반이라 즉각 예외를 던진다.
     */
    private void ensureHasItems(Order order) {
        if (order.itemCount() <= 0) {
            throw new IllegalStateException("no items");
        }
    }

    /**
     * 최소 금액 정책을 별도 메서드로 분리해 금액 로직 변경 시 영향 범위를 제한한다.
     */
    private void ensureAmount(Order order) {
        if (order.amount() < 100) {
            throw new IllegalStateException("amount too small");
        }
    }

    /**
     * 검증 결과를 명시적으로 표현해 호출부에서 boolean과 메시지를 헷갈리지 않도록 한다.
     */
    public record ValidationResult(boolean approved, String message) {
        public static ValidationResult ok() {
            return new ValidationResult(true, "ok");
        }
    }

    /**
     * 레거시 엔티티 대신 검증에 필요한 필드만 가진 경량 DTO를 사용해 샘플을 단순화했다.
     */
    public record Order(boolean deleted, boolean paid, LocalDate shipmentDate, int itemCount, double amount) {

        public boolean isDeleted() {
            return deleted;
        }

        public boolean isPaid() {
            return paid;
        }
    }
}
