package com.example.study.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "리뷰 비교 결과")
public record ReviewComparison(

        @Schema(description = "현재 리뷰 ID")
        String currentReviewId,

        @Schema(description = "이전 리뷰 ID")
        String previousReviewId,

        @Schema(description = "현재 점수")
        Integer currentScore,

        @Schema(description = "이전 점수")
        Integer previousScore,

        @Schema(description = "점수 변화")
        Integer scoreDifference,

        @Schema(description = "개선도 (%)")
        Double improvementRate,

        @Schema(description = "해결된 이슈 수")
        Integer resolvedIssues,

        @Schema(description = "새로 발견된 이슈 수")
        Integer newIssues,

        @Schema(description = "비교 분석")
        String analysis
) {
    public static ReviewComparison compare(
            CodeReviewResponse current,
            CodeReviewResponse previous
    ) {
        int scoreDiff = current.score() - previous.score();
        double improvementRate = previous.score() > 0
                ? ((double) scoreDiff / previous.score()) * 100
                : 0.0;

        int resolvedIssues = Math.max(0, previous.issues().size() - current.issues().size());
        int newIssues = Math.max(0, current.issues().size() - previous.issues().size());

        String analysis = generateAnalysis(scoreDiff, resolvedIssues, newIssues);

        return new ReviewComparison(
                current.reviewId(),
                previous.reviewId(),
                current.score(),
                previous.score(),
                scoreDiff,
                improvementRate,
                resolvedIssues,
                newIssues,
                analysis
        );
    }

    private static String generateAnalysis(int scoreDiff, int resolved, int newIssues) {
        if (scoreDiff > 10) {
            return String.format("코드가 크게 개선되었습니다! %d개의 이슈가 해결되었습니다.", resolved);
        } else if (scoreDiff > 0) {
            return "코드가 조금 개선되었습니다.";
        } else if (scoreDiff == 0) {
            return "코드 품질이 동일합니다.";
        } else {
            return String.format("코드가 악화되었습니다. %d개의 새로운 이슈가 발견되었습니다.", newIssues);
        }
    }
}