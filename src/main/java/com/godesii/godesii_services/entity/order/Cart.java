package com.godesii.godesii_services.entity.order;

import com.godesii.godesii_services.entity.BaseEntity;
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
    private Instant expiresAt;
    private String username;
    private Long restaurantId;
    private List<CartItem> cartItems;
    private Long totalPrice;

    // Cart locking fields
    private Boolean isLocked;
    private Instant lockedAt;
    private String lockedForOrderId;

    /**
     * Check if cart has expired
     * 
     * @return true if cart is expired
     */

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

    @Column(name = "expires_at")
    @Temporal(TemporalType.TIMESTAMP)
    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    @Column(name = "user_name")
    public String getUsername() {
        return username;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    @Column(name = "restaurant_id")
    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Column(name = "total_price")
    public Long getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Long totalPrice) {
        this.totalPrice = totalPrice;
    }

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "cart_selection", // Specifies the name of the join table
            joinColumns = @JoinColumn(name = "cart_id_fk", foreignKey = @ForeignKey(name = "CART_FK")), // Column in
                                                                                                        // join table
                                                                                                        // referring to
                                                                                                        // Cart
            inverseJoinColumns = @JoinColumn(name = "cartitem_id_fk", foreignKey = @ForeignKey(name = "CART_ITEM_FK"))
    // Column in join table referring to CartItem
    )
    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    @Column(name = "is_locked")
    public Boolean getIsLocked() {
        return isLocked != null ? isLocked : false;
    }

    public void setIsLocked(Boolean isLocked) {
        this.isLocked = isLocked;
    }

    @Column(name = "locked_at")
    @Temporal(TemporalType.TIMESTAMP)
    public Instant getLockedAt() {
        return lockedAt;
    }

    public void setLockedAt(Instant lockedAt) {
        this.lockedAt = lockedAt;
    }

    @Column(name = "locked_for_order_id")
    public String getLockedForOrderId() {
        return lockedForOrderId;
    }

    public void setLockedForOrderId(String lockedForOrderId) {
        this.lockedForOrderId = lockedForOrderId;
    }
}
