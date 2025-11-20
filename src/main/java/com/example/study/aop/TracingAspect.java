package com.example.study.aop;

import com.example.study.aop.support.AspectLogHelper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.slf4j.MDC;

import java.util.Arrays;
import java.util.UUID;

@Slf4j
@Aspect
@Component
@Order(0)
public class TracingAspect {

    @Around("com.example.study.aop.pointcut.CommonPointcuts.controllerLayer()")
    public Object traceAroundControllers(ProceedingJoinPoint joinPoint) throws Throwable {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        long start = System.currentTimeMillis();
        String signature = joinPoint.getSignature().toShortString();
        log.info("[trace-start] {} traceId={} args={}", signature, traceId, formatArgs(joinPoint.getArgs()));
        try {
            Object result = joinPoint.proceed();
            long elapsed = System.currentTimeMillis() - start;
            log.info("[trace-end] {} traceId={} result={} elapsed={}ms",
                    signature, traceId, AspectLogHelper.summarize(result), elapsed);
            return result;
        } catch (Throwable throwable) {
            long elapsed = System.currentTimeMillis() - start;
            log.error("[trace-error] {} traceId={} elapsed={}ms message={}",
                    signature, traceId, elapsed, throwable.getMessage(), throwable);
            throw throwable;
        } finally {
            MDC.remove("traceId");
        }
    }

    private String formatArgs(Object[] args) {
        if (args == null || args.length == 0) {
            return "(none)";
        }
        String argsString = Arrays.toString(args);
        return argsString.length() > 200 ? argsString.substring(0, 197) + "..." : argsString;
    }
}
