package com.example.study.lecture.refactor;

import static com.example.study.lecture.refactor.Section2AdvancedRefactExamples.AdvancedOrderStatus.APPROVED;
import static com.example.study.lecture.refactor.Section2AdvancedRefactExamples.AdvancedOrderStatus.PENDING;

import com.example.study.lecture.refactor.Section2AdvancedRefactExamples.AdvancedEventPublisher;
import com.example.study.lecture.refactor.Section2AdvancedRefactExamples.AdvancedInventory;
import com.example.study.lecture.refactor.Section2AdvancedRefactExamples.AdvancedOrder;
import com.example.study.lecture.refactor.Section2AdvancedRefactExamples.AdvancedOrderLine;
import com.example.study.lecture.refactor.Section2AdvancedRefactExamples.AdvancedOrderStatus;
import com.example.study.lecture.refactor.Section2AdvancedRefactExamples.AdvancedUser;
import com.example.study.lecture.refactor.Section2AdvancedRefactExamples.AdvancedUserProfileRequest;
import com.example.study.lecture.refactor.Section2AdvancedRefactExamples.AuditLogger;
import com.example.study.lecture.refactor.Section2AdvancedRefactExamples.ExternalMarketingApi;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Section 2 Advanced 실습 정답 모음.
 */
public final class Section2AdvancedRefactSolution {

    private Section2AdvancedRefactSolution() {
        throw new IllegalStateException("Utility class");
    }

    // ============================================================
    // ADVANCED EXAMPLE 1 - SOLUTION
    // ============================================================
    public static class OrderApprovalRefactAfter {
        private final AdvancedInventory inventory;
        private final AdvancedEventPublisher publisher;

        public OrderApprovalRefactAfter(AdvancedInventory inventory, AdvancedEventPublisher publisher) {
            this.inventory = inventory;
            this.publisher = publisher;
        }

        public AdvancedOrder approve(AdvancedOrder order) {
            ensurePending(order.getStatus());
            validateStock(order);
            decreaseStock(order);
            requestPayment(order);
            markApproved(order);
            publishApprovedEvent(order);
            return order;
        }

        private void ensurePending(AdvancedOrderStatus status) {
            if (status != PENDING) {
                throw new IllegalStateException("이미 처리된 주문입니다.");
            }
        }

        private void validateStock(AdvancedOrder order) {
            for (AdvancedOrderLine line : order.getLines()) {
                int stock = inventory.getStock(line.productCode());
                if (stock < line.quantity()) {
                    throw new IllegalStateException("재고 부족: " + line.productCode());
                }
            }
        }

        private void decreaseStock(AdvancedOrder order) {
            order.getLines().forEach(line -> inventory.decrease(line.productCode(), line.quantity()));
        }

        private void requestPayment(AdvancedOrder order) {
            BigDecimal totalAmount = order.getLines().stream()
                    .map(line -> line.price().multiply(BigDecimal.valueOf(line.quantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            System.out.printf("[Payment] order=%s amount=%s%n", order.getId(), totalAmount);
        }

        private void markApproved(AdvancedOrder order) {
            order.setStatus(APPROVED);
            order.setApprovedAt(LocalDateTime.now());
        }

        private void publishApprovedEvent(AdvancedOrder order) {
            publisher.publish("ORDER_APPROVED", order.getId());
        }
    }

    // ============================================================
    // ADVANCED EXAMPLE 2 - SOLUTION
    // ============================================================
    public static class UserProfileUpdateRefactAfter {
        private final AuditLogger auditLogger;
        private final ExternalMarketingApi marketingApi;

        public UserProfileUpdateRefactAfter(AuditLogger auditLogger, ExternalMarketingApi marketingApi) {
            this.auditLogger = auditLogger;
            this.marketingApi = marketingApi;
        }

        public void updateProfile(AdvancedUser user, AdvancedUserProfileRequest request) {
            validateRequest(request);
            applyProfile(user, request);
            logAudit(user, request);
            syncMarketing(user, request);
        }

        private void validateRequest(AdvancedUserProfileRequest request) {
            if (request.name() == null || request.name().isBlank()) {
                throw new IllegalArgumentException("이름은 필수입니다.");
            }
            if (request.email() == null || !request.email().contains("@")) {
                throw new IllegalArgumentException("이메일 형식이 올바르지 않습니다.");
            }
            if (request.phone() != null && request.phone().length() < 8) {
                throw new IllegalArgumentException("전화번호가 너무 짧습니다.");
            }
        }

        private void applyProfile(AdvancedUser user, AdvancedUserProfileRequest request) {
            user.setName(request.name());
            user.setEmail(request.email());
            user.setPhone(request.phone());
            user.setAddress(request.address());
            user.setUpdatedAt(LocalDateTime.now());
        }

        private void logAudit(AdvancedUser user, AdvancedUserProfileRequest request) {
            auditLogger.log(String.format("USER_PROFILE_UPDATED userId=%s name=%s", user.getId(), request.name()));
        }

        private void syncMarketing(AdvancedUser user, AdvancedUserProfileRequest request) {
            if (Boolean.TRUE.equals(request.marketingAgreement())) {
                marketingApi.syncAgreement(user.getId(), true);
            }
        }
    }
}
