package com.godesii.godesii_services.exception;

import com.godesii.godesii_services.entity.order.OrderStatus;

/**
 * Exception thrown when an invalid order state transition is attempted
 */
public class InvalidStateTransitionException extends RuntimeException {

    private final String orderId;
    private final OrderStatus currentStatus;
    private final OrderStatus targetStatus;

    public InvalidStateTransitionException(String message, String orderId,
            OrderStatus currentStatus, OrderStatus targetStatus) {
        super(String.format("%s - Order: %s, Current: %s, Attempted: %s",
                message, orderId, currentStatus, targetStatus));
        this.orderId = orderId;
        this.currentStatus = currentStatus;
        this.targetStatus = targetStatus;
    }

    public String getOrderId() {
        return orderId;
    }

    public OrderStatus getCurrentStatus() {
        return currentStatus;
    }

    public OrderStatus getTargetStatus() {
        return targetStatus;
    }
}
