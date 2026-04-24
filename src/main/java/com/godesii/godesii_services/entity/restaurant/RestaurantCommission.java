package com.godesii.godesii_services.entity.restaurant;

import com.godesii.godesii_services.entity.BaseEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Stores the platform commission configuration for each restaurant.
 * GoDesii deducts this percentage from the restaurant's item total before payout.
 * Modeled after how Zomato/Swiggy handle per-restaurant commission agreements.
 */
@Entity
@Table(name = "restaurant_commission")
public class RestaurantCommission extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The restaurant this commission applies to.
     * One restaurant can have only one active commission record.
     */
    @Column(name = "restaurant_id", nullable = false)
    private Long restaurantId;

    /**
     * Commission percentage charged by GoDesii on the item total.
     * Example: 20.00 means GoDesii takes 20% of itemTotal.
     */
    @Column(name = "commission_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal commissionPercentage;

    /**
     * Date from which this commission rate is valid.
     */
    @Column(name = "effective_from", nullable = false)
    private Instant effectiveFrom;

    /**
     * Whether this commission record is currently active.
     * Only one record per restaurant should be active at a time.
     */
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public BigDecimal getCommissionPercentage() {
        return commissionPercentage;
    }

    public void setCommissionPercentage(BigDecimal commissionPercentage) {
        this.commissionPercentage = commissionPercentage;
    }

    public Instant getEffectiveFrom() {
        return effectiveFrom;
    }

    public void setEffectiveFrom(Instant effectiveFrom) {
        this.effectiveFrom = effectiveFrom;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
