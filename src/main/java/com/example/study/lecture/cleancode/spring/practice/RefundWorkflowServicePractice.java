package com.example.study.lecture.cleancode.spring.practice;

import com.example.study.PurchaseStatus;
import com.example.study.entity.Purchase;
import com.example.study.repository.PurchaseRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Spring 연습용 예제 3: 하나의 서비스가 너무 많은 책임을 지는 상황.
 * - 상태 검증, 환불 기록 저장, 포인트 회수, 알림 발송, 감사 로그까지 모두 포함
 * - 구성 요소가 하드코딩되어 테스트/교체가 어렵다.
 *
 * TODO(SPRING-LAB):
 *  1) 환불 검증/재처리/알림/로그 등을 각각 별도 컴포넌트로 분리하세요.
 *  2) 상태 전환을 명확히 표현하는 도메인 메서드를 설계하세요.
 *  3) 트랜잭션 범위를 최소화하고, 외부 I/O(알림)는 트랜잭션 밖으로 분리하세요.
 */
@Service
public class RefundWorkflowServicePractice {

    private final PurchaseRepository purchaseRepository;
    private final LegacyNotificationGateway notificationGateway;
    private final AuditGateway auditGateway;

    public RefundWorkflowServicePractice(
            PurchaseRepository purchaseRepository,
            LegacyNotificationGateway notificationGateway,
            AuditGateway auditGateway
    ) {
        this.purchaseRepository = purchaseRepository;
        this.notificationGateway = notificationGateway;
        this.auditGateway = auditGateway;
    }

    @Transactional
    public Map<String, Object> processRefund(Long purchaseId, String reason) {
        auditGateway.append("refund start: " + purchaseId);
        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new IllegalArgumentException("purchase not found"));

        if (purchase.getStatus() == PurchaseStatus.REFUNDED) {
            throw new IllegalStateException("already refunded");
        }
        if (reason == null || reason.isBlank()) {
            reason = "기타";
        }

        purchase.markRefunded();
        purchaseRepository.save(purchase);

        notificationGateway.send("REFUND", "user-" + purchase.getUser().getId(), "환불 완료: " + reason);
        auditGateway.append("refund success : " + purchaseId + " at " + LocalDateTime.now());

        return Map.of(
                "purchaseId", purchaseId,
                "status", purchase.getStatus().name(),
                "reason", reason
        );
    }

    public interface LegacyNotificationGateway {
        void send(String topic, String target, String message);
    }

    public interface AuditGateway {
        void append(String message);
    }
}
