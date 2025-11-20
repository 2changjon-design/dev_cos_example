package com.example.study.config;

import com.example.study.service.ChatService;
import jakarta.annotation.PostConstruct;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.anthropic.AnthropicChatOptions;
import org.springframework.ai.anthropic.api.AnthropicApi;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@ConfigurationProperties(prefix = "spring.ai.anthropic")
@Slf4j
@Setter
public class ChatConfig {
    private String apiKey;

    @PostConstruct
    public void init() {
        log.debug("ClaudeChatService init");
        log.debug("Claude apiKey: {}", apiKey);
    }

    @Bean
    public AnthropicApi anthropicApi() {
        AnthropicApi.Builder builder = new AnthropicApi.Builder();
        builder.apiKey(apiKey);
        return builder.build();
    }

    @Bean(name = "claudeChatModel")
    @Primary
    public AnthropicChatModel anthgropicChatModel(AnthropicApi anthropicApi) {
        String modelName = "claude-sonnet-4-20250514";
        return AnthropicChatModel.builder()
                .anthropicApi(anthropicApi)
                .defaultOptions(
                    AnthropicChatOptions.builder()
                            .model(modelName)
                            .temperature(0.5) //창의력
                            .maxTokens(2048) //토큰수 제한
                            .build()
                )
                .build();
    }

    @Bean
    public ChatClient anthropicChatClient(
            ChatClient.Builder chatClientBuilder) {

        return chatClientBuilder
                .defaultSystem("""
                당신은 친절하고 도움이 되는 AI 어시스턴트입니다.
                사용자의 질문에 정확하고 이해하기 쉽게 답변해주세요.
                """) //defaultSystem(): 모든 대화에 적용될 기본 시스템 메시지 설정
                .build();
    }

}
