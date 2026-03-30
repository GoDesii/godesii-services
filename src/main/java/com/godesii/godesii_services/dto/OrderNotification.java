package com.godesii.godesii_services.dto;

import com.godesii.godesii_services.entity.order.Order;
import com.godesii.godesii_services.entity.order.OrderStatus;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO pushed to restaurant's website via WebSocket
 * whenever an order event occurs (placed, cancelled, status change, etc.)
 */
public class OrderNotification implements Serializable {

    public enum NotificationType {
        // Restaurant-facing
        NEW_ORDER,
        ORDER_CANCELLED,
        ORDER_STATUS_UPDATED,
        PAYMENT_FAILED,

        // Customer-facing
        ORDER_CONFIRMED,
        ORDER_REJECTED,
        ORDER_PREPARING,
        ORDER_OUT_FOR_DELIVERY,
        ORDER_DELIVERED,
        REFUND_INITIATED
    }

    private String orderId;
    private String restaurantId;
    private String customerName;
    private NotificationType type;
    private OrderStatus orderStatus;
    private Long totalAmount;
    private String deliveryNotes;
    private Instant timestamp;
    private String message;

    public OrderNotification() {
    }

    /**
     * Factory: build notification from an Order entity.
     */
    public static OrderNotification from(Order order, NotificationType type, String message) {
        OrderNotification n = new OrderNotification();
        n.orderId = order.getOrderId();
        n.restaurantId = order.getRestaurantId();
        n.customerName = order.getUsername();
        n.type = type;
        n.orderStatus = order.getOrderStatus();
        n.totalAmount = order.getTotalAmount();
        n.deliveryNotes = order.getDeliveryNotes();
        n.timestamp = Instant.now();
        n.message = message;
        return n;
    }

    // ---- Getters & Setters ----

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getDeliveryNotes() {
        return deliveryNotes;
    }

    public void setDeliveryNotes(String deliveryNotes) {
        this.deliveryNotes = deliveryNotes;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
