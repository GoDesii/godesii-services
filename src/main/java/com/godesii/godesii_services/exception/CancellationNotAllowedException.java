package com.godesii.godesii_services.exception;

import com.godesii.godesii_services.entity.order.OrderStatus;

/**
 * Exception thrown when cancellation is not allowed for current order status
 */
public class CancellationNotAllowedException extends RuntimeException {

    private final String orderId;
    private final OrderStatus currentStatus;

    public CancellationNotAllowedException(String message, String orderId, OrderStatus currentStatus) {
        super(String.format("%s - Order: %s, Status: %s", message, orderId, currentStatus));
        this.orderId = orderId;
        this.currentStatus = currentStatus;
    }

    public CancellationNotAllowedException(String message) {
        super(message);
        this.orderId = null;
        this.currentStatus = null;
    }

    public String getOrderId() {
        return orderId;
    }

    public OrderStatus getCurrentStatus() {
        return currentStatus;
    }
}
