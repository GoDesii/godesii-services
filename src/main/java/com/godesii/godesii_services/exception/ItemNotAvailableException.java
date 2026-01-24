package com.godesii.godesii_services.exception;

public class ItemNotAvailableException extends RuntimeException {

    public ItemNotAvailableException(String message) {
        super(message);
    }

    public ItemNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
