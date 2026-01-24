package com.godesii.godesii_services.entity.delivery;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Delivery Partner (Rider) entity
 */
@Entity
@Table(name = "delivery_partners")
public class DeliveryPartner {

    private String partnerId;
    private String name;
    private String phone;
    private String email;
    private VehicleType vehicleType;
    private BigDecimal currentLat;
    private BigDecimal currentLng;
    private Boolean isAvailable;
    private BigDecimal rating;
    private Integer totalDeliveries;
    private Instant createdAt;
    private Instant lastActive;

    @Id
    @UuidGenerator
    @Column(name = "partner_id")
    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    @Column(name = "name", nullable = false, length = 100)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "phone", nullable = false, length = 15)
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Column(name = "email", length = 100)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "vehicle_type")
    @Enumerated(EnumType.STRING)
    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    @Column(name = "current_lat", precision = 10, scale = 8)
    public BigDecimal getCurrentLat() {
        return currentLat;
    }

    public void setCurrentLat(BigDecimal currentLat) {
        this.currentLat = currentLat;
    }

    @Column(name = "current_lng", precision = 11, scale = 8)
    public BigDecimal getCurrentLng() {
        return currentLng;
    }

    public void setCurrentLng(BigDecimal currentLng) {
        this.currentLng = currentLng;
    }

    @Column(name = "is_available")
    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    @Column(name = "rating", precision = 3, scale = 2)
    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    @Column(name = "total_deliveries")
    public Integer getTotalDeliveries() {
        return totalDeliveries;
    }

    public void setTotalDeliveries(Integer totalDeliveries) {
        this.totalDeliveries = totalDeliveries;
    }

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Column(name = "last_active")
    @Temporal(TemporalType.TIMESTAMP)
    public Instant getLastActive() {
        return lastActive;
    }

    public void setLastActive(Instant lastActive) {
        this.lastActive = lastActive;
    }
}
