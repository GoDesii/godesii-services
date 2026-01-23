package com.godesii.godesii_services.entity.order;

/**
 * Order status enum representing the complete order lifecycle
 * 
 * State flow:
 * CREATED → PENDING_PAYMENT → PAYMENT_SUCCESS → CONFIRMED → PREPARING
 * → READY_FOR_PICKUP → OUT_FOR_DELIVERY → DELIVERED
 * 
 * Alternative flows:
 * PENDING_PAYMENT → PAYMENT_FAILED → (retry) → PENDING_PAYMENT
 * Any state → CANCELLED → REFUNDED
 */
public enum OrderStatus {

    /**
     * Order created from cart, initial state
     */
    CREATED("Order created from cart"),

    /**
     * Awaiting payment from customer
     */
    PENDING_PAYMENT("Awaiting payment"),

    /**
     * Payment processing failed
     */
    PAYMENT_FAILED("Payment failed"),

    /**
     * Payment successful, awaiting restaurant confirmation
     */
    PAYMENT_SUCCESS("Payment successful"),

    /**
     * Restaurant accepted the order
     */
    CONFIRMED("Order confirmed by restaurant"),

    /**
     * Order is being prepared
     */
    PREPARING("Order is being prepared"),

    /**
     * Order ready for pickup or dispatch
     */
    READY_FOR_PICKUP("Ready for pickup"),

    /**
     * Order out for delivery
     */
    OUT_FOR_DELIVERY("Out for delivery"),

    /**
     * Order successfully delivered
     */
    DELIVERED("Order delivered"),

    /**
     * Order cancelled by user, restaurant, or system
     */
    CANCELLED("Order cancelled"),

    /**
     * Refund processed for cancelled order
     */
    REFUNDED("Refund processed");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Check if order can be cancelled from this status
     */
    public boolean isCancellable() {
        return this != DELIVERED && this != CANCELLED && this != REFUNDED;
    }

    /**
     * Check if order is in a final state
     */
    public boolean isFinalState() {
        return this == DELIVERED || this == CANCELLED || this == REFUNDED;
    }

    /**
     * Check if payment is required for this status
     */
    public boolean requiresPayment() {
        return this == CREATED || this == PENDING_PAYMENT || this == PAYMENT_FAILED;
    }

    /**
     * Check if this status can transition to the target status
     * Enforces valid state machine transitions
     */
    public boolean canTransitionTo(OrderStatus target) {
        if (target == null)
            return false;
        if (this == target)
            return true; // Same state is always valid

        switch (this) {
            case CREATED:
                return target == PENDING_PAYMENT || target == CANCELLED;

            case PENDING_PAYMENT:
                return target == PAYMENT_SUCCESS || target == PAYMENT_FAILED || target == CANCELLED;

            case PAYMENT_FAILED:
                return target == PENDING_PAYMENT || target == CANCELLED; // Allow retry

            case PAYMENT_SUCCESS:
                return target == CONFIRMED || target == CANCELLED;

            case CONFIRMED:
                return target == PREPARING || target == CANCELLED;

            case PREPARING:
                return target == READY_FOR_PICKUP || target == CANCELLED;

            case READY_FOR_PICKUP:
                return target == OUT_FOR_DELIVERY || target == DELIVERED || target == CANCELLED;

            case OUT_FOR_DELIVERY:
                return target == DELIVERED || target == CANCELLED;

            case DELIVERED:
                return false; // Final state - no transitions allowed

            case CANCELLED:
                return target == REFUNDED; // Can only go to refunded

            case REFUNDED:
                return false; // Final state - no transitions allowed

            default:
                return false;
        }
    }
}
