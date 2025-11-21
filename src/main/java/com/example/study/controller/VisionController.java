package com.example.study.controller;

import com.example.study.controller.dto.ImageAnalysisResponse;
import com.example.study.service.ReceiptAnalysisService;
import com.example.study.service.VisionService;
import com.example.study.service.dto.ImageAnalysis;
import com.example.study.service.dto.ReceiptData;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

import static com.example.study.service.VisionService.DEFAULT_CHAT_RESPONSE_CLASS;

@RestController
@RequestMapping("/api/vision")
@RequiredArgsConstructor
public class VisionController {

    private final VisionService visionService;
    private final ReceiptAnalysisService receiptAnalysisService;

    /**
     * 이미지 분석 (커스텀 프롬프트)
     * POST /api/vision/analyze
     */
    @PostMapping(value = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImageAnalysisResponse<ChatResponse>> analyzeImage(
            @RequestParam String prompt,
            @RequestParam MultipartFile image) throws IOException {


        return ResponseEntity.status(HttpStatus.OK)
                .body(visionService.analyzeImage(ImageAnalysis.of(prompt, image), DEFAULT_CHAT_RESPONSE_CLASS));
    }

    /**
     * OCR - 이미지에서 텍스트 추출
     * POST /api/vision/ocr
     */
    @PostMapping(value = "/ocr", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> extractText(
            @RequestParam("image") MultipartFile image) throws IOException {

        String text = visionService.extractText(image);
        return ResponseEntity.ok(Map.of("text", text));
    }

    /**
     * 이미지 상세 설명
     * POST /api/vision/describe
     */
    @PostMapping(value = "/describe", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> describeImage(
            @RequestParam("image") MultipartFile image) throws IOException {

        String description = visionService.describeImage(image);
        return ResponseEntity.ok(Map.of("description", description));
    }

    /**
     * 차트/그래프 분석
     * POST /api/vision/chart
     */
    @PostMapping(value = "/chart", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> analyzeChart(
            @RequestParam("image") MultipartFile image) throws IOException {

        String analysis = visionService.analyzeChart(image);
        return ResponseEntity.ok(Map.of("analysis", analysis));
    }

    /**
     * 두 이미지 비교
     * POST /api/vision/compare
     */
    @PostMapping(value = "/compare", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> compareImages(
            @RequestParam("image1") MultipartFile image1,
            @RequestParam("image2") MultipartFile image2) {

        String comparison = visionService.compareImages(image1, image2);
        return ResponseEntity.ok(Map.of("comparison", comparison));
    }

    @PostMapping(value = "/receipt", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImageAnalysisResponse<ReceiptData>> processReceipt(@RequestParam("image") MultipartFile image) throws IOException, NoSuchFieldException {
        return ResponseEntity.status(HttpStatus.OK)
                .body(receiptAnalysisService.processReceipt(image));
    }

}
