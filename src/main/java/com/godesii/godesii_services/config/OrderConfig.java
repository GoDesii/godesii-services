package com.godesii.godesii_services.config;

public class OrderConfig {

    // Timeout configurations (in minutes)
    public static final int PAYMENT_TIMEOUT_MINUTES = 15;
    public static final int RESTAURANT_CONFIRMATION_TIMEOUT_MINUTES = 5;
    public static final int CART_LOCK_TIMEOUT_MINUTES = 20;

    // Auto-cancellation settings
    public static final boolean AUTO_CANCEL_ON_PAYMENT_TIMEOUT = true;
    public static final boolean AUTO_CONFIRM_ON_RESTAURANT_TIMEOUT = false; // Manual confirmation required

    private OrderConfig() {
        // Private constructor to prevent instantiation
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
