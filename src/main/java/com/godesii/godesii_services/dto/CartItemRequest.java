package com.godesii.godesii_services.dto;

import com.godesii.godesii_services.entity.order.CartItem;
import jakarta.validation.constraints.*;

import java.util.ArrayList;
import java.util.List;

public class CartItemRequest {

    private String cartItemId;

    @NotNull(message = "Product ID is required")
    @NotBlank(message = "Product ID must not be blank")
    private String productId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 100, message = "Quantity cannot exceed 100")
    private Integer quantity;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private Long price;

    @Size(max = 500, message = "Special instruction cannot exceed 500 characters")
    private String specialInstruction;

    public String getCartItemId() {
        return cartItemId;
    }

    public void setCartItemId(String cartItemId) {
        this.cartItemId = cartItemId;
    }

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

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public String getSpecialInstruction() {
        return specialInstruction;
    }

    public void setSpecialInstruction(String specialInstruction) {
        this.specialInstruction = specialInstruction;
    }

    public static CartItem mapToEntity(CartItemRequest item) {
        CartItem cartItem = new CartItem();
        cartItem.setProductId(item.getProductId());
        cartItem.setQuantity(item.getQuantity());
        cartItem.setPrice(item.getPrice());
        cartItem.setSpecialInstruction(item.getSpecialInstruction());
        return cartItem;
    }

    public static List<CartItem> mapToEntityList(List<CartItemRequest> items) {
        List<CartItem> cartItems = new ArrayList<>();
        for (CartItemRequest item : items) {
            cartItems.add(mapToEntity(item));
        }
        return cartItems;
    }

    /**
     * Updates existing CartItem entity with non-null values from request
     */
    public static void updateEntity(CartItem existing, CartItemRequest request) {
        if (request.getProductId() != null) {
            existing.setProductId(request.getProductId());
        }
        if (request.getQuantity() != null) {
            existing.setQuantity(request.getQuantity());
        }
        if (request.getPrice() != null) {
            existing.setPrice(request.getPrice());
        }
        if (request.getSpecialInstruction() != null) {
            existing.setSpecialInstruction(request.getSpecialInstruction());
        }
    }
}
