package com.example.study.controller.dto;

public record TokenUsage (
        Integer promptTokens, // 입력(프롬프트)에 사용된 토큰 수
        Integer completionTokens, // 출력(응답)에 사용된 토큰 수
        Integer totalTokens // 전체 토큰 수 (비용 계산에 사용)
) {
}
