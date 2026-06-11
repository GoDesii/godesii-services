package com.godesii.godesii_services.dto;

import com.godesii.godesii_services.entity.delivery.AssignmentStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * WebSocket (STOMP) payload pushed to delivery partners, customers and admins
 * whenever a delivery lifecycle event occurs.
 *
 * <h3>Delivery partner subscribes to:</h3>
 * <pre>/user/queue/delivery</pre>
 *
 * <h3>Customer subscribes to:</h3>
 * <pre>/user/queue/delivery</pre>
 *
 * <h3>Admin / dashboard subscribes to:</h3>
 * <pre>/topic/delivery/all</pre>
 */
public class DeliveryNotification implements Serializable {

    /**
     * All possible delivery-specific notification events.
     */
    public enum NotificationType {

        // ── Partner-facing ────────────────────────────────────────────────────

        /** Server pushed a new assignment to the partner. Partner must ACCEPT or REJECT. */
        NEW_ASSIGNMENT,

        /** Assignment was cancelled before the partner could act (order cancelled). */
        ASSIGNMENT_CANCELLED,

        // ── Partner-action confirmations ──────────────────────────────────────

        /** Partner accepted the delivery successfully. */
        DELIVERY_ACCEPTED,

        /** Partner rejected the delivery; reassignment will be triggered. */
        DELIVERY_REJECTED,

        /** Partner confirmed order pickup from restaurant. */
        ORDER_PICKED_UP,

        /** Partner marked the order as delivered to the customer. */
        ORDER_DELIVERED,

        // ── Location events ───────────────────────────────────────────────────

        /** Partner's real-time location has been updated. */
        LOCATION_UPDATED,

        // ── Customer-facing ───────────────────────────────────────────────────

        /** A delivery partner has been assigned to the customer's order. */
        PARTNER_ASSIGNED,

        /** Partner is on their way to the restaurant. */
        PARTNER_EN_ROUTE_TO_RESTAURANT,

        /** Partner picked up the order and is heading to the customer. */
        OUT_FOR_DELIVERY,

        /** Order has been delivered. */
        DELIVERED,

        // ── Admin / system-facing ─────────────────────────────────────────────

        /** No partner was available; assignment failed. */
        NO_PARTNER_AVAILABLE,

        /** Delivery was reassigned after rejection. */
        DELIVERY_REASSIGNED
    }

    private String assignmentId;
    private String orderId;
    private String partnerId;
    private String partnerName;
    private String partnerPhone;
    private AssignmentStatus assignmentStatus;
    private NotificationType type;
    private BigDecimal partnerLat;
    private BigDecimal partnerLng;
    private Instant timestamp;
    private String message;

    public DeliveryNotification() {
    }

    // ── Factory ──────────────────────────────────────────────────────────────

    /**
     * Build a basic notification without location fields.
     */
    public static DeliveryNotification of(String assignmentId,
                                          String orderId,
                                          String partnerId,
                                          String partnerName,
                                          String partnerPhone,
                                          AssignmentStatus assignmentStatus,
                                          NotificationType type,
                                          String message) {
        DeliveryNotification n = new DeliveryNotification();
        n.assignmentId    = assignmentId;
        n.orderId         = orderId;
        n.partnerId       = partnerId;
        n.partnerName     = partnerName;
        n.partnerPhone    = partnerPhone;
        n.assignmentStatus = assignmentStatus;
        n.type            = type;
        n.timestamp       = Instant.now();
        n.message         = message;
        return n;
    }

    /**
     * Build a location-update notification.
     */
    public static DeliveryNotification locationUpdate(String assignmentId,
                                                      String orderId,
                                                      String partnerId,
                                                      BigDecimal lat,
                                                      BigDecimal lng) {
        DeliveryNotification n = new DeliveryNotification();
        n.assignmentId    = assignmentId;
        n.orderId         = orderId;
        n.partnerId       = partnerId;
        n.type            = NotificationType.LOCATION_UPDATED;
        n.assignmentStatus = AssignmentStatus.ACCEPTED;
        n.partnerLat      = lat;
        n.partnerLng      = lng;
        n.timestamp       = Instant.now();
        n.message         = "Partner location updated";
        return n;
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public String getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(String assignmentId) {
        this.assignmentId = assignmentId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public String getPartnerPhone() {
        return partnerPhone;
    }

    public void setPartnerPhone(String partnerPhone) {
        this.partnerPhone = partnerPhone;
    }

    public AssignmentStatus getAssignmentStatus() {
        return assignmentStatus;
    }

    public void setAssignmentStatus(AssignmentStatus assignmentStatus) {
        this.assignmentStatus = assignmentStatus;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public BigDecimal getPartnerLat() {
        return partnerLat;
    }

    public void setPartnerLat(BigDecimal partnerLat) {
        this.partnerLat = partnerLat;
    }

    public BigDecimal getPartnerLng() {
        return partnerLng;
    }

    public void setPartnerLng(BigDecimal partnerLng) {
        this.partnerLng = partnerLng;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
