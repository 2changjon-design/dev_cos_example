package com.example.study.lecture.refactoradvanced;

import java.util.ArrayList;
import java.util.List;

/**
 * Domain Event 리팩토링 예제.
 *
 * BEFORE:
 *  - 서비스가 주문 저장 + 메일 전송 + 포인트 적립 등 부수 효과를 모두 직접 호출한다.
 * AFTER:
 *  - TODO: OrderPlacedEvent + EventPublisher를 도입해 관심사를 분리한다.
 */
public final class DomainEventExamples {

    private DomainEventExamples() {
        throw new IllegalStateException("Utility class");
    }

    public static class OrderServiceBefore {
        private final MailService mailService = new MailService();
        private final PointService pointService = new PointService();
        private final AnalyticsService analyticsService = new AnalyticsService();

        public void placeOrder(String userEmail) {
            saveOrder();
            mailService.sendMail(userEmail);
            pointService.addPoint(userEmail, 100);
            analyticsService.track(userEmail);
        }

        private void saveOrder() {
            // save logic
        }
    }

    /**
     * AFTER Placeholder:
     *  - TODO: OrderPlacedEvent를 발행하고, Mail/Point 핸들러는 이벤트를 구독하도록 분리한다.
     */
    public static class OrderEventPublisher {
        private final List<OrderListener> listeners = new ArrayList<>();

        public void register(OrderListener listener) {
            listeners.add(listener);
        }

        public void publish(String userEmail) {
            listeners.forEach(listener -> listener.onOrder(userEmail));
        }
    }

    public interface OrderListener {
        void onOrder(String userEmail);
    }

    static class MailService implements OrderListener {
        @Override
        public void onOrder(String userEmail) {
            sendMail(userEmail);
        }

        void sendMail(String email) { /* ... */ }
    }

    static class PointService implements OrderListener {
        @Override
        public void onOrder(String userEmail) {
            addPoint(userEmail, 100);
        }

        void addPoint(String email, int point) { /* ... */ }
    }

    static class AnalyticsService implements OrderListener {
        @Override
        public void onOrder(String userEmail) {
            track(userEmail);
        }

        void track(String email) { /* ... */ }
    }
}
