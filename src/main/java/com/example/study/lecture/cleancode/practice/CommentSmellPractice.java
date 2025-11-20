package com.example.study.lecture.cleancode.practice;

import java.time.Duration;
import java.time.Instant;

/**
 * 주석 냄새를 확인하기 위한 예제.
 *
 * TODO(LAB):
 *  1) 코드로 의도를 표현할 수 있는 주석을 제거하고 메서드/변수명을 개선하세요.
 *  2) 실제 동작과 다른 주석을 맞추거나 삭제하세요.
 *  3) TODO 주석에는 맥락/담당자를 추가하거나 즉시 해결하세요.
 */
public class CommentSmellPractice {

    /**
     * 할인율을 적용해 가격을 계산한다.
     * 아래 로직과 완전히 동일한 내용을 반복 설명하는 주석이다.
     */
    public int calcDiscountPrice(int price) {
        // 가격에 10% 할인율을 적용한다.
        return (int) (price * 0.9);
    }

    /**
     * 주석과 실제 로직이 달라지는 예.
     * 주석은 "3일"이라고 설명하지만 실제로는 48시간이다.
     */
    public boolean isExpired(Instant createdAt) {
        // 생성 후 3일이 지나면 만료된다고 가정
        return createdAt.plus(Duration.ofHours(48)).isBefore(Instant.now());
    }

    /**
     * TODO가 방치된 상태: 누가, 언제, 무엇을 해야 하는지 알 수 없다.
     */
    public void syncLegacySystem() {
        // TODO: 리팩토링
        System.out.println("call legacy API v1");
    }

    /**
     * 좋은 주석이 필요한 상황도 보여주기 위해 배치.
     */
    public void flushBeforeExternalCall() {
        // 외부 PG사의 버그로 인해, ID를 먼저 flush하지 않으면 중복 결제가 발생한다.
        System.out.println("flush()");
        System.out.println("call external()");
    }
}
