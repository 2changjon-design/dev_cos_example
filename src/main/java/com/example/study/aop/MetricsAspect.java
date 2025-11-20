package com.example.study.aop;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@Order(5)
@RequiredArgsConstructor
public class MetricsAspect {

    private final MeterRegistry meterRegistry;

    @Around("com.example.study.aop.pointcut.CommonPointcuts.serviceLayer()")
    public Object recordServiceMetrics(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String operation = resolveOperation(methodName);

        Counter.builder("service.calls")
                .description("Counts service-level invocations grouped by CRUD semantics")
                .tag("class", className)
                .tag("method", methodName)
                .tag("operation", operation)
                .register(meterRegistry)
                .increment();

        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            Object result = joinPoint.proceed();
            stopTimer(sample, className, methodName, operation, "SUCCESS");
            return result;
        } catch (Throwable throwable) {
            stopTimer(sample, className, methodName, operation, "FAILED");
            Counter.builder("service.errors")
                    .description("Counts service exceptions by CRUD semantics")
                    .tag("class", className)
                    .tag("method", methodName)
                    .tag("operation", operation)
                    .register(meterRegistry)
                    .increment();
            throw throwable;
        }
    }

    private void stopTimer(Timer.Sample sample, String className, String methodName, String operation, String status) {
        sample.stop(Timer.builder("service.execution")
                .description("Execution time for service-layer methods")
                .tag("class", className)
                .tag("method", methodName)
                .tag("operation", operation)
                .tag("status", status)
                .register(meterRegistry));
    }

    private String resolveOperation(String methodName) {
        String lower = methodName.toLowerCase();
        if (lower.startsWith("create") || lower.startsWith("save")) {
            return "CREATE";
        }
        if (lower.startsWith("update") || lower.startsWith("modify")) {
            return "UPDATE";
        }
        if (lower.startsWith("delete") || lower.startsWith("remove")) {
            return "DELETE";
        }
        if (lower.startsWith("get") || lower.startsWith("find") || lower.startsWith("search")) {
            return "READ";
        }
        return "OTHER";
    }
}

