package com.godesii.godesii_services.dto;

import com.godesii.godesii_services.entity.order.Cart;

import java.time.Instant;
import java.util.List;

public class CartRequest {

    private Long userId;
    private Long restaurantId;
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

    public static Cart mapToEntity(CartRequest request){
        Cart cart = new Cart();
        cart.setUserId(request.getUserId());
        cart.setRestaurantId(request.getRestaurantId());
        cart.setCreateAt(Instant.now());
        cart.setCartItems(CartItemRequest.mapToEntityList(request.getCartItemRequests()));
        return cart;
    }
}
