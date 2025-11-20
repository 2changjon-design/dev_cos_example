package com.example.study.lecture.cleancode.spring.answer.refund;

import com.example.study.entity.Purchase;
import org.springframework.stereotype.Component;

@Component
public class RefundNotifier {

    public void notifyRefund(Purchase purchase, String reason) {
        // 실제 운영에서는 메시지 큐/푸시 등을 호출하도록 분리.
        System.out.printf("notify user=%s refund=%s reason=%s%n",
                purchase.getUser().getId(),
                purchase.getId(),
                reason);
    }
}
