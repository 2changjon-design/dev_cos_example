package com.example.study.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ChatResponseV2(
        String message, //AI의 응답 메시지
        String conversationId, //대화 세션 ID
        LocalDateTime timestamp, // 응답 생성 시각
        TokenUsage tokenUsage //토큰 사용량 정보
) {
    public static ChatResponseV2 of(String message, String conversationId,
                                    LocalDateTime timestamp, TokenUsage tokenUsage) {
        return ChatResponseV2.builder()
                .message(message)
                .conversationId(conversationId)
                .timestamp(timestamp)
                .tokenUsage(tokenUsage)
                .build();
    }

    public static ChatResponseV2 of(String message, String conversationId,
                                    TokenUsage tokenUsage) {
        return ChatResponseV2.of(message, conversationId, LocalDateTime.now(), tokenUsage);
    }

    public static ChatResponseV2 of(String message, String conversationId) {
        return ChatResponseV2.of(message, conversationId, LocalDateTime.now(), null);
    }

}