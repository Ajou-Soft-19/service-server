package com.ajousw.spring.domain.exception;

public class ApiNotSupportedException extends RuntimeException {
    public ApiNotSupportedException(String message) {
        super(message);
    }

    public ApiNotSupportedException(String message, Throwable cause) {
        super(message, cause);
    }
}
