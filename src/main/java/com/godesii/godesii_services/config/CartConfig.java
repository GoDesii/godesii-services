package com.godesii.godesii_services.config;

public class CartConfig {

    // Cart expiry configuration
    public static final int CART_EXPIRY_MINUTES = 30;

    // Price component constants (in paise/cents)
    public static final long PACKAGING_CHARGE_PER_ITEM = 5L;
    public static final double GST_RATE = 0.05; // 5%
    public static final long PLATFORM_FEE = 2L;
    public static final long DELIVERY_FEE = 40L; // Placeholder, can be calculated based on distance

    private CartConfig() {
        // Private constructor to prevent instantiation
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
