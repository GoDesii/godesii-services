package com.godesii.godesii_services.exception;

import com.godesii.godesii_services.entity.order.OrderStatus;

public class InvalidOrderStateException extends RuntimeException {

    private final String orderId;
    private final OrderStatus currentStatus;
    private final OrderStatus attemptedStatus;

    public InvalidOrderStateException(String message, String orderId, OrderStatus currentStatus,
            OrderStatus attemptedStatus) {
        super(message);
        this.orderId = orderId;
        this.currentStatus = currentStatus;
        this.attemptedStatus = attemptedStatus;
    }

    public String getOrderId() {
        return orderId;
    }

    public OrderStatus getCurrentStatus() {
        return currentStatus;
    }

    public OrderStatus getAttemptedStatus() {
        return attemptedStatus;
    }
}
