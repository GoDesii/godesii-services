package com.godesii.godesii_services.exception;

public class PaymentFailedException extends RuntimeException {

    private final String paymentId;
    private final String failureReason;

    public PaymentFailedException(String message, String paymentId, String failureReason) {
        super(message);
        this.paymentId = paymentId;
        this.failureReason = failureReason;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public String getFailureReason() {
        return failureReason;
    }
}
