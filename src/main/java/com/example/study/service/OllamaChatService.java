package com.example.study.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class OllamaChatService {

    private final ChatClient chatClient;

    public String chat(String message) {

        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }

    public String chatWithRole(String message, String systemPrompt) {
        return chatClient.prompt()
                .system(systemPrompt)
                .user(message)
                .call()
                .content();
    }

    public String chatWithOptions(String message, double temperature) {
        return chatClient.prompt()
                .user(message)
                .options(ChatOptions.builder()
                        .temperature(temperature)
                        .build())
                .call()
                .content();
    }

    public String translateText(String text, String targetLanguage) {
        return chatClient.prompt()
                .system(targetLanguage)
                .user(text)
                .call()
                .content();
    }

    public String generateCode(String description, String language) {
        return chatClient.prompt()
                .system(language)
                .user(description)
                .call()
                .content();
    }
}
