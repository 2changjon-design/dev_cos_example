package com.example.study.lecture.cleancode.answer;

import java.time.Duration;
import java.time.Instant;

/**
 * 불필요한 주석을 제거하고, 꼭 필요한 배경만 남긴 버전.
 * 메서드 이름/파라미터를 통해 의도를 드러낼 수 있는 부분은 코드 자체에 맡기고,
 * 외부 제약이나 비즈니스 배경처럼 코드에서 유추할 수 없는 맥락만 주석으로 남겼다.
 */
public class CommentSmellAnswer {

    /**
     * 할인율을 인자로 분리해두면 10%가 하드코딩되지 않아 테스트와 전략 확장이 쉽다.
     */
    public int calculateDiscountPrice(int price) {
        return applyDiscount(price, 0.1);
    }

    /**
     * 계산식이 반복될 수 있어 별도 메서드로 빼 재사용성과 가독성을 동시에 확보했다.
     */
    private int applyDiscount(int price, double rate) {
        return (int) (price * (1 - rate));
    }

    /**
     * 도메인 정책 상 TTL 기반 만료 규칙을 분명히 보여주기 위해 의도 기반 네이밍을 사용한다.
     */
    public boolean isExpired(Instant createdAt, Duration ttl) {
        return createdAt.plus(ttl).isBefore(Instant.now());
    }

    public void syncLegacySystem() {
        // 외부 시스템 교체 시까지는 v1 API를 동기 호출해야 한다. (비동기 호출 시 정합성 깨짐)
        System.out.println("call legacy API v1");
    }

    public void flushBeforeExternalCall() {
        // 실제 운영 장애 이슈 회피 목적. (PG사 버그 추적 JIRA-582 참조)
        System.out.println("flush()");
        System.out.println("call external()");
    }
}
