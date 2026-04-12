package com.godesii.godesii_services.exception;

public class CartLockedException extends RuntimeException {

    private final Long cartId;
    private final String lockedForOrderId;

    public CartLockedException(String message, Long cartId, String lockedForOrderId) {
        super(message);
        this.cartId = cartId;
        this.lockedForOrderId = lockedForOrderId;
    }

    public Long getCartId() {
        return cartId;
    }

    public String getLockedForOrderId() {
        return lockedForOrderId;
    }
}
