package com.godesii.godesii_services.service.payment;

import java.time.Instant;

/**
 * Payment response DTO with UPI support
 */
public class PaymentResponse {

    private String paymentId;
    private String paymentUrl;
    private String status;
    private boolean success;
    private String errorMessage;

    // Payment method
    private String paymentMethod;

    // UPI-specific fields
    private String upiQrCode; // Base64 encoded QR code data or UPI string
    private String upiIntentUrl; // Deep link for UPI apps (gpay://, phonepe://, etc.)
    private String merchantVpa; // Merchant's Virtual Payment Address

    // Gateway specific order ID (e.g., Google Pay transaction ID)
    private String gatewayOrderId;

    // Payment expiry
    private Instant expiresAt;

    public PaymentResponse() {
    }

    public PaymentResponse(String paymentId, String paymentUrl, String status, boolean success) {
        this.paymentId = paymentId;
        this.paymentUrl = paymentUrl;
        this.status = status;
        this.success = success;
    }

    // Getters and Setters
    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getPaymentUrl() {
        return paymentUrl;
    }

    public void setPaymentUrl(String paymentUrl) {
        this.paymentUrl = paymentUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getUpiQrCode() {
        return upiQrCode;
    }

    public void setUpiQrCode(String upiQrCode) {
        this.upiQrCode = upiQrCode;
    }

    public String getUpiIntentUrl() {
        return upiIntentUrl;
    }

    public void setUpiIntentUrl(String upiIntentUrl) {
        this.upiIntentUrl = upiIntentUrl;
    }

    public String getMerchantVpa() {
        return merchantVpa;
    }

    public void setMerchantVpa(String merchantVpa) {
        this.merchantVpa = merchantVpa;
    }

    public String getGatewayOrderId() {
        return gatewayOrderId;
    }

    public void setGatewayOrderId(String gatewayOrderId) {
        this.gatewayOrderId = gatewayOrderId;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }
}
