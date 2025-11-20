package com.example.study.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class LessonChecklistServiceGWTTest {

    // 테스트 대상 서비스
    // 테스트 대상 서비스
    private LessonChecklistService lessonChecklistService;

    @BeforeEach
    void setUp() {
        // Given: 각 테스트마다 새로운 서비스 인스턴스를 생성한다.
        lessonChecklistService = new LessonChecklistService();
    }

    @Test
    @DisplayName("[GWT] 조건 충족 시 eligible=true")
    void givenValidExperience_whenIsEligible_thenReturnsTrue() {
        // Given: 경험 2년, 프로젝트 3개의 입력값을 설정한다.
        // 경력 2년을 정의한다.
        int experienceYears = 2;
        // 프로젝트 3개를 정의한다.
        int completedProjects = 3;

        // When: isEligible를 호출한다.
        // When 단계: 메서드를 호출한다.
        boolean result = lessonChecklistService.isEligible(experienceYears, completedProjects);

        // Then: true를 기대한다.
        // Then 단계: true인지 검증한다.
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("[GWT] 음수 입력 시 예외")
    void givenNegativeProjects_whenIsEligible_thenThrows() {
        // Given: 프로젝트 수가 음수다.
        // 경험 1년을 준비한다.
        int experienceYears = 1;
        // 프로젝트 -5로 잘못된 값을 설정한다.
        int completedProjects = -5;

        // When & Then: 호출 시 IllegalArgumentException 발생.
        assertThatThrownBy(() -> lessonChecklistService.isEligible(experienceYears, completedProjects))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("[GWT] practiceHours 계산")
    void givenWeeks_whenEstimatePracticeHours_thenReturnsHours() {
        // Given: 3주 입력을 제공한다.
        // 3주 입력을 선언한다.
        int weeks = 3;

        // When: practiceHours를 계산한다.
        // When 단계: practiceHours 계산.
        int hours = lessonChecklistService.estimatePracticeHours(weeks);

        // Then: 15시간 결과를 검증한다.
        // Then 단계: 15시간인지 확인한다.
        assertThat(hours).isEqualTo(15);
    }

    @Test
    @DisplayName("[GWT] practiceHours 입력 검증")
    void givenInvalidWeeks_whenEstimatePracticeHours_thenThrows() {
        // Given: 음수 주차 입력을 설정한다.
        // 잘못된 주차 입력을 선언한다.
        int weeks = -1;

        // When & Then: 예외가 발생해야 한다.
        assertThatThrownBy(() -> lessonChecklistService.estimatePracticeHours(weeks))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
