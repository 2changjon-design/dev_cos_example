package com.example.study.lecture.cleancode.practice;

import java.util.List;

/**
 * 의도적으로 나쁜 냄새를 심어둔 배송 서비스 예제.
 * - 축약형 메서드/변수 네이밍
 * - 중복된 배송비 계산 로직
 * - 불필요한 주석
 *
 * TODO(LAB):
 *  1) calcShipFee/변수 이름을 역할이 드러나도록 리팩토링하세요.
 *  2) 일반/긴급 배송 계산의 중복을 헬퍼 메서드로 추출하세요.
 *  3) 배송 정책 숫자를 상수 혹은 enum으로 정리하세요.
 */
public class ShippingServicePractice {

    // 메서드 이름만 봐서는 어떤 배송 정책인지 파악하기 어렵다.
    public double calcShipFee(List<ItemData> d_list, int p, boolean urg) {
        if (d_list == null || d_list.isEmpty()) {
            throw new IllegalArgumentException("need items");
        }

        double total = 0;
        for (ItemData d : d_list) {
            total += d.price();
        }

        double shipFee = total * 0.07;
        if (p > 3000) {
            shipFee *= 0.8;
        }

        // 배송비를 계산한다.
        if (urg) {
            shipFee += 12.5;
        } else {
            shipFee += 4.5;
        }

        return shipFee;
    }

    // 일반 배송 처리 (실제로는 procUrgShip와 완전히 동일한 로직이 반복된다)
    public double procNormalShip(List<ItemData> items) {
        double shipFee = 0;
        for (ItemData item : items) {
            shipFee += item.price() * 0.07;
            if (item.weight() > 15) {
                shipFee += 3;
            }
        }
        shipFee += 4.5;
        return shipFee;
    }

    public double procUrgShip(List<ItemData> items) {
        double shipFee = 0;
        for (ItemData item : items) {
            shipFee += item.price() * 0.07;
            if (item.weight() > 15) {
                shipFee += 3;
            }
        }
        shipFee += 12.5;
        return shipFee;
    }

    public record ItemData(String nm, double price, double weight) {
    }
}
