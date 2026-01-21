package com.godesii.godesii_services.dto;

import com.godesii.godesii_services.entity.order.OrderItem;
import jakarta.validation.constraints.*;

import java.util.List;
import java.util.stream.Collectors;

public class OrderItemRequest {

    @NotBlank(message = "Product ID is required")
    private String productId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 100, message = "Quantity cannot exceed 100")
    private Integer quantity;

    @NotNull(message = "Price at purchase is required")
    @Positive(message = "Price must be positive")
    private Long priceAtPurchase;

    // Getters and Setters
    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Long getPriceAtPurchase() {
        return priceAtPurchase;
    }

    public void setPriceAtPurchase(Long priceAtPurchase) {
        this.priceAtPurchase = priceAtPurchase;
    }

    /**
     * Maps OrderItemRequest DTO to OrderItem entity
     */
    public static OrderItem mapToEntity(OrderItemRequest request) {
        OrderItem orderItem = new OrderItem();
        orderItem.setProductId(request.getProductId());
        orderItem.setQuantity(request.getQuantity());
        orderItem.setPriceAtPurchase(request.getPriceAtPurchase());
        return orderItem;
    }

    /**
     * Maps list of OrderItemRequest DTOs to list of OrderItem entities
     */
    public static List<OrderItem> mapToEntities(List<OrderItemRequest> requests) {
        if (requests == null) {
            return null;
        }
        return requests.stream()
                .map(OrderItemRequest::mapToEntity)
                .collect(Collectors.toList());
    }

    /**
     * Updates existing OrderItem entity with non-null values from request
     */
    public static void updateEntity(OrderItem existing, OrderItemRequest request) {
        if (request.getProductId() != null) {
            existing.setProductId(request.getProductId());
        }
        if (request.getQuantity() != null) {
            existing.setQuantity(request.getQuantity());
        }
        if (request.getPriceAtPurchase() != null) {
            existing.setPriceAtPurchase(request.getPriceAtPurchase());
        }
    }
}
