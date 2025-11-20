package com.example.study.lecture.refactor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Section 1 Advanced 정답 코드.
 */
public final class Section1AdvancedRefactSolution {

    private Section1AdvancedRefactSolution() {
        throw new IllegalStateException("Utility class");
    }

    // ============================================================
    // Advanced 1) 메서드 추출 - 청구서 생성
    // ============================================================
    public static class InvoiceGeneratorAfter {

        public Section1AdvancedRefactExamples.Invoice generate(Section1AdvancedRefactExamples.Invoice invoice) {
            BigDecimal subtotal = calculateSubtotal(invoice);
            BigDecimal discount = calculateDiscount(subtotal, invoice.clientTier());
            BigDecimal discountedSubtotal = subtotal.subtract(discount);
            BigDecimal tax = calculateTax(discountedSubtotal, invoice.taxRate());
            BigDecimal serviceFee = calculateServiceFee(invoice);

            invoice.setSummary(summaryText(subtotal, discount, tax, serviceFee));
            invoice.setTotal(discountedSubtotal.add(tax).add(serviceFee));
            invoice.setGeneratedAt(LocalDateTime.now());
            return invoice;
        }

        private BigDecimal calculateSubtotal(Section1AdvancedRefactExamples.Invoice invoice) {
            return invoice.lines().stream()
                    .map(line -> line.unitPrice().multiply(BigDecimal.valueOf(line.quantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        private BigDecimal calculateDiscount(BigDecimal subtotal, String clientTier) {
            return switch (clientTier) {
                case "PLATINUM" -> subtotal.multiply(BigDecimal.valueOf(0.12));
                case "GOLD" -> subtotal.multiply(BigDecimal.valueOf(0.07));
                default -> BigDecimal.ZERO;
            };
        }

        private BigDecimal calculateTax(BigDecimal discountedSubtotal, double taxRate) {
            return discountedSubtotal.multiply(BigDecimal.valueOf(taxRate));
        }

        private BigDecimal calculateServiceFee(Section1AdvancedRefactExamples.Invoice invoice) {
            BigDecimal fee = BigDecimal.valueOf(15_000);
            if (invoice.lines().size() > 5) {
                fee = fee.add(BigDecimal.valueOf(10_000));
            }
            return fee;
        }

        private String summaryText(BigDecimal subtotal, BigDecimal discount, BigDecimal tax, BigDecimal fee) {
            return String.format("subtotal=%s, discount=%s, tax=%s, fee=%s", subtotal, discount, tax, fee);
        }
    }

    // ============================================================
    // Advanced 2) 단일 책임 - 고객 온보딩
    // ============================================================
    public static class CustomerOnboardingAfter {
        private final ProfileValidator validator;
        private final Section1AdvancedRefactExamples.CustomerRepository repository;
        private final Section1AdvancedRefactExamples.EmailSender emailSender;
        private final Section1AdvancedRefactExamples.AuditLogger auditLogger;

        public CustomerOnboardingAfter(ProfileValidator validator,
                                       Section1AdvancedRefactExamples.CustomerRepository repository,
                                       Section1AdvancedRefactExamples.EmailSender emailSender,
                                       Section1AdvancedRefactExamples.AuditLogger auditLogger) {
            this.validator = validator;
            this.repository = repository;
            this.emailSender = emailSender;
            this.auditLogger = auditLogger;
        }

        public void onboard(Section1AdvancedRefactExamples.CustomerProfile profile) {
            validator.validate(profile);
            repository.save(profile);
            emailSender.send(profile.email(), "[환영] 가입해주셔서 감사합니다.");
            auditLogger.log("CUSTOMER_ONBOARD " + profile.email());
        }
    }

    public static class ProfileValidator {
        public void validate(Section1AdvancedRefactExamples.CustomerProfile profile) {
            if (profile.name() == null || profile.name().isBlank()) {
                throw new IllegalArgumentException("이름은 필수입니다.");
            }
            if (profile.email() == null || !profile.email().contains("@")) {
                throw new IllegalArgumentException("이메일 형식이 잘못되었습니다.");
            }
        }
    }

    // ============================================================
    // Advanced 3) 명확한 이름 짓기 - 지표 수집기
    // ============================================================
    public static class MetricsCollectorAfter {
        private int totalEvents;
        private int premiumEvents;
        private int regularEvents;

        public void recordEvent(int engagementScore, boolean isPremiumUser) {
            totalEvents += engagementScore;
            if (isPremiumUser) {
                premiumEvents++;
            } else {
                regularEvents++;
            }
        }

        public int getTotalEngagementScore() {
            return totalEvents;
        }

        public int getPremiumEventCount() {
            return premiumEvents;
        }

        public int getRegularEventCount() {
            return regularEvents;
        }
    }
}
