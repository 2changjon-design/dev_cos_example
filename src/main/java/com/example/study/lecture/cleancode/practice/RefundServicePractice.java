package com.example.study.lecture.cleancode.practice;

/**
 * 네이밍 개선 실습용 RefundService.
 * 축약형 이름과 모호한 메서드명을 그대로 유지했다.
 *
 * TODO(LAB):
 *  1) 메서드/변수 이름을 도메인 용어로 바꾸고, 입력 검증을 별도 메서드로 뽑으세요.
 *  2) 필요한 경우 반환 타입을 만들어 환불 결과를 표현해 보세요.
 */
public class RefundServicePractice {

    public void procPartRef(Long id, Double amt) {
        if (id == null || amt == null || amt <= 0) {
            throw new IllegalArgumentException("Invalid input");
        }

        double rem = calcRem(id, amt);
        saveRef(id, amt, rem);
    }

    private double calcRem(Long id, Double amt) {
        // ... 실제 로직 대신 단순 값
        return 100.0 - amt;
    }

    private void saveRef(Long id, Double amt, Double rem) {
        // ... 데이터 저장 로직
    }
}
