package com.godesii.godesii_services.controller.delivery;

import com.godesii.godesii_services.dto.DeliveryNotification;
import com.godesii.godesii_services.entity.delivery.DeliveryAssignment;
import com.godesii.godesii_services.entity.delivery.DeliveryPartner;
import com.godesii.godesii_services.exception.ResourceNotFoundException;
import com.godesii.godesii_services.repository.delivery.DeliveryAssignmentRepository;
import com.godesii.godesii_services.repository.delivery.DeliveryPartnerRepository;
import com.godesii.godesii_services.service.delivery.DeliveryNotificationService;
import com.godesii.godesii_services.service.delivery.DeliveryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;

/**
 * WebSocket (STOMP) controller for the <b>Delivery Partner</b> app.
 *
 * <h3>Delivery partner subscribes to (personal queue):</h3>
 * <pre>/user/queue/delivery</pre>
 *
 * <h3>Customer subscribes to (per-order tracking):</h3>
 * <pre>/topic/order/{orderId}/delivery</pre>
 *
 * <h3>Admin dashboard subscribes to (all events):</h3>
 * <pre>/topic/delivery/all</pre>
 *
 * <h3>Actions sent from the partner app (prefix /app):</h3>
 * <pre>
 *   /app/delivery/assignments/{assignmentId}/accept    → partner accepts
 *   /app/delivery/assignments/{assignmentId}/reject    → partner rejects
 *   /app/delivery/assignments/{assignmentId}/pickup    → partner confirms pickup
 *   /app/delivery/assignments/{assignmentId}/deliver   → partner marks delivered
 *   /app/delivery/partners/{partnerId}/location       → partner sends live location
 *   /app/delivery/assignments/{assignmentId}/status   → query current status
 * </pre>
 */
@Controller
public class DeliveryNotificationController {

    private static final Logger log = LoggerFactory.getLogger(DeliveryNotificationController.class);

    private final DeliveryService deliveryService;
    private final DeliveryNotificationService notificationService;
    private final DeliveryAssignmentRepository assignmentRepo;
    private final DeliveryPartnerRepository partnerRepo;

    public DeliveryNotificationController(DeliveryService deliveryService,
                                          DeliveryNotificationService notificationService,
                                          DeliveryAssignmentRepository assignmentRepo,
                                          DeliveryPartnerRepository partnerRepo) {
        this.deliveryService      = deliveryService;
        this.notificationService  = notificationService;
        this.assignmentRepo       = assignmentRepo;
        this.partnerRepo          = partnerRepo;
    }

    // ── Status query ──────────────────────────────────────────────────────────

    /**
     * Query the current status of an assignment.
     *
     * <p>Partner / admin sends: {@code /app/delivery/assignments/{assignmentId}/status}
     * <p>Triggers a notification broadcast to the admin topic.
     */
    @MessageMapping("/delivery/assignments/{assignmentId}/status")
    public void handleStatusQuery(@DestinationVariable String assignmentId) {
        log.info("WebSocket: status query for assignment {}", assignmentId);

        DeliveryAssignment assignment = requireAssignment(assignmentId);
        DeliveryPartner    partner    = requirePartner(assignment.getPartnerId());

        DeliveryNotification notification = DeliveryNotification.of(
                assignment.getAssignmentId(),
                assignment.getOrderId(),
                partner.getPartnerId(),
                partner.getName(),
                partner.getPhone(),
                assignment.getStatus(),
                DeliveryNotification.NotificationType.DELIVERY_ACCEPTED, // reuse generic status update
                "Assignment " + assignmentId + " is currently: " + assignment.getStatus().getDescription());

        notificationService.broadcastStatusUpdate(notification);
    }

    // ── Partner actions ───────────────────────────────────────────────────────

    /**
     * Partner accepts the delivery assignment.
     *
     * <p>Partner sends: {@code /app/delivery/assignments/{assignmentId}/accept}
     * <br>Notifies: partner (personal queue) + customer (order topic) + admin (broadcast)
     */
    @MessageMapping("/delivery/assignments/{assignmentId}/accept")
    public void handleAccept(@DestinationVariable String assignmentId) {
        log.info("WebSocket: partner accepting assignment {}", assignmentId);

        DeliveryAssignment assignment = deliveryService.partnerAcceptsDelivery(assignmentId);
        DeliveryPartner    partner    = requirePartner(assignment.getPartnerId());

        notificationService.notifyDeliveryAccepted(assignment, partner);
        log.info("Assignment {} accepted; notifications sent", assignmentId);
    }

    /**
     * Partner rejects the delivery assignment.
     *
     * <p>Partner sends: {@code /app/delivery/assignments/{assignmentId}/reject}
     * <br>Payload: {@code { "reason": "Too far away" }}
     * <br>Notifies: partner (personal queue) + admin (broadcast)
     * <br>Side-effect: triggers reassignment logic.
     */
    @MessageMapping("/delivery/assignments/{assignmentId}/reject")
    public void handleReject(@DestinationVariable String assignmentId,
                             @Payload DeliveryRejectRequest request) {
        log.info("WebSocket: partner rejecting assignment {} – reason: {}", assignmentId, request.getReason());

        DeliveryAssignment assignment = requireAssignment(assignmentId);
        DeliveryPartner    partner    = requirePartner(assignment.getPartnerId());

        deliveryService.partnerRejectsDelivery(assignmentId, request.getReason());
        notificationService.notifyDeliveryRejected(assignment, partner);
        log.info("Assignment {} rejected; notifications sent", assignmentId);
    }

