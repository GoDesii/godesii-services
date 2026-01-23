package com.godesii.godesii_services.exception;

/**
 * Exception thrown when order placement fails
 */
public class OrderPlacementFailedException extends RuntimeException {

    public OrderPlacementFailedException(String message) {
        super(message);
    }

    public OrderPlacementFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
