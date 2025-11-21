package com.example.study.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "코드 리뷰 응답")
public record CodeReviewResponse(

        @Schema(description = "리뷰 ID")
        String reviewId,

        @Schema(description = "언어")
        String language,

        @Schema(description = "리뷰 레벨")
        String reviewLevel,

        @Schema(description = "발견된 이슈 목록")
        List<CodeIssue> issues,

        @Schema(description = "개선 제안 목록")
        List<String> improvements,

        @Schema(description = "코드 품질 점수 (0-100)")
        Integer score,

        @Schema(description = "개선된 코드")
        String improvedCode,

        @Schema(description = "전체 리뷰 내용")
        String fullReview,

        @Schema(description = "리뷰 생성 시간")
        LocalDateTime timestamp,

        @Schema(description = "AI 응답 시간 (ms)")
        Long responseTimeMs
) {
    public static CodeReviewResponse of(
            String reviewId,
            String language,
            String reviewLevel,
            List<CodeIssue> issues,
            List<String> improvements,
            Integer score,
            String improvedCode,
            String fullReview,
            Long responseTimeMs
    ) {
        return new CodeReviewResponse(
                reviewId,
                language,
                reviewLevel,
                issues,
                improvements,
                score,
                improvedCode,
                fullReview,
                LocalDateTime.now(),
                responseTimeMs
        );
    }
}