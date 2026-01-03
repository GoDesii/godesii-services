package com.godesii.godesii_services.entity.order;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "cart")
public class Cart {

    private String id;
    private Instant createAt;
    private Instant updatedAt;
    private Long userId;
    private Long restaurantId;
    private List<CartItem> cartItems;
    private Long totalPrice;

    @Id
    @UuidGenerator()
    @Column(name = "cart_id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    public Instant getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Instant createAt) {
        this.createAt = createAt;
    }

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Column(name = "user_id")
    public Long getUserId() {
        return userId;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    @Column(name = "restaurant_id")
    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Column(name = "total_price")
    public Long getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Long totalPrice) {
        this.totalPrice = totalPrice;
    }

    @JoinTable(
            name = "cart_selections",
            joinColumns = @JoinColumn(
                    name = "cart_id",
                    foreignKey = @ForeignKey(name = "FK_CART_ID")
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "cart_item_id",
                    foreignKey = @ForeignKey(name = "FK_CART_ITEM_ID")
            )
    )
    @ManyToMany(fetch = FetchType.LAZY)
    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }
}
