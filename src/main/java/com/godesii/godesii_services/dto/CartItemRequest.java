package com.godesii.godesii_services.dto;

import com.godesii.godesii_services.entity.order.CartItem;

import java.util.ArrayList;
import java.util.List;

public class CartItemRequest {

    private String cartItemId;
    private Long productId;
    private Integer quantity;
    private Long price;
    private String specialInstruction;

    public String getCartItemId() {
        return cartItemId;
    }

    public void setCartItemId(String cartItemId) {
        this.cartItemId = cartItemId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
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

    public static CartItem mapToEntity(CartItemRequest item){
        CartItem cartItem = new CartItem();
        cartItem.setProductId(item.getProductId());
        cartItem.setQuantity(item.getQuantity());
        cartItem.setPrice(item.getPrice());
        cartItem.setSpecialInstruction(item.getSpecialInstruction());
        return cartItem;
    }

    public static List<CartItem> mapToEntityList(List<CartItemRequest> items){
        List<CartItem> cartItems = new ArrayList<>();
        for(CartItemRequest item:items){
            cartItems.add(mapToEntity(item));
        }
        return cartItems;
    }
}
