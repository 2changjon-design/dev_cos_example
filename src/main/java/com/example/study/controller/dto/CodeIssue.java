package com.example.study.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "코드 이슈")
public record CodeIssue(

        @Schema(description = "심각도", example = "MEDIUM")
        String severity,

        @Schema(description = "라인 번호", example = "5")
        Integer line,

        @Schema(description = "이슈 메시지", example = "변수명이 명확하지 않습니다")
        String message,

        @Schema(description = "개선 제안", example = "totalSum으로 변경하세요")
        String suggestion
) {
    public static CodeIssue of(String severity, Integer line, String message, String suggestion) {
        return new CodeIssue(severity, line, message, suggestion);
    }
}