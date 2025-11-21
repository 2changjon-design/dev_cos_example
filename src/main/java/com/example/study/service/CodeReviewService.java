package com.example.study.service;

import com.example.study.controller.dto.CodeReviewResponse;
import com.example.study.controller.dto.ReviewComparison;
import com.example.study.service.dto.CodeReview;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CodeReviewService {

    private final ChatClient chatClient;
    private final Map<String, CodeReviewResponse> reviewHistory = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;

    public CodeReviewResponse reviewCode(CodeReview review) {
        long startTime = System.currentTimeMillis();

        String systemMessage = createSystemPrompt(review.getReviewLevel(), review.getLanguage());
        // User Prompt 생성
        String userPrompt = createUserPrompt(review.getCode(), review.getLanguage());

        // AI 호출
        CodeReviewResponse aiResponse = chatClient.prompt()
                .system(systemMessage)
                .user(userPrompt)
                .call()
                .entity(CodeReviewResponse.class);

        long responseTime = System.currentTimeMillis() - startTime;
        log.debug("AI 응답: {} \n 소요 시간: {}", aiResponse, responseTime);

        // ✅ In-Memory에 저장
        assert aiResponse != null;
        if (aiResponse.reviewId() != null) reviewHistory.put(aiResponse.reviewId(), aiResponse);
        log.info("리뷰 완료 및 저장: {} ({}ms)", aiResponse.reviewId(), responseTime);

        return aiResponse;
    }

    /**
     * 리뷰 히스토리 조회 (최신순)
     */
    public List<CodeReviewResponse> getReviewHistory(Integer limit) {
        List<CodeReviewResponse> allReviews = reviewHistory.values().stream()
                .sorted(Comparator.comparing(CodeReviewResponse::timestamp).reversed())
                .collect(Collectors.toList());

        if (limit != null && limit > 0) {
            return allReviews.stream()
                    .limit(limit)
                    .collect(Collectors.toList());
        }

        return allReviews;
    }

    /**
     * 특정 리뷰 조회
     */
    public CodeReviewResponse getReviewById(String reviewId) {
        CodeReviewResponse review = reviewHistory.get(reviewId);
        if (review == null) {
            throw new IllegalArgumentException("리뷰를 찾을 수 없습니다: " + reviewId);
        }
        return review;
    }

    /**
     * 언어별 리뷰 조회
     */
    public List<CodeReviewResponse> getReviewsByLanguage(String language) {
        return reviewHistory.values().stream()
                .filter(review -> review.language().equalsIgnoreCase(language))
                .sorted(Comparator.comparing(CodeReviewResponse::timestamp).reversed())
                .collect(Collectors.toList());
    }

    /**
     * 리뷰 비교
     */
    public ReviewComparison compareReviews(String currentReviewId, String previousReviewId) {
        CodeReviewResponse current = getReviewById(currentReviewId);
        CodeReviewResponse previous = getReviewById(previousReviewId);

        return ReviewComparison.compare(current, previous);
    }

    /**
     * 특정 리뷰 삭제
     */
    public boolean deleteReview(String reviewId) {
        CodeReviewResponse removed = reviewHistory.remove(reviewId);
        if (removed != null) {
            log.info("리뷰 삭제됨: {}", reviewId);
            return true;
        }
        return false;
    }

    /**
     * 전체 리뷰 삭제
     */
    public void clearAllReviews() {
        int count = reviewHistory.size();
        reviewHistory.clear();
        log.info("모든 리뷰 히스토리 삭제됨: {}개", count);
    }

    /**
     * 저장된 리뷰 개수
     */
    public int getReviewCount() {
        return reviewHistory.size();
    }

    private String createSystemPrompt(String reviewLevel, String language) {
        String basePrompt = String.format("""
                당신은 %s 코드를 전문적으로 리뷰하는 시니어 개발자입니다.
                코드를 분석하고 JSON 형식으로 응답해야 합니다.
                """, language);

        String levelPrompt = switch (reviewLevel) {
            case "basic" -> """
                    리뷰 레벨: BASIC
                    - 문법 오류 확인
                    - 기본 코딩 컨벤션 체크
                    - 명백한 버그 찾기
                    - 초보자도 이해할 수 있게 설명
                    """;
            case "detailed" -> """
                    리뷰 레벨: DETAILED
                    - 성능 최적화 제안
                    - 가독성 개선 방안
                    - 베스트 프랙티스 적용
                    - 잠재적 버그 분석
                    - 리팩토링 제안
                    """;
            case "expert" -> """
                    리뷰 레벨: EXPERT
                    - 아키텍처 개선 제안
                    - 디자인 패턴 적용
                    - SOLID 원칙 준수 여부
                    - 확장성과 유지보수성 분석
                    - 고급 최적화 기법
                    """;
            default -> "";
        };

        String formatPrompt = """
                응답 형식 (반드시 준수):
                {
                  "issues": [
                    {
                      "severity": "HIGH|MEDIUM|LOW",
                      "line": 줄번호,
                      "message": "이슈 설명",
                      "suggestion": "개선 방법"
                    }
                  ],
                  "improvements": ["개선사항1", "개선사항2", ...],
                  "score": 0-100점수,
                  "improvedCode": "개선된 전체 코드",
                  "summary": "전체 리뷰 요약"
                }
                
                중요:
                - 반드시 유효한 JSON 형식으로만 응답하세요
                - JSON 외의 다른 텍스트는 포함하지 마세요
                - 코드 블록 마크다운(```)도 사용하지 마세요
                """;

        return basePrompt + levelPrompt + formatPrompt;
    }

    /**
     * User Prompt 생성
     */
    private String createUserPrompt(String code, String language) {
        return String.format("""
                다음 %s 코드를 리뷰해주세요:
                ```
                %s
                %s
                ```
                
                위 지시사항에 따라 JSON 형식으로만 응답해주세요.
                """, language, language.toLowerCase(), code);
    }
}