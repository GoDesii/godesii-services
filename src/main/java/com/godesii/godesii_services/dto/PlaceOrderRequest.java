package com.godesii.godesii_services.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for placing an order from cart
 */
public class PlaceOrderRequest {

    @NotBlank(message = "Username is required")
    private String username;

    @Pattern(regexp = "^(UPI|CARD|WALLET|COD|NET_BANKING)$", message = "Payment method must be one of: UPI, CARD, WALLET, COD, NET_BANKING")
    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    @NotNull(message = "Delivery address ID is required")
    @Positive(message = "Delivery address ID must be positive")
    private Long deliveryAddressId;

    @Size(max = 500, message = "Special instructions cannot exceed 500 characters")
    private String specialInstructions;

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Long getDeliveryAddressId() {
        return deliveryAddressId;
    }

    public void setDeliveryAddressId(Long deliveryAddressId) {
        this.deliveryAddressId = deliveryAddressId;
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }
}
