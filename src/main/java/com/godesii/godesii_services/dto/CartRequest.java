package com.godesii.godesii_services.dto;

import com.godesii.godesii_services.entity.order.Cart;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;

public class CartRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Restaurant ID is required")
    private Long restaurantId;

    @NotEmpty(message = "Cart must have at least one item")
    @Valid
    private List<CartItemRequest> cartItemRequests;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public List<CartItemRequest> getCartItemRequests() {
        return cartItemRequests;
    }

    public void setCartItemRequests(List<CartItemRequest> cartItemRequests) {
        this.cartItemRequests = cartItemRequests;
    }

    public static Cart mapToEntity(CartRequest request) {
        Cart cart = new Cart();
        cart.setUserId(request.getUserId());
        cart.setRestaurantId(request.getRestaurantId());
        cart.setCreateAt(Instant.now());
        cart.setUpdatedAt(Instant.now());
        cart.setCartItems(CartItemRequest.mapToEntityList(request.getCartItemRequests()));
        // Calculate total price
        long totalPrice = request.getCartItemRequests().stream()
                .mapToLong(item -> item.getPrice() * item.getQuantity())
                .sum();
        cart.setTotalPrice(totalPrice);
        return cart;
    }

    /**
     * Updates existing Cart entity with non-null values from request
     */
    public static void updateEntity(Cart existing, CartRequest request) {
        if (request.getUserId() != null) {
            existing.setUserId(request.getUserId());
        }
        if (request.getRestaurantId() != null) {
            existing.setRestaurantId(request.getRestaurantId());
        }
        if (request.getCartItemRequests() != null && !request.getCartItemRequests().isEmpty()) {
            existing.setCartItems(CartItemRequest.mapToEntityList(request.getCartItemRequests()));
            // Recalculate total price
            long totalPrice = request.getCartItemRequests().stream()
                    .mapToLong(item -> item.getPrice() * item.getQuantity())
                    .sum();
            existing.setTotalPrice(totalPrice);
        }
        existing.setUpdatedAt(Instant.now());
    }
}
