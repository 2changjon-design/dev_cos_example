package com.example.study.lecture.cleancode.spring.practice;

import com.example.study.lecture.cleancode.spring.practice.RefundWorkflowServicePractice.AuditGateway;
import com.example.study.lecture.cleancode.spring.practice.RefundWorkflowServicePractice.LegacyNotificationGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 연습용 컴포넌트들이 주입될 수 있도록 기본 구현을 제공한다.
 * 실제 서비스에서는 외부 메시지/감사 시스템으로 대체하면 된다.
 */
@Configuration
public class SpringPracticeConfig {

    private static final Logger log = LoggerFactory.getLogger(SpringPracticeConfig.class);

    @Bean
    public LegacyNotificationGateway legacyNotificationGateway() {
        return (topic, target, message) ->
                log.info("[practice] notify topic={} target={} msg={}", topic, target, message);
    }

    @Bean
    public AuditGateway auditGateway() {
        return message -> log.info("[practice-audit] {}", message);
    }
}
