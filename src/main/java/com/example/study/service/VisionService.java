package com.example.study.service;

import com.example.study.controller.dto.ImageAnalysisResponse;
import com.example.study.controller.dto.TokenUsage;
import com.example.study.service.dto.ImageAnalysis;
import com.example.study.service.dto.ReceiptData;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.content.Media;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VisionService {

    private static final String DEFAULT_CONTENT_TYPE = "image/jpeg";
    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    /**
     * 이미지 분석
     */
    // Controller(request) -> Service(request)
    // Service -> request 전처리 사용하시나요?
    // Request Message -> naming 변경되거나, 추가되거나, 삭제되는 경우,
    // Service 코드 자체 수정이 되는 현상이 발생하잖아.
    // Mapper -> DTO for Service Layer ->
    public ImageAnalysisResponse analyzeImage(ImageAnalysis imageAnalysis) {
        String prompt = imageAnalysis.prompt();

        // 1. 이미지 바이트 배열 가져오기
        byte[] imageBytes = imageAnalysis.imageBytes();

        // 2. MIME 타입 결정
        String contentType = imageAnalysis.contentType();
        if (contentType == null) {
            contentType = DEFAULT_CONTENT_TYPE;
        }

        // 3. Media 객체 생성
        Media media = new Media(
                MimeTypeUtils.parseMimeType(contentType),
                new ByteArrayResource(imageBytes)
        );

        // 4. UserMessage 생성 (정적 팩토리 메서드 사용)
        UserMessage userMessage = UserMessage.builder()
                .media(List.of(media))
                .text(prompt)
                .build();

        // 5. Claude Vision API 호출
        // 2개의 ChatResponse DTO 객체가 존재합니다.
        // 1번째 ChatResponse DTO 객체는 Spring.AI 라이브러리, 패지키에 선언된 클래스를 말하고요
        // 2번째 우리가 직접 만든, 개발자 생성한 DTO
        org.springframework.ai.chat.model.ChatResponse response = chatClient.prompt()
                .messages(userMessage)
                .call()
                .chatResponse();

        //해당 방식이 정석이다
//        chatClient.prompt()
//                .messages(userMessage)
//                .call()
//                .entity(ImageAnalysisResponse.class);

        // 6. 응답 추출
        String analysis = response.getResult().getOutput().getText();

        // 7. 토큰 사용량 추출
        TokenUsage tokenUsage = null;
        // Kotlin
        // JDK 1.8 -> 국룰
        // JDK 17 -> JDK 9,10
        // -> var, Type Reference -> Java 10부터 제공하는 기능이라서, 사용하면 좋다.
        var metadata = response.getMetadata();
        if (metadata != null && metadata.getUsage() != null) {
            var usage = metadata.getUsage();
            tokenUsage = new TokenUsage(
                    usage.getPromptTokens(),
                    usage.getCompletionTokens(),
                    usage.getTotalTokens()
            );
        }

        return ImageAnalysisResponse.of(
                analysis,
                contentType,
                imageBytes.length,
                tokenUsage
        );

    }

    /**
     * 이미지에서 텍스트 추출 (OCR)
     */
    public String extractText(MultipartFile imageFile) throws IOException {
        String prompt = """
                이미지에 있는 모든 텍스트를 정확하게 추출해주세요.
                텍스트만 출력하고, 다른 설명은 필요 없습니다.
                """;

        ImageAnalysisResponse response = analyzeImage(ImageAnalysis.of(prompt, imageFile));
        return response.analysis();
    }

    /**
     * 이미지 상세 설명 생성
     */
    public String describeImage(MultipartFile imageFile) throws IOException {
        String prompt = """
                이 이미지를 다음 형식으로 상세히 설명해주세요:
                
                1. 전체적인 장면
                2. 주요 객체들
                3. 색상과 분위기
                4. 특이사항
                """;

        ImageAnalysisResponse response = analyzeImage(ImageAnalysis.of(prompt, imageFile));
        return response.analysis();
    }

    /**
     * 차트/그래프 분석
     */
    public String analyzeChart(MultipartFile imageFile) throws IOException {
        String prompt = """
                이 차트/그래프를 분석해주세요:
                
                1. 차트 유형 (막대, 선, 파이 등)
                2. 주요 데이터 포인트
                3. 트렌드와 패턴
                4. 인사이트와 결론
                """;

        ImageAnalysisResponse response = analyzeImage(ImageAnalysis.of(prompt, imageFile));
        return response.analysis();
    }

    /**
     * 이미지 비교 분석
     */
    public String compareImages(MultipartFile image1, MultipartFile image2) {
        try {
            // 첫 번째 이미지 처리
            byte[] bytes1 = image1.getBytes();
            Media media1 = new Media(
                    MimeTypeUtils.parseMimeType(
                            image1.getContentType() != null ? image1.getContentType() : "image/jpeg"
                    ),
                    new ByteArrayResource(bytes1)
            );

            // 두 번째 이미지 처리
            byte[] bytes2 = image2.getBytes();
            Media media2 = new Media(
                    MimeTypeUtils.parseMimeType(
                            image2.getContentType() != null ? image2.getContentType() : "image/jpeg"
                    ),
                    new ByteArrayResource(bytes2)
            );

            // 프롬프트와 함께 두 이미지 전달
            String prompt = """
                    두 이미지를 비교하여 다음을 설명해주세요:
                    
                    1. 공통점
                    2. 차이점
                    3. 각 이미지의 특징
                    """;

            // UserMessage 생성 (List로 여러 미디어 전달)
            UserMessage userMessage = UserMessage.builder()
                    .media(List.of(media1, media2))
                    .text(prompt)
                    .build();

            ChatResponse response = chatClient.prompt()
                    .messages(userMessage)
                    .call()
                    .chatResponse();

            return response.getResult().getOutput().getText();

        } catch (IOException e) {
            log.error("이미지 비교 실패: {}", e.getMessage());
            throw new RuntimeException("이미지 비교 중 오류 발생", e);
        }
    }

    /**
     * 영수증 스캐너
     * */
    public ReceiptData processReceipt(MultipartFile imageFile) throws IOException, NoSuchFieldException {

        String prompt = """
                이 영수증을 JSON 형식으로 파싱해주세요.
                
                응답 형식:
                {
                  "storeName": "가게명",
                  "address": "주소",
                  "date": "YYYY-MM-DD",
                  "time": "HH:MM",
                  "items": [
                    {"name": "상품명", "quantity": 1, "price": 10000}
                  ],
                  "subtotal": 10000,
                  "tax": 1000,
                  "total": 11000,
                  "paymentMethod": "카드/현금"
                }
                
                JSON만 출력하세요.
                """;

        ImageAnalysisResponse response = analyzeImage(ImageAnalysis.of(prompt, imageFile));
        log.info("response ::: {}", response);
        String jsonResponse = cleanJsonResponse(response.analysis());

        return objectMapper.readValue(jsonResponse, ReceiptData.class);
    }

    /**
     * JSON 응답 정제 (강화 버전)
     * - 마크다운 코드 블록 제거 (```json ... ```)
     * - 백틱(`) 제거
     * - 앞뒤 공백 제거
     * - 불필요한 텍스트 제거
     */
    private String cleanJsonResponse(String response) throws NoSuchFieldException {
        if (response == null || response.isEmpty()) {
            throw new NoSuchFieldException("Vision API 응답이 비어있습니다");
        }

        log.debug("정제 전 응답: {}", response);

        // 1. 마크다운 코드 블록 제거
        response = response.replaceAll("```json\\s*", "");
        response = response.replaceAll("```\\s*", "");

        // 2. 모든 백틱 제거
        response = response.replace("`", "");

        // 3. 앞뒤 공백 제거
        response = response.trim();

        // 4. JSON 시작 위치 찾기
        int jsonStart = response.indexOf("{");
        int jsonEnd = response.lastIndexOf("}");

        if (jsonStart == -1 || jsonEnd == -1 || jsonStart >= jsonEnd) {
            log.error("유효한 JSON을 찾을 수 없습니다. 응답: {}", response);
            throw new NoSuchFieldException("Vision API 응답에서 JSON을 찾을 수 없습니다");
        }

        // 5. JSON 부분만 추출
        response = response.substring(jsonStart, jsonEnd + 1);

        log.debug("정제 후 응답: {}", response);

        return response;
    }
}