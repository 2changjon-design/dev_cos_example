package com.example.study.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(description = "코드 리뷰 요청")
public record CodeReviewRequest(

        @Schema(description = "리뷰할 코드", example = "public class Test { ... }")
        @NotBlank(message = "코드는 필수입니다")
        String code,

        @Schema(description = "프로그래밍 언어", example = "Java")
        @NotBlank(message = "언어는 필수입니다")
        @Pattern(regexp = "Java|Python|JavaScript", message = "지원하는 언어: Java, Python, JavaScript")
        String language,

        @Schema(description = "리뷰 레벨", example = "detailed", allowableValues = {"basic", "detailed", "expert"})
        @Pattern(regexp = "basic|detailed|expert", message = "리뷰 레벨: basic, detailed, expert")
        String reviewLevel
) {
    // 기본값 설정
    public CodeReviewRequest {
        if (reviewLevel == null || reviewLevel.isBlank()) {
            reviewLevel = "detailed";
        }
    }
}