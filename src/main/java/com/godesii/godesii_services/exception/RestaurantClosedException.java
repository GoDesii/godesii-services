package com.godesii.godesii_services.exception;

public class RestaurantClosedException extends RuntimeException {

    public RestaurantClosedException(String message) {
        super(message);
    }

    public RestaurantClosedException(String message, Throwable cause) {
        super(message, cause);
    }
}
