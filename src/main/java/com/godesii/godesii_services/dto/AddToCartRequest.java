package com.godesii.godesii_services.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

public class AddToCartRequest {

    @NotBlank(message = "Username is required")
    private String username;

    @NotNull(message = "Restaurant ID is required")
    @Positive(message = "Restaurant ID must be positive")
    private Long restaurantId;

    @NotNull(message = "Items list is required")
    @Size(min = 1, message = "At least one item is required")
    @Valid
    private List<CartItemEntry> items;

    // --- Nested item entry ---

    public static class CartItemEntry {

        @NotBlank(message = "Menu item ID is required")
        private String menuItemId;

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        @Max(value = 100, message = "Quantity cannot exceed 100")
        private Integer quantity;

        @Size(max = 500, message = "Special instruction cannot exceed 500 characters")
        private String specialInstruction;

        public String getMenuItemId() {
            return menuItemId;
        }

        public void setMenuItemId(String menuItemId) {
            this.menuItemId = menuItemId;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public String getSpecialInstruction() {
            return specialInstruction;
        }

        public void setSpecialInstruction(String specialInstruction) {
            this.specialInstruction = specialInstruction;
        }
    }

    // --- Getters / Setters ---

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public List<CartItemEntry> getItems() {
        return items;
    }

    public void setItems(List<CartItemEntry> items) {
        this.items = items;
    }
}
