package com.godesii.godesii_services.dto;

import com.godesii.godesii_services.entity.order.OrderStatus;

import java.time.Instant;

/**
 * Response DTO after order placement
 */
public class PlaceOrderResponse {

    private String orderId;
    private OrderStatus orderStatus;
    private String paymentId;
    private String paymentUrl;
    private Long totalAmount;
    private Instant expiresAt;
    private CartResponse order; // Complete order details with cart info

    // Constructors
    public PlaceOrderResponse() {
    }

    public PlaceOrderResponse(String orderId, OrderStatus orderStatus, String paymentId,
            String paymentUrl, Long totalAmount, Instant expiresAt, CartResponse order) {
        this.orderId = orderId;
        this.orderStatus = orderStatus;
        this.paymentId = paymentId;
        this.paymentUrl = paymentUrl;
        this.totalAmount = totalAmount;
        this.expiresAt = expiresAt;
        this.order = order;
    }

    // Getters and Setters
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

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

    public Long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public CartResponse getOrder() {
        return order;
    }

    public void setOrder(CartResponse order) {
        this.order = order;
    }
}
