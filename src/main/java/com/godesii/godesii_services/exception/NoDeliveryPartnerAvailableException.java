package com.godesii.godesii_services.exception;

/**
 * Exception thrown when no delivery partners are available
 */
public class NoDeliveryPartnerAvailableException extends RuntimeException {

    private final String orderId;
    private final int retriesAttempted;

    public NoDeliveryPartnerAvailableException(String message, String orderId, int retriesAttempted) {
        super(String.format("%s - Order: %s, Retries: %d", message, orderId, retriesAttempted));
        this.orderId = orderId;
        this.retriesAttempted = retriesAttempted;
    }

    public NoDeliveryPartnerAvailableException(String message) {
        super(message);
        this.orderId = null;
        this.retriesAttempted = 0;
    }

    public String getOrderId() {
        return orderId;
    }

    public int getRetriesAttempted() {
        return retriesAttempted;
    }
}
