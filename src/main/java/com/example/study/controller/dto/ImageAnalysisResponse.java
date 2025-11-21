package com.example.study.controller.dto;

import lombok.Builder;

@Builder
public record ImageAnalysisResponse<T>(
        T analysis,
        String imageType,
        long imageSize,
        TokenUsage tokenUsage
) {
    public static <T> ImageAnalysisResponse<T> of(T analysis,
                                                  String imageType,
                                                  long imageSize,
                                                  TokenUsage tokenUsage) {
        return ImageAnalysisResponse.<T>builder()
                .analysis(analysis)
                .imageType(imageType)
                .imageSize(imageSize)
                .tokenUsage(tokenUsage)
                .build();
    }
}