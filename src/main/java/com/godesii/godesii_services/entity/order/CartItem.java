package com.godesii.godesii_services.entity.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.List;

@Entity
@Table(name = "cart_item")
public class CartItem {

    private String cartItemId;
    private Long productId;
    private Integer quantity;
    private Long price;
    private Cart cart;
    private String specialInstruction;

    @Id
    @UuidGenerator
    @Column(name = "cart_item_id")
    public String getCartItemId() {
        return cartItemId;
    }

    public void setCartItemId(String cartItemId) {
        this.cartItemId = cartItemId;
    }

    @Column(name = "product_id", nullable = false)
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    @Column(name = "total_quantity")
    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Column(name = "item_price")
    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", insertable = false, updatable = false) // Foreign key in CartItem table (though it's null when using JoinTable)
    public Cart getCarts() {
        return cart;
    }

    public void setCarts(Cart cart) {
        this.cart = cart;
    }

    @Column(name = "special_instruction")
    public String getSpecialInstruction() {
        return specialInstruction;
    }

    public void setSpecialInstruction(String specialInstruction) {
        this.specialInstruction = specialInstruction;
    }

}
