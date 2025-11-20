package com.example.study.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@Order(3)
public class ExecutionTimeAspect {

    @Value("${monitoring.slow-threshold-ms:300}")
    private long slowThresholdMs;

    @Around("com.example.study.aop.pointcut.CommonPointcuts.serviceLayer()")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            return joinPoint.proceed();
        } finally {
            long elapsed = System.currentTimeMillis() - start;
            if (elapsed > slowThresholdMs) {
                log.warn("Slow execution detected: {} took {} ms (threshold {} ms)",
                        joinPoint.getSignature().toShortString(), elapsed, slowThresholdMs);
            } else {
                log.info("Execution time for {}: {} ms",
                        joinPoint.getSignature().toShortString(), elapsed);
            }
        }
    }
}
