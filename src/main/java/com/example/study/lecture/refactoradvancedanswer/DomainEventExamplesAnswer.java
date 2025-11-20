package com.example.study.lecture.refactoradvancedanswer;

import com.example.study.lecture.refactoradvanced.DomainEventExamples.OrderListener;
import com.example.study.lecture.refactoradvanced.DomainEventExamples.OrderServiceBefore;

import java.util.ArrayList;
import java.util.List;

/**
 * Domain Event After Answer.
 */
public final class DomainEventExamplesAnswer {

    private DomainEventExamplesAnswer() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 이벤트 퍼블리셔를 도입해 부수 효과(Mail, Point)를 느슨하게 연결한 버전.
     * - 서비스는 주문 저장 후 EventPublisher에만 의존한다.
     */
    public static class OrderServiceAfter {
        private final DomainEventPublisher publisher = new DomainEventPublisher();

        public OrderServiceAfter(List<OrderListener> listeners) {
            listeners.forEach(publisher::register);
        }

        public void placeOrder(String userEmail) {
            saveOrder();
            publisher.publish(userEmail);
        }

        private void saveOrder() {
            // save logic
        }
    }

    /**
     * 간단한 Event Publisher.
     * - 리스너 등록/호출 책임을 캡슐화하여 서비스와 핸들러 간 결합을 낮춘다.
     */
    public static class DomainEventPublisher {
        private final List<OrderListener> listeners = new ArrayList<>();

        public void register(OrderListener listener) {
            listeners.add(listener);
        }

        public void publish(String userEmail) {
            listeners.forEach(listener -> listener.onOrder(userEmail));
        }
    }
}
