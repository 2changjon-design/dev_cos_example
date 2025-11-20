package com.example.study.lecture.cleancode.answer;

import java.util.List;

/**
 * ShippingServicePractice를 리팩토링한 버전.
 * - 명확한 메서드/변수 이름으로 요구사항을 자연어에 가깝게 표현
 * - 계산 로직을 재사용 가능한 메서드로 추출(DRY)
 * - 의미 있는 상수 분리로 값 변경 시 영향 범위를 최소화
 */
public class ShippingServiceAnswer {

    private static final double BASE_RATE = 0.07;
    private static final double HEAVY_ITEM_LIMIT = 15.0;
    private static final double HEAVY_ITEM_SURCHARGE = 3.0;
    private static final double NORMAL_DELIVERY_FEE = 4.5;
    private static final double URGENT_DELIVERY_FEE = 12.5;

    /**
     * 외부에 노출되는 단일 계산 진입점. 유효성 검사 → 기본 비용 → 할인 → 배송 옵션 순서가 한눈에 보인다.
     */
    public double calculateShippingFee(List<Item> items, int userPoint, DeliverySpeed deliverySpeed) {
        validateItems(items);

        double baseCost = calculateBaseShippingCost(items);
        double discountedCost = applyUserPointDiscount(baseCost, userPoint);
        return addDeliverySurcharge(discountedCost, deliverySpeed);
    }

    /**
     * 주문 객체에 부가 효과를 주는 케이스를 분리해, 순수 계산 메서드와의 결합을 최소화했다.
     */
    public void calculateAndAssignShippingFee(OrderSheet orderSheet, DeliverySpeed deliverySpeed) {
        double shippingFee = calculateShippingFee(orderSheet.items(), orderSheet.userPoint(), deliverySpeed);
        orderSheet.setShippingFee(shippingFee);
    }

    /**
     * 입력 방어를 조기에 수행해 이후 로직이 null 여부를 신경 쓰지 않도록 한다.
     */
    private void validateItems(List<Item> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("아이템 목록이 비어 있습니다.");
        }
    }

    /**
     * 기본 비용 계산을 메서드로 추출하면, 무게/가격 정책이 바뀌어도 다른 단계에 영향이 없다.
     */
    private double calculateBaseShippingCost(List<Item> items) {
        double baseCost = 0;
        for (Item item : items) {
            double itemCost = item.price() * BASE_RATE;
            if (item.weight() > HEAVY_ITEM_LIMIT) {
                itemCost += HEAVY_ITEM_SURCHARGE;
            }
            baseCost += itemCost;
        }
        return baseCost;
    }

    /**
     * 사용자 포인트 할인 정책을 한 곳에서 관리해 추후 구간/비율 조정이 쉬워진다.
     */
    private double applyUserPointDiscount(double shippingCost, int userPoint) {
        if (userPoint > 3000) {
            return shippingCost * 0.8;
        }
        return shippingCost;
    }

    /**
     * 배송 속도 별 추가 요금을 switch로 분리해, 새로운 속도가 추가될 때 컴파일 타임 경고를 받을 수 있다.
     */
    private double addDeliverySurcharge(double shippingCost, DeliverySpeed deliverySpeed) {
        return shippingCost + switch (deliverySpeed) {
            case NORMAL -> NORMAL_DELIVERY_FEE;
            case URGENT -> URGENT_DELIVERY_FEE;
        };
    }

    /**
     * 계산에 필요한 정보만 담은 경량 DTO라 record로 선언해 접근을 단순화했다.
     */
    public record Item(String name, double price, double weight) {
    }

    /**
     * 배송 속도는 제한된 값 집합이므로 enum으로 선언해 잘못된 문자열 입력을 원천 차단했다.
     */
    public enum DeliverySpeed {
        NORMAL,
        URGENT
    }

    /**
     * 배송비 계산에 필요한 최소 정보만 가진 불변 필드 OrderSheet.
     * 세터는 배송비 반영 시점만 열어두고 나머지 데이터는 생성자로 고정한다.
     */
    public static class OrderSheet {
        private final List<Item> items;
        private final int userPoint;
        private double shippingFee;

        public OrderSheet(List<Item> items, int userPoint) {
            this.items = items;
            this.userPoint = userPoint;
        }

        public List<Item> items() {
            return items;
        }

        public int userPoint() {
            return userPoint;
        }

        public double shippingFee() {
            return shippingFee;
        }

        public void setShippingFee(double shippingFee) {
            this.shippingFee = shippingFee;
        }
    }
}
