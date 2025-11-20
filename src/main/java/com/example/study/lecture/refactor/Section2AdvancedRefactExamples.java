package com.example.study.lecture.refactor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * SECTION 2 Advanced: 메서드 추출 + 클래스 분리를 복합적으로 연습할 수 있는 심화 예제 모음.
 * 모든 BEFORE 클래스는 의도적으로 길고 복잡하며, AFTER 클래스는 학습자가 직접 채우도록 비워 두었다.
 */
public final class Section2AdvancedRefactExamples {

    private Section2AdvancedRefactExamples() {
        throw new IllegalStateException("Utility class");
    }

    // ============================================================
    // ADVANCED EXAMPLE 1: 주문 승인 프로세스
    // ============================================================

    /**
     * SECTION 2 ADVANCED - BEFORE:
     * - 주문 승인/재고 확인/이벤트 발행 등 다양한 책임이 단일 메서드에 몰려 있다.
     * - Long Method + Feature Envy + Shotgun Surgery 악취를 모두 포함한다.
     */
    public static class OrderApprovalRefactBefore {

        public AdvancedOrder approve(AdvancedOrder order, AdvancedInventory inventory, AdvancedEventPublisher publisher) {
            if (order.getStatus() != AdvancedOrderStatus.PENDING) {
                throw new IllegalStateException("이미 처리된 주문입니다.");
            }

            // 1. 재고 확인 및 차감
            for (AdvancedOrderLine line : order.getLines()) {
                int stock = inventory.getStock(line.productCode());
                if (stock < line.quantity()) {
                    throw new IllegalStateException("재고 부족: " + line.productCode());
                }
            }
            for (AdvancedOrderLine line : order.getLines()) {
                inventory.decrease(line.productCode(), line.quantity());
            }

            // 2. 결제 요청 (단순 로그로 대체)
            BigDecimal totalAmount = BigDecimal.ZERO;
            for (AdvancedOrderLine line : order.getLines()) {
                totalAmount = totalAmount.add(line.price().multiply(BigDecimal.valueOf(line.quantity())));
            }
            System.out.printf("[Payment] order=%s amount=%s%n", order.getId(), totalAmount);

            // 3. 상태 변경 및 이벤트 발행
            order.setStatus(AdvancedOrderStatus.APPROVED);
            order.setApprovedAt(LocalDateTime.now());
            publisher.publish("ORDER_APPROVED", order.getId());
            return order;
        }
    }

    /**
     * SECTION 2 ADVANCED - AFTER Placeholder:
     * TODO: 재고 검증, 결제 계산, 이벤트 발행 등을 별도 메서드/협력 객체로 분리해보자.
     */
    public static class OrderApprovalRefactAfter {
        // 학습자가 직접 작성.
    }

    // ============================================================
    // ADVANCED EXAMPLE 2: 사용자 프로필 업데이트
    // ============================================================

    /**
     * SECTION 2 ADVANCED - BEFORE:
     * - 입력 검증, 외부 API 호출, 감사 로그 등 이질적인 로직이 한 메서드에 혼재.
     * - 메서드 추출 + 예외 메시지 정리 + DTO 분리를 실습한다.
     */
    public static class UserProfileUpdateRefactBefore {

        public void updateProfile(AdvancedUser user, AdvancedUserProfileRequest request, AuditLogger logger, ExternalMarketingApi marketingApi) {
            if (request.name() == null || request.name().isBlank()) {
                throw new IllegalArgumentException("이름은 필수입니다.");
            }
            if (request.email() == null || !request.email().contains("@")) {
                throw new IllegalArgumentException("이메일 형식이 올바르지 않습니다.");
            }
            if (request.phone() != null && request.phone().length() < 8) {
                throw new IllegalArgumentException("전화번호가 너무 짧습니다.");
            }

            user.setName(request.name());
            user.setEmail(request.email());
            user.setPhone(request.phone());
            user.setAddress(request.address());
            user.setUpdatedAt(LocalDateTime.now());

            logger.log(String.format("USER_PROFILE_UPDATED userId=%s name=%s", user.getId(), request.name()));

            if (request.marketingAgreement() != null && request.marketingAgreement()) {
                marketingApi.syncAgreement(user.getId(), true);
            }
        }
    }

    /**
     * SECTION 2 ADVANCED - AFTER Placeholder:
     * TODO: 입력 검증 로직을 별도 메서드로 추출하고, 외부 API 호출 전후를 명확히 분리해보자.
     */
    public static class UserProfileUpdateRefactAfter {
        // 학습자가 직접 작성.
    }

    // ============================================================
    // ADVANCED SUPPORTING TYPES
    // ============================================================

    public static class AdvancedOrder {
        private final String id;
        private final List<AdvancedOrderLine> lines = new ArrayList<>();
        private AdvancedOrderStatus status = AdvancedOrderStatus.PENDING;
        private LocalDateTime approvedAt;

        public AdvancedOrder(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public List<AdvancedOrderLine> getLines() {
            return lines;
        }

        public AdvancedOrderStatus getStatus() {
            return status;
        }

        public void setStatus(AdvancedOrderStatus status) {
            this.status = status;
        }

        public void setApprovedAt(LocalDateTime approvedAt) {
            this.approvedAt = approvedAt;
        }

        public void addLine(AdvancedOrderLine line) {
            lines.add(line);
        }
    }

    public enum AdvancedOrderStatus {
        PENDING, APPROVED, CANCELED
    }

    public record AdvancedOrderLine(String productCode, BigDecimal price, int quantity) {
    }

    public static class AdvancedInventory {
        private final List<InventoryItem> stock = new ArrayList<>();

        public int getStock(String productCode) {
            return stock.stream()
                    .filter(item -> item.code.equals(productCode))
                    .findFirst()
                    .map(item -> item.quantity)
                    .orElse(0);
        }

        public void decrease(String productCode, int quantity) {
            stock.stream()
                    .filter(item -> item.code.equals(productCode))
                    .findFirst()
                    .ifPresent(item -> item.quantity -= quantity);
        }

        public void addItem(String code, int quantity) {
            stock.add(new InventoryItem(code, quantity));
        }

        private static class InventoryItem {
            private final String code;
            private int quantity;

            private InventoryItem(String code, int quantity) {
                this.code = code;
                this.quantity = quantity;
            }
        }
    }

    public interface AdvancedEventPublisher {
        void publish(String eventName, String payload);
    }

    public static class AdvancedUser {
        private final Long id;
        private String name;
        private String email;
        private String phone;
        private String address;
        private LocalDateTime updatedAt;

        public AdvancedUser(Long id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }

        public Long getId() {
            return id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public void setUpdatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
        }
    }

    public record AdvancedUserProfileRequest(
            String name,
            String email,
            String phone,
            String address,
            Boolean marketingAgreement
    ) {
    }

    public interface AuditLogger {
        void log(String message);
    }

    public interface ExternalMarketingApi {
        void syncAgreement(Long userId, boolean agree);
    }
}
