package com.example.study.lecture.cleancode.spring.practice1;

import com.example.study.entity.Purchase;
import com.example.study.repository.PurchaseRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Spring 심화 연습용 예제: 컨트롤러 한 곳에서 외부 결제/알림/상태 갱신까지 처리.
 *
 * 문제점
 * - Controller가 직접 RestTemplate/HttpURLConnection을 생성해 외부 시스템을 호출.
 * - JSON 문자열을 직접 만들고 반환 타입이 고정되어 있다.
 * - 엔티티를 그대로 갱신하며 예외/로깅 처리가 제각각이다.
 *
 * TODO(SPRING-ADV-PRACTICE1):
 *  1) Controller-Service-Gateway-Notifier 레이어를 분리하고, 의존성 주입 가능하도록 인터페이스를 정의하세요.
 *  2) 결제 요청/응답 DTO + 매퍼를 만들고, 예외/검증/트랜잭션 경계를 명확히 하세요.
 *  3) 외부 호출(RestTemplate/Slack 웹훅)을 Bean으로 분리하여 재사용, 테스트 용이성을 확보하세요.
 */
@RestController
@RequestMapping("/spring/practice1/payments")
public class PaymentControllerPractice1 {

    private final PurchaseRepository purchaseRepository;

    public PaymentControllerPractice1(PurchaseRepository purchaseRepository) {
        this.purchaseRepository = purchaseRepository;
    }

    @PostMapping("/{purchaseId}/settle")
    @Transactional
    public String settle(@PathVariable Long purchaseId, @RequestBody Map<String, Object> requestBody) throws Exception {
        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new IllegalArgumentException("purchase not found"));

        String cardNo = (String) requestBody.get("cardNo");
        if (!StringUtils.hasText(cardNo)) {
            throw new IllegalArgumentException("cardNo is required");
        }
        Integer installment = (Integer) requestBody.getOrDefault("installment", 1);

        Map<String, Object> gatewayRequest = new HashMap<>();
        gatewayRequest.put("purchaseId", purchase.getId());
        gatewayRequest.put("amount", purchase.getTotalPrice());
        gatewayRequest.put("cardNo", mask(cardNo));
        gatewayRequest.put("installment", installment);
        gatewayRequest.put("requestedAt", LocalDateTime.now().toString());

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(
                "https://payment.example.com/api/pay",
                gatewayRequest,
                String.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new IllegalStateException("payment gateway error");
        }

        purchase.markCompleted();
        purchaseRepository.save(purchase);

        notifySlack(purchase.getId(), response.getBody());
        return """
                {"status":"OK","transaction":"%s"}
                """.formatted(response.getBody());
    }

    private String mask(String cardNo) {
        if (cardNo == null || cardNo.length() < 4) {
            return "****";
        }
        return "****-****-****-" + cardNo.substring(cardNo.length() - 4);
    }

    private void notifySlack(Long purchaseId, String transactionId) throws Exception {
        URL url = new URL("https://hooks.slack.com/services/example");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        String payload = """
                {"text":"purchase %s paid with transaction %s"}
                """.formatted(purchaseId, transactionId);
        try (OutputStream os = connection.getOutputStream()) {
            os.write(payload.getBytes(StandardCharsets.UTF_8));
        }
        connection.getResponseCode();
    }
}
