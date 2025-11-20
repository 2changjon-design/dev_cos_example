package com.example.study.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Practice aspect for measuring PurchaseService method execution time with @Around advice.
 */
@Slf4j
@Aspect
@Component
@Order(2)
public class PurchasePerformanceAspect {

    @Pointcut("execution(* com.example.study.service.PurchaseService.*(..))")
    private void purchaseServiceMethods() {
    }

    @Around("purchaseServiceMethods()")
    public Object measurePurchaseLogic(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        log.info("[PurchaseTimer] {} started", joinPoint.getSignature().toShortString());
        try {
            return joinPoint.proceed();
        } finally {
            long elapsed = System.currentTimeMillis() - start;
            log.info("[PurchaseTimer] {} finished in {} ms", joinPoint.getSignature().toShortString(), elapsed);
        }
    }
}

