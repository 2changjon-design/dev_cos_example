package com.example.study.service;

import com.example.study.controller.dto.ChatResponseV2;
import reactor.core.publisher.Flux;
import org.springframework.ai.chat.messages.Message;

import javax.naming.LimitExceededException;
import java.util.List;

public interface IChatService {
    // 기본 채팅 (히스토리 없음)
    ChatResponseV2 chat(String question);

    // 대화 히스토리를 유지하는 채팅
    ChatResponseV2 chatWithHistory(String question, String conversationId) throws LimitExceededException;;

    // 스트리밍 채팅
    Flux<String> chatStream(String question);

    // 대화 히스토리 조회
    List<Message> getConversationHistory(String conversationId);

    // 대화 삭제
    void clearConversationBy(String conversationId);

    // 모든 대화 삭제
    void clearAllConversations();
}
