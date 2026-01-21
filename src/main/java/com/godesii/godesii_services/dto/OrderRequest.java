package com.godesii.godesii_services.dto;

import com.godesii.godesii_services.entity.order.Order;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.List;

public class OrderRequest {

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotBlank(message = "Restaurant ID is required")
    private String restaurantId;

    @Size(max = 50, message = "Order status cannot exceed 50 characters")
    private String orderStatus;

    @NotEmpty(message = "Order must have at least one item")
    @Valid
    private List<OrderItemRequest> orderItems;

    @Valid
    private OrderAddressRequest orderAddress;

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public List<OrderItemRequest> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemRequest> orderItems) {
        this.orderItems = orderItems;
    }

    public OrderAddressRequest getOrderAddress() {
        return orderAddress;
    }

    public void setOrderAddress(OrderAddressRequest orderAddress) {
        this.orderAddress = orderAddress;
    }

    /**
     * Maps OrderRequest DTO to Order entity
     */
    public static Order mapToEntity(OrderRequest request) {
        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setRestaurantId(request.getRestaurantId());
        order.setOrderStatus(request.getOrderStatus() != null ? request.getOrderStatus() : "PENDING");
        order.setOrderDate(Instant.now());
        order.setOrderItems(OrderItemRequest.mapToEntities(request.getOrderItems()));
        order.setOrderAddress(OrderAddressRequest.mapToEntity(request.getOrderAddress()));

        // Calculate total amount
        long totalAmount = request.getOrderItems().stream()
                .mapToLong(item -> item.getPriceAtPurchase() * item.getQuantity())
                .sum();
        order.setTotalAmount(totalAmount);

        return order;
    }

    /**
     * Updates existing Order entity with non-null values from request
     */
    public static void updateEntity(Order existing, OrderRequest request) {
        if (request.getUserId() != null) {
            existing.setUserId(request.getUserId());
        }
        if (request.getRestaurantId() != null) {
            existing.setRestaurantId(request.getRestaurantId());
        }
        if (request.getOrderStatus() != null) {
            existing.setOrderStatus(request.getOrderStatus());
        }
        if (request.getOrderItems() != null && !request.getOrderItems().isEmpty()) {
            existing.setOrderItems(OrderItemRequest.mapToEntities(request.getOrderItems()));
            // Recalculate total amount
            long totalAmount = request.getOrderItems().stream()
                    .mapToLong(item -> item.getPriceAtPurchase() * item.getQuantity())
                    .sum();
            existing.setTotalAmount(totalAmount);
        }
        if (request.getOrderAddress() != null) {
            existing.setOrderAddress(
                    OrderAddressRequest.updateEntity(existing.getOrderAddress(), request.getOrderAddress()));
        }
    }
}
