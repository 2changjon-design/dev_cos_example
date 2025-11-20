package com.example.study.controller.dto;

public record ChatRequestV2(
        String message, // 사용자가 보내는 메시지 (필수)
        String conversationId // 대화 세션 ID (선택, null이면 새 대화 생성)
) {
    public ChatRequestV2 {
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("message is null or blank");
        }
    }
}