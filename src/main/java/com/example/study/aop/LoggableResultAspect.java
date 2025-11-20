package com.example.study.aop;

import com.example.study.aop.support.AspectLogHelper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@Order(2)
public class LoggableResultAspect {

    @AfterReturning(
            pointcut = "com.example.study.aop.pointcut.CommonPointcuts.loggableMethods()",
            returning = "result")
    public void logLoggableResult(JoinPoint joinPoint, Object result) {
        log.info("[@annotation-return] {} -> {}",
                joinPoint.getSignature().toShortString(),
                AspectLogHelper.summarize(result));
    }
}
