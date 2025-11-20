package com.example.study.lecture.refactorspringsection1;

import org.springframework.web.bind.annotation.RestController;

/**
 * After 버전 컨트롤러 (실습 시 직접 작성).
 *
 * TODO 가이드:
 * - After Answer처럼 Controller는 Service 한 개만 의존하고 Request/Response mapping만 수행하도록 하자.
 * - URI, HTTP Method, ResponseEntity 빌더를 공통화해 테스트 가능성을 높인다.
 * - 예외 처리 책임을 Controller 밖으로 분리해 재사용 가능한 구조를 만들어보자.
 */
@RestController
public class RefactorSection1OrderControllerAfter {
    // TODO: Controller 분리/DTO 맵핑 등을 직접 구현해보세요.
}
