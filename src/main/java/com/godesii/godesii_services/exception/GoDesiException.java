package com.godesii.godesii_services.exception;


public class GoDesiException extends RuntimeException {

    public GoDesiException(String message) {
        super(message);
    }

    public GoDesiException(String message, Throwable cause) {
        super(message, cause);
    }

    public GoDesiException(Throwable cause) {
        super(cause);
    }
}
