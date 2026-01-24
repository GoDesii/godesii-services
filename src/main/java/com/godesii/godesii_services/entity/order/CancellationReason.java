package com.godesii.godesii_services.entity.order;

/**
 * Cancellation reason categories for comprehensive tracking
 */
public enum CancellationReason {

    /**
     * User decided not to proceed with order
     */
    USER_CHANGED_MIND("User changed mind"),

    /**
     * Restaurant doesn't have items in stock
     */
    ITEM_OUT_OF_STOCK("Item out of stock"),

    /**
     * Restaurant too busy to fulfill order
     */
    RESTAURANT_BUSY("Restaurant too busy"),

    /**
     * Restaurant is closed
     */
    RESTAURANT_CLOSED("Restaurant closed"),

    /**
     * No delivery partners available
     */
    RIDER_UNAVAILABLE("No delivery partner available"),

    /**
     * Delivery partner rejected or cancelled
     */
    RIDER_CANCELLED("Delivery partner cancelled"),

    /**
     * Payment processing failed
     */
    PAYMENT_FAILED("Payment failed"),

    /**
     * Price changed between cart and order
     */
    PRICE_MISMATCH("Price mismatch"),

    /**
     * System timeout or technical issues
     */
    SYSTEM_ERROR("System error"),

    /**
     * Other reason not categorized
     */
    OTHER("Other reason");

    private final String description;

    CancellationReason(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
