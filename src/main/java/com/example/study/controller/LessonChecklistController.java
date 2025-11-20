package com.example.study.controller;

import com.example.study.controller.dto.LessonEligibilityRequest;
import com.example.study.controller.dto.LessonEligibilityResponse;
import com.example.study.controller.dto.PracticeHoursResponse;
import com.example.study.service.LessonChecklistService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/lesson")
@Validated
public class LessonChecklistController {

    private final LessonChecklistService lessonChecklistService;

    public LessonChecklistController(LessonChecklistService lessonChecklistService) {
        this.lessonChecklistService = lessonChecklistService;
    }

    @PostMapping("/check-eligibility")
    public LessonEligibilityResponse checkEligibility(@Valid @RequestBody LessonEligibilityRequest request) {
        boolean eligible = lessonChecklistService.isEligible(request.experienceYears(), request.completedProjects());
        String message = eligible ? "축하합니다! 실습을 진행할 준비가 되었습니다." : "추가 경험이 필요합니다.";
        return new LessonEligibilityResponse(eligible, message);
    }

    @GetMapping("/practice-hours")
    public PracticeHoursResponse getPracticeHours(@RequestParam @Min(value = 1, message = "weeks는 1 이상이어야 합니다.") int weeks) {
        int hours = lessonChecklistService.estimatePracticeHours(weeks);
        return new PracticeHoursResponse(weeks, hours);
    }
}
