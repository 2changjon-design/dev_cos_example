package com.example.study.lecture.cleancode.spring.practice2;

import com.example.study.PurchaseStatus;
import com.example.study.entity.Purchase;
import com.example.study.repository.PurchaseRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.StringJoiner;

/**
 * 심화 연습 2: 조회 전용 API가 Service/DTO 없이 컨트롤러에 모든 책임이 몰려 있는 예제.
 *
 * 문제점
 * - 컨트롤러에서 JPA 엔티티를 그대로 조회/필터/정렬/가공
 * - 문자열을 직접 조합하여 JSON 흉내를 냄
 * - 상태/기간 필터링 로직이 중복되고 테스트가 어려움
 *
 * TODO(SPRING-ADV-PRACTICE2):
 *  1) 읽기 전용 QueryService + DTO를 도입해 Controller는 orchestration만 하도록 만드세요.
 *  2) 기간/상태 필터링 로직을 재사용 가능한 컴포넌트로 추출하세요.
 *  3) 금액 합계/평균 계산을 전용 집계 객체에서 수행하고, JSON 문자열 대신 DTO/ResponseEntity를 사용하세요.
 */
@RestController
@RequestMapping("/spring/practice2/reports")
public class AnalyticsReportControllerPractice2 {

    private final PurchaseRepository purchaseRepository;

    public AnalyticsReportControllerPractice2(PurchaseRepository purchaseRepository) {
        this.purchaseRepository = purchaseRepository;
    }

    @GetMapping("/purchases")
    public String purchaseReport(
            @RequestParam(required = false) PurchaseStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(defaultValue = "5") Integer limit
    ) {
        List<Purchase> purchases = purchaseRepository.findAll();

        List<Purchase> filtered = purchases.stream()
                .filter(p -> status == null || p.getStatus() == status)
                .filter(p -> from == null || !p.getPurchasedAt().isBefore(from))
                .filter(p -> to == null || !p.getPurchasedAt().isAfter(to))
                .sorted(Comparator.comparing(Purchase::getPurchasedAt).reversed())
                .limit(limit)
                .toList();

        BigDecimal total = filtered.stream()
                .map(Purchase::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal average = filtered.isEmpty()
                ? BigDecimal.ZERO
                : total.divide(BigDecimal.valueOf(filtered.size()), BigDecimal.ROUND_HALF_UP);

        StringJoiner joiner = new StringJoiner(",", "{", "}");
        joiner.add("\"count\":" + filtered.size());
        joiner.add("\"total\":\"" + total + "\"");
        joiner.add("\"average\":\"" + average + "\"");

        StringJoiner purchasesJson = new StringJoiner(",", "\"items\":[", "]");
        for (Purchase purchase : filtered) {
            purchasesJson.add("""
                    {"id":%s,"user":%s,"product":%s,"status":"%s","total":"%s","purchasedAt":"%s"}
                    """.formatted(
                    purchase.getId(),
                    purchase.getUser().getId(),
                    purchase.getProduct().getId(),
                    purchase.getStatus().name(),
                    purchase.getTotalPrice(),
                    purchase.getPurchasedAt()
            ));
        }
        joiner.add(purchasesJson.toString());

        return joiner.toString();
    }
}
