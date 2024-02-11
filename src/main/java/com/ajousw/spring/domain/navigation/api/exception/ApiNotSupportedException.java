package com.ajousw.spring.domain.navigation.api.exception;

public class ApiNotSupportedException extends RuntimeException {
    public ApiNotSupportedException(String message) {
        super(message);
    }

    public ApiNotSupportedException(String message, Throwable cause) {
        super(message, cause);
    }
}
