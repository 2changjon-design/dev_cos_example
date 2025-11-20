package com.example.study.lecture.refactorspringsection1;

import org.springframework.stereotype.Service;

/**
 * After 버전은 학습자가 직접 구현하도록 비워둔다.
 *
 * TODO 가이드:
 * - validateRequest(), buildOrder(), enforceTotalPriceLimit(), toResponse() 같은 메서드로 단계별 로직을 분리하자.
 * - After Answer에서는 MapStruct Mapper + ServiceException을 이용해 재사용 가능한 구조를 보여준다.
 * - 서비스는 "흐름"만 담당하고, 세부 구현은 협력 객체(Validator/Mapper)로 위임하는 구조를 목표로 삼자.
 */
@Service
public class RefactorSection1OrderServiceAfter {
    // 구현은 수업 중 실습으로 작성합니다.
}
