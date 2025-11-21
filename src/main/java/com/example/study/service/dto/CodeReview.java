package com.example.study.service.dto;

import com.example.study.controller.dto.CodeReviewRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class CodeReview {
    private final String code;
    private final String reviewLevel;
    private final String language;

    public static CodeReview from(CodeReviewRequest req) {
        return CodeReview.builder()
                .code(req.code())
                .reviewLevel(req.reviewLevel())
                .language(req.language())
                .build();
    }
}
