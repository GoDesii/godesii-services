package com.godesii.godesii_services.exception;

public class CartValidationException extends RuntimeException {

    public CartValidationException(String message) {
        super(message);
    }

    public CartValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
