package com.example.study.aop;

import com.example.study.common.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@Order(4)
public class ExceptionLoggingAspect {

    @AfterThrowing(pointcut = "com.example.study.aop.pointcut.CommonPointcuts.serviceLayer()", throwing = "exception")
    public void logServiceException(ServiceException exception) {
        log.error("Service layer exception caught. code={} message={}",
                exception.getCode(), exception.getMessage());
    }

    @AfterThrowing(pointcut = "com.example.study.aop.pointcut.CommonPointcuts.serviceLayer()", throwing = "throwable")
    public void logUnexpectedException(Throwable throwable) {
        if (throwable instanceof ServiceException) {
            return; // already handled above
        }
        log.error("Unexpected service layer exception: {}", throwable.getMessage(), throwable);
    }
}
