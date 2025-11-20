package com.example.study.common;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ServiceException extends RuntimeException{
    String code;
    String message;

    public ServiceException(ServiceExceptionCode exceptionCode) {
        super(exceptionCode.getMessage());
        this.code = exceptionCode.name();
        this.message = exceptionCode.getMessage();
    }

    public ServiceException(ServiceExceptionCode exceptionCode, String message) {
        super(exceptionCode.getMessage() + " : " + message);
        this.code = exceptionCode.name();
        this.message = message;
    }
}
