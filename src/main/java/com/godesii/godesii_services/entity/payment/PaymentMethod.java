package com.godesii.godesii_services.entity.payment;

/**
 * Payment method enum for all supported payment options
 */
public enum PaymentMethod {

    /**
     * UPI payment (Google Pay, PhonePe, Paytm, etc.)
     */
    UPI("UPI Payment", "Unified Payments Interface"),

    /**
     * Credit/Debit card payment
     */
    CARD("Card Payment", "Credit/Debit Card"),

    /**
     * Digital wallet payment (Paytm, PhonePe wallet, etc.)
     */
    WALLET("Wallet Payment", "Digital Wallet"),

    /**
     * Net banking payment
     */
    NET_BANKING("Net Banking", "Internet Banking"),

    /**
     * Cash on delivery
     */
    COD("Cash on Delivery", "Pay on delivery");

    private final String displayName;
    private final String description;

    PaymentMethod(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Check if this payment method requires online payment gateway
     */
    public boolean requiresOnlinePayment() {
        return this != COD;
    }

    /**
     * Check if this payment method supports instant confirmation
     */
    public boolean supportsInstantConfirmation() {
        return this == UPI || this == WALLET;
    }

    /**
     * Get payment method from string, case-insensitive
     */
    public static PaymentMethod fromString(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Payment method cannot be null");
        }

        try {
            return PaymentMethod.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid payment method: " + value +
                    ". Valid options are: UPI, CARD, WALLET, NET_BANKING, COD");
        }
    }
}
