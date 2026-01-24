package com.godesii.godesii_services.entity.delivery;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Delivery Assignment linking orders to delivery partners
 */
@Entity
@Table(name = "delivery_assignments")
public class DeliveryAssignment {

    private String assignmentId;
    private String orderId;
    private String partnerId;
    private AssignmentStatus status;
    private Instant assignedAt;
    private Instant acceptedAt;
    private Instant rejectedAt;
    private String rejectionReason;
    private Instant pickedUpAt;
    private Instant deliveredAt;

    // Location tracking
    private BigDecimal pickupLat;
    private BigDecimal pickupLng;
    private BigDecimal dropLat;
    private BigDecimal dropLng;

    @Id
    @UuidGenerator
    @Column(name = "assignment_id")
    public String getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(String assignmentId) {
        this.assignmentId = assignmentId;
    }

    @Column(name = "order_id", nullable = false)
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    @Column(name = "partner_id", nullable = false)
    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    public AssignmentStatus getStatus() {
        return status;
    }

    public void setStatus(AssignmentStatus status) {
        this.status = status;
    }

    @Column(name = "assigned_at")
    @Temporal(TemporalType.TIMESTAMP)
    public Instant getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(Instant assignedAt) {
        this.assignedAt = assignedAt;
    }

    @Column(name = "accepted_at")
    @Temporal(TemporalType.TIMESTAMP)
    public Instant getAcceptedAt() {
        return acceptedAt;
    }

    public void setAcceptedAt(Instant acceptedAt) {
        this.acceptedAt = acceptedAt;
    }

    @Column(name = "rejected_at")
    @Temporal(TemporalType.TIMESTAMP)
    public Instant getRejectedAt() {
        return rejectedAt;
    }

    public void setRejectedAt(Instant rejectedAt) {
        this.rejectedAt = rejectedAt;
    }

    @Column(name = "rejection_reason", length = 500)
    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    @Column(name = "picked_up_at")
    @Temporal(TemporalType.TIMESTAMP)
    public Instant getPickedUpAt() {
        return pickedUpAt;
    }

    public void setPickedUpAt(Instant pickedUpAt) {
        this.pickedUpAt = pickedUpAt;
    }

    @Column(name = "delivered_at")
    @Temporal(TemporalType.TIMESTAMP)
    public Instant getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(Instant deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    @Column(name = "pickup_lat", precision = 10, scale = 8)
    public BigDecimal getPickupLat() {
        return pickupLat;
    }

    public void setPickupLat(BigDecimal pickupLat) {
        this.pickupLat = pickupLat;
    }

    @Column(name = "pickup_lng", precision = 11, scale = 8)
    public BigDecimal getPickupLng() {
        return pickupLng;
    }

    public void setPickupLng(BigDecimal pickupLng) {
        this.pickupLng = pickupLng;
    }

    @Column(name = "drop_lat", precision = 10, scale = 8)
    public BigDecimal getDropLat() {
        return dropLat;
    }

    public void setDropLat(BigDecimal dropLat) {
        this.dropLat = dropLat;
    }

    @Column(name = "drop_lng", precision = 11, scale = 8)
    public BigDecimal getDropLng() {
        return dropLng;
    }

    public void setDropLng(BigDecimal dropLng) {
        this.dropLng = dropLng;
    }
}
