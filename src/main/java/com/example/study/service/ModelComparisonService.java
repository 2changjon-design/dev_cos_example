package com.example.study.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ModelComparisonService {

    private final ChatClient ollamaChatClient;  // OLLAMA (로컬)*
    private final ChatClient claudeChatClient;  // Claude (클라우드)*

    public ModelComparisonService(
            ChatClient.Builder ollamaBuilder,
            ChatClient.Builder claudeBuilder) {
        this.ollamaChatClient = ollamaBuilder.build();
        this.claudeChatClient = claudeBuilder.build();
    }

    public void compareModels(String prompt) {
        // 1. OLLAMA 테스트
        long ollamaStart = System.currentTimeMillis();
        String ollamaResponse = ollamaChatClient.prompt()
                .user(prompt)
                .call()
                .content();
        long ollamaTime = System.currentTimeMillis() - ollamaStart;

        // 2. Claude 테스트
        long claudeStart = System.currentTimeMillis();
        String claudeResponse = claudeChatClient.prompt()
                .user(prompt)
                .call()
                .content();
        long claudeTime = System.currentTimeMillis() - claudeStart;

        // 3. 결과 출력
        log.info("=== 모델 비교 결과 ===");
        log.info("질문: {}", prompt);
        log.info("\n[OLLAMA (Qwen 2.5 3B)]");
        log.info("응답 시간: {}ms", ollamaTime);
        log.info("응답: {}", ollamaResponse);
        log.info("\n[Claude (Sonnet 4)]");
        log.info("응답 시간: {}ms", claudeTime);
        log.info("응답: {}", claudeResponse);
    }
}