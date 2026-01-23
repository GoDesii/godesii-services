package com.godesii.godesii_services.exception;

import com.godesii.godesii_services.dto.PriceBreakdown;

/**
 * Exception thrown when order price doesn't match expected amount
 */
public class PriceMismatchException extends RuntimeException {

    private final Long expectedAmount;
    private final Long actualAmount;
    private final PriceBreakdown priceBreakdown;

    public PriceMismatchException(String message, Long expectedAmount,
            Long actualAmount, PriceBreakdown priceBreakdown) {
        super(message);
        this.expectedAmount = expectedAmount;
        this.actualAmount = actualAmount;
        this.priceBreakdown = priceBreakdown;
    }

    public Long getExpectedAmount() {
        return expectedAmount;
    }

    public Long getActualAmount() {
        return actualAmount;
    }

    public PriceBreakdown getPriceBreakdown() {
        return priceBreakdown;
    }
}
