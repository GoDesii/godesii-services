package com.godesii.godesii_services.exception;

import java.math.BigDecimal;

public class PriceChangedException extends RuntimeException {

    private final BigDecimal oldPrice;
    private final BigDecimal newPrice;

    public PriceChangedException(String message, BigDecimal oldPrice, BigDecimal newPrice) {
        super(message);
        this.oldPrice = oldPrice;
        this.newPrice = newPrice;
    }

    public BigDecimal getOldPrice() {
        return oldPrice;
    }

    public BigDecimal getNewPrice() {
        return newPrice;
    }
}