    /**
     * Partner confirms they have picked up the order from the restaurant.
     *
     * <p>Partner sends: {@code /app/delivery/assignments/{assignmentId}/pickup}
     * <br>Notifies: partner (personal queue) + customer OUT_FOR_DELIVERY + admin (broadcast)
     */
    @MessageMapping("/delivery/assignments/{assignmentId}/pickup")
    public void handlePickup(@DestinationVariable String assignmentId) {
        log.info("WebSocket: partner marking assignment {} as PICKED_UP", assignmentId);

        DeliveryAssignment assignment = deliveryService.markAsPickedUp(assignmentId);
        DeliveryPartner    partner    = requirePartner(assignment.getPartnerId());

        notificationService.notifyOrderPickedUp(assignment, partner);
        log.info("Assignment {} marked PICKED_UP; notifications sent", assignmentId);
    }

    /**
     * Partner marks the order as delivered to the customer.
     *
     * <p>Partner sends: {@code /app/delivery/assignments/{assignmentId}/deliver}
     * <br>Notifies: partner (personal queue) + customer DELIVERED + admin (broadcast)
     */
    @MessageMapping("/delivery/assignments/{assignmentId}/deliver")
    public void handleDeliver(@DestinationVariable String assignmentId) {
        log.info("WebSocket: partner marking assignment {} as DELIVERED", assignmentId);

        DeliveryAssignment assignment = deliveryService.markAsDelivered(assignmentId);
        DeliveryPartner    partner    = requirePartner(assignment.getPartnerId());

        notificationService.notifyOrderDelivered(assignment, partner);
        log.info("Assignment {} marked DELIVERED; notifications sent", assignmentId);
    }

    // ── Real-time location ────────────────────────────────────────────────────

    /**
     * Partner streams their GPS location while on a delivery.
     *
     * <p>Partner sends: {@code /app/delivery/partners/{partnerId}/location}
     * <br>Payload: {@code { "latitude": 12.9716, "longitude": 77.5946 }}
     * <br>Broadcasts: per-order customer tracking topic + admin
     */
    @MessageMapping("/delivery/partners/{partnerId}/location")
    public void handleLocationUpdate(@DestinationVariable String partnerId,
                                     @Payload LocationUpdateRequest request) {
        log.debug("WebSocket: location update from partner {} → ({}, {})",
                partnerId, request.getLatitude(), request.getLongitude());

        deliveryService.updateDeliveryLocation(partnerId,
                request.getLatitude(), request.getLongitude());

        // Fetch active assignment to attach orderId to the notification
        assignmentRepo.findActiveAssignmentByPartnerId(partnerId).ifPresent(assignment ->
                notificationService.notifyLocationUpdate(
                        assignment.getAssignmentId(),
                        assignment.getOrderId(),
                        partnerId,
                        request.getLatitude(),
                        request.getLongitude()));
    }

    // ── Subscription hooks ────────────────────────────────────────────────────

    /**
     * Triggered when a delivery partner subscribes to their personal queue.
     * Can be used to push any pending assignments immediately.
     *
     * <p>Partner subscribes to: {@code /user/queue/delivery}
     */
    @SubscribeMapping("/queue/delivery")
    public void onPartnerSubscribe() {
        log.info("A delivery partner subscribed to their delivery queue");
        // Optional: push pending assignment if any
    }

    /**
     * Triggered when a customer subscribes to the per-order delivery tracking topic.
     *
     * <p>Customer subscribes to: {@code /topic/order/{orderId}/delivery}
     */
    @SubscribeMapping("/topic/order/{orderId}/delivery")
    public void onCustomerSubscribe(@DestinationVariable String orderId) {
        log.info("Customer subscribed to delivery tracking for order {}", orderId);
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private DeliveryAssignment requireAssignment(String assignmentId) {
        return assignmentRepo.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found: " + assignmentId));
    }

    private DeliveryPartner requirePartner(String partnerId) {
        return partnerRepo.findById(partnerId)
                .orElseThrow(() -> new ResourceNotFoundException("Partner not found: " + partnerId));
    }

    // ── Inner payload DTOs ────────────────────────────────────────────────────

    /**
     * Payload for reject action: {@code { "reason": "..." }}
     */
    public static class DeliveryRejectRequest {
        private String reason;

        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }

    /**
     * Payload for real-time location updates: {@code { "latitude": ..., "longitude": ... }}
     */
    public static class LocationUpdateRequest {
        private BigDecimal latitude;
        private BigDecimal longitude;

        public BigDecimal getLatitude()  { return latitude; }
        public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }

        public BigDecimal getLongitude() { return longitude; }
        public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }
    }
}
