package com.example.study.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class LessonChecklistServiceAAATest {

    // 테스트 대상 서비스 인스턴스를 보관한다.
    // 테스트 대상 서비스 인스턴스를 보관한다.
    private LessonChecklistService lessonChecklistService;

    @BeforeEach
    void setUp() {
        // AAA 패턴에서 Arrange 단계: 서비스 객체를 생성한다.
        lessonChecklistService = new LessonChecklistService();
    }

    @Test
    @DisplayName("경력과 프로젝트 수가 조건을 만족하면 eligible=true를 반환한다")
    void isEligible_returnsTrue_whenThresholdMet() {
        // Arrange: 최소 조건을 충족하는 입력값을 준비한다.
        // 경력 3년을 선언한다.
        int experienceYears = 3;
        // 프로젝트 4개를 선언한다.
        int completedProjects = 4;

        // Act: 서비스 메서드를 호출한다.
        // 서비스 메서드를 호출한다.
        boolean result = lessonChecklistService.isEligible(experienceYears, completedProjects);

        // Assert: true가 반환되는지 검증한다.
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("음수 값이 들어오면 IllegalArgumentException을 던진다")
    void isEligible_throwsException_whenNegativeInput() {
        // Arrange: 음수 경험치를 준비한다.
        // 음수 경험치를 준비한다.
        int experienceYears = -1;
        // 프로젝트 2개를 설정한다.
        int completedProjects = 2;

        // Act & Assert: 예외가 발생하는지 확인한다.
        assertThatThrownBy(() -> lessonChecklistService.isEligible(experienceYears, completedProjects))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("주차 수를 넘기면 예상 학습 시간을 계산한다")
    void estimatePracticeHours_returnsHours_whenWeeksPositive() {
        // Arrange: 4주 입력을 정의한다.
        // 4주 입력값을 선언한다.
        int weeks = 4;

        // Act: 예상 시간을 계산한다.
        int hours = lessonChecklistService.estimatePracticeHours(weeks);

        // Assert: 5시간 * 주 수로 계산되는지 검증한다.
        assertThat(hours).isEqualTo(20);
    }

    @Test
    @DisplayName("주차 수가 0 이하이면 예외가 발생한다")
    void estimatePracticeHours_throwsException_whenWeeksInvalid() {
        // Arrange: 잘못된 입력값을 준비한다.
        // 0주라는 잘못된 입력을 선언한다.
        int weeks = 0;

        // Act & Assert: IllegalArgumentException 여부를 확인한다.
        assertThatThrownBy(() -> lessonChecklistService.estimatePracticeHours(weeks))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
