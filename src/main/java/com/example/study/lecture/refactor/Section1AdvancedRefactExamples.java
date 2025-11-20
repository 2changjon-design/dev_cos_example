package com.example.study.lecture.refactor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * SECTION 1 Advanced 실습 예제
 *
 * 심화 주제 역시
 *  - 메서드 추출
 *  - 단일 책임 원칙
 *  - 명확한 이름 짓기
 * 로 구성된다.
 */
public final class Section1AdvancedRefactExamples {

    private Section1AdvancedRefactExamples() {
        throw new IllegalStateException("Utility class");
    }

    // ============================================================
    // Advanced 1) 메서드 추출 - 복잡한 청구서 생성
    // ============================================================

    public static class InvoiceGeneratorBefore {

        public Invoice generate(Invoice invoice) {
            BigDecimal subtotal = BigDecimal.ZERO;
            for (InvoiceLine line : invoice.lines()) {
                BigDecimal lineTotal = line.unitPrice().multiply(BigDecimal.valueOf(line.quantity()));
                subtotal = subtotal.add(lineTotal);
            }

            BigDecimal discount = BigDecimal.ZERO;
            if (invoice.clientTier().equals("PLATINUM")) {
                discount = subtotal.multiply(BigDecimal.valueOf(0.12));
            } else if (invoice.clientTier().equals("GOLD")) {
                discount = subtotal.multiply(BigDecimal.valueOf(0.07));
            }
            BigDecimal discountedSubtotal = subtotal.subtract(discount);

            BigDecimal tax = discountedSubtotal.multiply(BigDecimal.valueOf(invoice.taxRate()));
            BigDecimal serviceFee = BigDecimal.valueOf(15_000);
            if (invoice.lines().size() > 5) {
                serviceFee = serviceFee.add(BigDecimal.valueOf(10_000));
            }

            invoice.setSummary(String.format("subtotal=%s, discount=%s, tax=%s, fee=%s",
                    subtotal, discount, tax, serviceFee));
            invoice.setTotal(discountedSubtotal.add(tax).add(serviceFee));
            invoice.setGeneratedAt(LocalDateTime.now());
            return invoice;
        }
    }

    public static class InvoiceGeneratorAfter {
        // TODO: 할인/세금/수수료 계산을 private 메서드로 분리해보세요.
    }

    public static class Invoice {
        private final Long id;
        private final String clientTier;
        private final double taxRate;
        private final List<InvoiceLine> lines = new ArrayList<>();
        private BigDecimal total = BigDecimal.ZERO;
        private String summary = "";
        private LocalDateTime generatedAt;

        public Invoice(Long id, String clientTier, double taxRate) {
            this.id = id;
            this.clientTier = clientTier;
            this.taxRate = taxRate;
        }

        public Long id() {
            return id;
        }

        public String clientTier() {
            return clientTier;
        }

        public double taxRate() {
            return taxRate;
        }

        public List<InvoiceLine> lines() {
            return lines;
        }

        public void addLine(InvoiceLine line) {
            lines.add(line);
        }

        public BigDecimal total() {
            return total;
        }

        public void setTotal(BigDecimal total) {
            this.total = total;
        }

        public String summary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public LocalDateTime generatedAt() {
            return generatedAt;
        }

        public void setGeneratedAt(LocalDateTime generatedAt) {
            this.generatedAt = generatedAt;
        }
    }

    public record InvoiceLine(String description, BigDecimal unitPrice, int quantity) {
    }

    // ============================================================
    // Advanced 2) 단일 책임 - 고객 온보딩
    // ============================================================

    public static class CustomerOnboardingBefore {
        private final CustomerRepository repository;
        private final EmailSender emailSender;
        private final AuditLogger auditLogger;

        public CustomerOnboardingBefore(CustomerRepository repository,
                                        EmailSender emailSender,
                                        AuditLogger auditLogger) {
            this.repository = repository;
            this.emailSender = emailSender;
            this.auditLogger = auditLogger;
        }

        public void onboard(CustomerProfile profile) {
            if (profile.name() == null || profile.name().isBlank()) {
                throw new IllegalArgumentException("이름은 필수입니다.");
            }
            if (profile.email() == null || !profile.email().contains("@")) {
                throw new IllegalArgumentException("이메일 형식이 잘못되었습니다.");
            }

            repository.save(profile);
            emailSender.send(profile.email(), "[환영] 가입해주셔서 감사합니다.");
            auditLogger.log("CUSTOMER_ONBOARD " + profile.email());
        }
    }

    public static class CustomerOnboardingAfter {
        // TODO: 검증/저장/알림/감사를 별도 컴포넌트로 분리해보세요.
    }

    public record CustomerProfile(String name, String email) {
    }

    public interface CustomerRepository {
        void save(CustomerProfile profile);
    }

    public interface EmailSender {
        void send(String email, String message);
    }

    public interface AuditLogger {
        void log(String message);
    }

    // ============================================================
    // Advanced 3) 명확한 이름 짓기 - 지표 수집기
    // ============================================================

    public static class MetricsCollectorBefore {
        private int m1;
        private int m2;
        private int m3;

        public void add(int a, boolean b) {
            m1 += a;
            if (b) {
                m2++;
            } else {
                m3++;
            }
        }

        public int getM1() {
            return m1;
        }

        public int getM2() {
            return m2;
        }

        public int getM3() {
            return m3;
        }
    }

    public static class MetricsCollectorAfter {
        // TODO: 의미 있는 이름(예: totalEvents, premiumEvents, regularEvents)으로 다시 작성해보세요.
    }
}
