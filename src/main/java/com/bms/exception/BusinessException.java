package com.bms.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {
    private final HttpStatus status;

    public BusinessException(String message) {
        super(message);
        this.status = HttpStatus.EXPECTATION_FAILED;
    }

    public BusinessException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public BusinessException(String message, HttpStatus status, Throwable cause) {
        super(message, cause);
        this.status = status;
    }
} 