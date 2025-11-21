package com.example.study.controller;

import com.example.study.controller.dto.CodeReviewRequest;
import com.example.study.controller.dto.CodeReviewResponse;
import com.example.study.controller.dto.ReviewComparison;
import com.example.study.service.CodeReviewService;
import com.example.study.service.dto.CodeReview;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Code Review", description = "AI 코드 리뷰 API")
@RestController
@RequestMapping("/api/code-review")
@RequiredArgsConstructor
@Slf4j
public class CodeReviewController {

    private final CodeReviewService codeReviewService;

    @Operation(
            summary = "코드 리뷰 분석",
            description = "제공된 코드를 분석하고 개선 사항을 제안합니다"
    )
    @PostMapping("/analyze")
    public ResponseEntity<CodeReviewResponse> analyzeCode(
            @Valid @RequestBody CodeReviewRequest request
    ) {
        log.info("=== 코드 리뷰 요청 ===");
        log.info("언어: {}, 레벨: {}", request.language(), request.reviewLevel());

        CodeReviewResponse response = codeReviewService.reviewCode(CodeReview.from(request));

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "리뷰 히스토리 조회",
            description = "이전에 수행한 코드 리뷰 히스토리를 조회합니다"
    )
    @GetMapping("/history")
    public ResponseEntity<List<CodeReviewResponse>> getHistory(
            @Parameter(description = "조회할 개수 (기본값: 전체)")
            @RequestParam(required = false) Integer limit
    ) {
        List<CodeReviewResponse> history = codeReviewService.getReviewHistory(limit);
        return ResponseEntity.ok(history);
    }

    @Operation(
            summary = "특정 리뷰 조회",
            description = "리뷰 ID로 특정 리뷰를 조회합니다"
    )
    @GetMapping("/history/{reviewId}")
    public ResponseEntity<CodeReviewResponse> getReviewById(
            @Parameter(description = "리뷰 ID")
            @PathVariable String reviewId
    ) {
        CodeReviewResponse review = codeReviewService.getReviewById(reviewId);
        return ResponseEntity.ok(review);
    }

    @Operation(
            summary = "언어별 리뷰 조회",
            description = "특정 언어의 리뷰 히스토리를 조회합니다"
    )
    @GetMapping("/history/language/{language}")
    public ResponseEntity<List<CodeReviewResponse>> getReviewsByLanguage(
            @Parameter(description = "언어 (Java, Python, JavaScript)")
            @PathVariable String language
    ) {
        List<CodeReviewResponse> reviews = codeReviewService.getReviewsByLanguage(language);
        return ResponseEntity.ok(reviews);
    }

    @Operation(
            summary = "리뷰 비교",
            description = "두 리뷰를 비교하여 개선도를 측정합니다"
    )
    @GetMapping("/history/{reviewId}/compare/{previousReviewId}")
    public ResponseEntity<ReviewComparison> compareReviews(
            @Parameter(description = "현재 리뷰 ID")
            @PathVariable String reviewId,

            @Parameter(description = "이전 리뷰 ID")
            @PathVariable String previousReviewId
    ) {
        ReviewComparison comparison = codeReviewService.compareReviews(reviewId, previousReviewId);
        return ResponseEntity.ok(comparison);
    }

    @Operation(
            summary = "간편 리뷰 (Basic)",
            description = "기본 레벨로 빠른 리뷰를 수행합니다"
    )
    @PostMapping("/quick")
    public ResponseEntity<CodeReviewResponse> quickReview(
            @Parameter(description = "언어")
            @RequestParam String language,

            @Parameter(description = "코드")
            @RequestBody String code
    ) {
        CodeReviewRequest request = new CodeReviewRequest(code, language, "basic");
        CodeReviewResponse response = codeReviewService.reviewCode(CodeReview.from(request));
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "리뷰 통계",
            description = "저장된 리뷰의 통계 정보를 조회합니다"
    )
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        int totalReviews = codeReviewService.getReviewCount();
        List<CodeReviewResponse> allReviews = codeReviewService.getReviewHistory(null);

        // 언어별 통계
        Map<String, Long> languageStats = allReviews.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        CodeReviewResponse::language,
                        java.util.stream.Collectors.counting()
                ));

        // 평균 점수
        double avgScore = allReviews.stream()
                .mapToInt(CodeReviewResponse::score)
                .average()
                .orElse(0.0);

        Map<String, Object> stats = Map.of(
                "totalReviews", totalReviews,
                "languageDistribution", languageStats,
                "averageScore", Math.round(avgScore * 100.0) / 100.0
        );

        return ResponseEntity.ok(stats);
    }

    @Operation(
            summary = "특정 리뷰 삭제",
            description = "리뷰 ID로 특정 리뷰를 삭제합니다"
    )
    @DeleteMapping("/history/{reviewId}")
    public ResponseEntity<Map<String, String>> deleteReview(
            @Parameter(description = "리뷰 ID")
            @PathVariable String reviewId
    ) {
        boolean deleted = codeReviewService.deleteReview(reviewId);

        if (deleted) {
            return ResponseEntity.ok(Map.of(
                    "message", "리뷰가 삭제되었습니다",
                    "reviewId", reviewId
            ));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "전체 리뷰 삭제",
            description = "모든 리뷰 히스토리를 삭제합니다"
    )
    @DeleteMapping("/history")
    public ResponseEntity<Map<String, String>> clearAllReviews() {
        codeReviewService.clearAllReviews();

        return ResponseEntity.ok(Map.of(
                "message", "모든 리뷰 히스토리가 삭제되었습니다"
        ));
    }
}