package com.example.study.lecture.cleancode.spring.practice3;

import com.example.study.PurchaseStatus;
import com.example.study.entity.Purchase;
import com.example.study.repository.PurchaseRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * 심화 연습 3: 스케줄러가 모든 책임을 짊어지고 있는 예제.
 *
 * 문제점
 * - 매 실행마다 전체 주문을 조회하고, 필터링/결제/상태 변경/알림을 모두 수행.
 * - 외부 결제 호출/알림 로직이 분리되어 있지 않아 테스트가 불가능.
 * - 실패/재시도 정책, 배치 단위, 동시성 제어가 없다.
 *
 * TODO(SPRING-ADV-PRACTICE3):
 *  1) Pending 주문 조회 → 결제 처리 → 결과 기록 단계를 각각 컴포넌트/서비스로 분리하세요.
 *  2) 배치 단위 처리, 재시도 정책, 외부 호출 추상화(PaymentGateway) 등을 도입하세요.
 *  3) 스케줄러는 orchestration만 담당하고, 트랜잭션 범위를 서비스에 위임하세요.
 */
@Component
public class LegacySettlementSchedulerPractice3 {

    private final PurchaseRepository purchaseRepository;

    public LegacySettlementSchedulerPractice3(PurchaseRepository purchaseRepository) {
        this.purchaseRepository = purchaseRepository;
    }

    @Scheduled(fixedDelay = 60_000)
    @Transactional
    public void settlePendingOrders() {
        List<Purchase> purchases = purchaseRepository.findAll();
        RestTemplate restTemplate = new RestTemplate();
        for (Purchase purchase : purchases) {
            if (purchase.getStatus() != PurchaseStatus.PENDING) {
                continue;
            }
            try {
                restTemplate.postForEntity(
                        "https://payment.example.com/api/auto-settle/" + purchase.getId(),
                        null,
                        String.class
                );
                purchase.markCompleted();
                purchaseRepository.save(purchase);
                System.out.println("[scheduler] settled purchase=" + purchase.getId());
            } catch (Exception exception) {
                System.err.println("[scheduler] fail purchase=" + purchase.getId() + " reason=" + exception.getMessage());
            }
        }
    }
}
