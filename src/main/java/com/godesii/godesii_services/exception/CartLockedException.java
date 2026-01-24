package com.godesii.godesii_services.exception;

public class CartLockedException extends RuntimeException {

    private final String cartId;
    private final String lockedForOrderId;

    public CartLockedException(String message, String cartId, String lockedForOrderId) {
        super(message);
        this.cartId = cartId;
        this.lockedForOrderId = lockedForOrderId;
    }

    public String getCartId() {
        return cartId;
    }

    public String getLockedForOrderId() {
        return lockedForOrderId;
    }
}
