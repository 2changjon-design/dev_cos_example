package com.example.study.controller.dto;

import jakarta.validation.constraints.Min;

public record LessonEligibilityRequest(
        @Min(value = 0, message = "경력은 0 이상이어야 합니다.")
        int experienceYears,
        @Min(value = 0, message = "프로젝트 수는 0 이상이어야 합니다.")
        int completedProjects
) {
}
