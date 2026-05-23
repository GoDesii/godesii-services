package com.godesii.godesii_services.service.delivery;

import com.godesii.godesii_services.dto.DeliveryNotification;
import com.godesii.godesii_services.dto.DeliveryNotification.NotificationType;
import com.godesii.godesii_services.entity.delivery.AssignmentStatus;
import com.godesii.godesii_services.entity.delivery.DeliveryAssignment;
import com.godesii.godesii_services.entity.delivery.DeliveryPartner;
import com.godesii.godesii_services.service.FcmService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Sends real-time delivery lifecycle notifications via:
 * <ol>
 *   <li><b>WebSocket (STOMP)</b> — for in-app connected clients</li>
 *   <li><b>FCM Push Notification</b> — for mobile clients in the background</li>
 * </ol>
 *
 * <h3>WebSocket topic / queue layout</h3>
 * <pre>
 *   /user/queue/delivery              → delivery partner (personal queue)
 *   /topic/order/{orderId}/delivery   → customer tracking feed for a specific order
 *   /topic/delivery/all               → admin / ops dashboard (all events)
 * </pre>
 *
 * <h3>FCM topic layout</h3>
 * <pre>
 *   delivery-partner-{partnerId}      → partner device(s) subscribed at login
 * </pre>
 */
@Service
public class DeliveryNotificationService {

    private static final Logger log = LoggerFactory.getLogger(DeliveryNotificationService.class);

    /** Personal WebSocket queue consumed by the delivery partner app. */
    private static final String PARTNER_QUEUE = "/queue/delivery";

    /** Per-order WebSocket topic consumed by the customer tracking screen. */
    private static final String CUSTOMER_ORDER_TOPIC = "/topic/order/%s/delivery";

    /** WebSocket broadcast topic consumed by admin / ops dashboard. */
    private static final String ADMIN_TOPIC = "/topic/delivery/all";

    /** FCM topic prefix for delivery partners — subscribed to at login. */
    private static final String FCM_PARTNER_TOPIC_PREFIX = "delivery-partner-";

    private final SimpMessagingTemplate messaging;
    private final FcmService fcmService;

    public DeliveryNotificationService(SimpMessagingTemplate messaging, FcmService fcmService) {
        this.messaging  = messaging;
        this.fcmService = fcmService;
    }

    // ── Partner-facing notifications ──────────────────────────────────────────

    /**
     * Push a new assignment to the delivery partner via WebSocket + FCM.
     */
    public void notifyNewAssignment(DeliveryAssignment assignment, DeliveryPartner partner) {
        String msg = "New delivery assignment! Order #" + assignment.getOrderId();
        DeliveryNotification n = DeliveryNotification.of(
                assignment.getAssignmentId(), assignment.getOrderId(),
                partner.getPartnerId(), partner.getName(), partner.getPhone(),
                AssignmentStatus.ASSIGNED, NotificationType.NEW_ASSIGNMENT, msg);

        sendToPartner(partner.getPartnerId(), n);
        broadcastToAdmin(n);

        // FCM push — partner may have the app backgrounded
        fcmService.sendToTopic(
                FCM_PARTNER_TOPIC_PREFIX + partner.getPartnerId(),
                "🛵 New Delivery Request",
                msg,
                buildData(n));
    }

    /**
     * Confirm to the partner that their acceptance was recorded.
     */
    public void notifyDeliveryAccepted(DeliveryAssignment assignment, DeliveryPartner partner) {
        String msg = "You have accepted order #" + assignment.getOrderId();
        DeliveryNotification n = DeliveryNotification.of(
                assignment.getAssignmentId(), assignment.getOrderId(),
                partner.getPartnerId(), partner.getName(), partner.getPhone(),
                AssignmentStatus.ACCEPTED, NotificationType.DELIVERY_ACCEPTED, msg);

        sendToPartner(partner.getPartnerId(), n);
        sendToCustomerOrderTopic(assignment.getOrderId(), n);
        broadcastToAdmin(n);

        // FCM push to partner
        fcmService.sendToTopic(
                FCM_PARTNER_TOPIC_PREFIX + partner.getPartnerId(),
                "✅ Order Accepted",
                msg,
                buildData(n));
    }

    /**
     * Confirm to the partner that their rejection was recorded.
     */
    public void notifyDeliveryRejected(DeliveryAssignment assignment, DeliveryPartner partner) {
        String msg = "You rejected order #" + assignment.getOrderId() + ". Reassigning...";
        DeliveryNotification n = DeliveryNotification.of(
                assignment.getAssignmentId(), assignment.getOrderId(),
                partner.getPartnerId(), partner.getName(), partner.getPhone(),
                AssignmentStatus.REJECTED, NotificationType.DELIVERY_REJECTED, msg);

        sendToPartner(partner.getPartnerId(), n);
        broadcastToAdmin(n);
    }

    /**
     * Notify partner + customer that the order was picked up from the restaurant.
     */
    public void notifyOrderPickedUp(DeliveryAssignment assignment, DeliveryPartner partner) {
        String partnerMsg = "Order #" + assignment.getOrderId() + " picked up — heading to customer";
        DeliveryNotification n = DeliveryNotification.of(
                assignment.getAssignmentId(), assignment.getOrderId(),
                partner.getPartnerId(), partner.getName(), partner.getPhone(),
                AssignmentStatus.PICKED_UP, NotificationType.ORDER_PICKED_UP, partnerMsg);
        sendToPartner(partner.getPartnerId(), n);

        // Customer — OUT_FOR_DELIVERY
        String customerMsg = "Your order #" + assignment.getOrderId() + " is out for delivery! 🛵";
        DeliveryNotification customerNotif = DeliveryNotification.of(
                assignment.getAssignmentId(), assignment.getOrderId(),
                partner.getPartnerId(), partner.getName(), partner.getPhone(),
                AssignmentStatus.PICKED_UP, NotificationType.OUT_FOR_DELIVERY, customerMsg);
        sendToCustomerOrderTopic(assignment.getOrderId(), customerNotif);

        broadcastToAdmin(n);

        // FCM push to partner confirmation
        fcmService.sendToTopic(
                FCM_PARTNER_TOPIC_PREFIX + partner.getPartnerId(),
                "📦 Order Picked Up",
                partnerMsg,
                buildData(n));
    }

    /**
     * Notify partner + customer that the order was delivered successfully.
     */
    public void notifyOrderDelivered(DeliveryAssignment assignment, DeliveryPartner partner) {
        String partnerMsg = "Order #" + assignment.getOrderId() + " delivered successfully 🎉";
        DeliveryNotification n = DeliveryNotification.of(
                assignment.getAssignmentId(), assignment.getOrderId(),
                partner.getPartnerId(), partner.getName(), partner.getPhone(),
                AssignmentStatus.DELIVERED, NotificationType.ORDER_DELIVERED, partnerMsg);
        sendToPartner(partner.getPartnerId(), n);

        // Customer
        String customerMsg = "Your order #" + assignment.getOrderId() + " has been delivered. Enjoy! 😋";
        DeliveryNotification customerNotif = DeliveryNotification.of(
                assignment.getAssignmentId(), assignment.getOrderId(),
                partner.getPartnerId(), partner.getName(), partner.getPhone(),
                AssignmentStatus.DELIVERED, NotificationType.DELIVERED, customerMsg);
        sendToCustomerOrderTopic(assignment.getOrderId(), customerNotif);

        broadcastToAdmin(n);

        // FCM push to partner
        fcmService.sendToTopic(
                FCM_PARTNER_TOPIC_PREFIX + partner.getPartnerId(),
                "✅ Delivery Complete",
                partnerMsg,
                buildData(n));
    }

    /**
     * Notify the partner that their active assignment was cancelled.
     */
    public void notifyAssignmentCancelled(DeliveryAssignment assignment, DeliveryPartner partner) {
        String msg = "Assignment for order #" + assignment.getOrderId() + " has been cancelled";
        DeliveryNotification n = DeliveryNotification.of(
                assignment.getAssignmentId(), assignment.getOrderId(),
                partner.getPartnerId(), partner.getName(), partner.getPhone(),
                AssignmentStatus.CANCELLED, NotificationType.ASSIGNMENT_CANCELLED, msg);

        sendToPartner(partner.getPartnerId(), n);
        broadcastToAdmin(n);

        // FCM push — partner must know even if app is closed
        fcmService.sendToTopic(
                FCM_PARTNER_TOPIC_PREFIX + partner.getPartnerId(),
                "❌ Assignment Cancelled",
                msg,
                buildData(n));
    }

    /**
     * Notify admin that delivery was reassigned to a new partner.
     */
    public void notifyDeliveryReassigned(String orderId, String newPartnerId) {
        DeliveryNotification n = DeliveryNotification.of(
                null, orderId, newPartnerId, null, null,
                AssignmentStatus.ASSIGNED, NotificationType.DELIVERY_REASSIGNED,
                "Order #" + orderId + " reassigned to partner " + newPartnerId);
        broadcastToAdmin(n);
    }

    /**
     * Notify admin that no delivery partner was available for an order.
     */
    public void notifyNoPartnerAvailable(String orderId) {
        DeliveryNotification n = DeliveryNotification.of(
                null, orderId, null, null, null,
                null, NotificationType.NO_PARTNER_AVAILABLE,
                "No delivery partner available for order #" + orderId);
        broadcastToAdmin(n);
    }

    // ── Location event ────────────────────────────────────────────────────────

    /**
     * Broadcast a partner's real-time GPS update to the customer's order topic.
     * Location updates are WebSocket-only (too frequent for FCM push).
     */
    public void notifyLocationUpdate(String assignmentId, String orderId, String partnerId,
                                     BigDecimal lat, BigDecimal lng) {
        DeliveryNotification n = DeliveryNotification.locationUpdate(
                assignmentId, orderId, partnerId, lat, lng);
        sendToCustomerOrderTopic(orderId, n);
        broadcastToAdmin(n);
        // Note: location updates are NOT sent via FCM — too high frequency
    }

    // ── Customer: partner assigned ────────────────────────────────────────────

    /**
     * Tell the customer which partner has been assigned to their order.
     */
    public void notifyPartnerAssigned(DeliveryAssignment assignment, DeliveryPartner partner) {
        String msg = "A delivery partner has been assigned to your order #" + assignment.getOrderId();
        DeliveryNotification n = DeliveryNotification.of(
                assignment.getAssignmentId(), assignment.getOrderId(),
                partner.getPartnerId(), partner.getName(), partner.getPhone(),
                AssignmentStatus.ASSIGNED, NotificationType.PARTNER_ASSIGNED, msg);
        sendToCustomerOrderTopic(assignment.getOrderId(), n);
    }

    // ── Generic status broadcast ──────────────────────────────────────────────

    /**
     * Broadcast any pre-built notification to the admin topic.
     */
    public void broadcastStatusUpdate(DeliveryNotification notification) {
        broadcastToAdmin(notification);
    }

    // ── WebSocket transport helpers ───────────────────────────────────────────

    private void sendToPartner(String partnerId, DeliveryNotification notification) {
        log.info("WS → partner {} | {} | order {}",
                partnerId, notification.getType(), notification.getOrderId());
        try {
            messaging.convertAndSendToUser(partnerId, PARTNER_QUEUE, notification);
        } catch (Exception e) {
            log.error("WS send to partner {} failed: {}", partnerId, e.getMessage(), e);
        }
    }

    private void sendToCustomerOrderTopic(String orderId, DeliveryNotification notification) {
        String dest = String.format(CUSTOMER_ORDER_TOPIC, orderId);
        log.info("WS → customer topic {} | {}", dest, notification.getType());
        try {
            messaging.convertAndSend(dest, notification);
        } catch (Exception e) {
            log.error("WS send to topic {} failed: {}", dest, e.getMessage(), e);
        }
    }

    private void broadcastToAdmin(DeliveryNotification notification) {
        log.debug("WS → admin | {} | order {}", notification.getType(), notification.getOrderId());
        try {
            messaging.convertAndSend(ADMIN_TOPIC, notification);
        } catch (Exception e) {
            log.error("WS broadcast to admin failed: {}", e.getMessage(), e);
        }
    }

    // ── FCM data payload helper ───────────────────────────────────────────────

    /**
     * Build a flat string-keyed data map from a notification.
     * The mobile app reads these keys to route the push notification.
     */
    private Map<String, String> buildData(DeliveryNotification n) {
        return Map.of(
                "type",         n.getType() != null ? n.getType().name() : "",
                "assignmentId", n.getAssignmentId() != null ? n.getAssignmentId() : "",
                "orderId",      n.getOrderId() != null ? n.getOrderId() : "",
                "partnerId",    n.getPartnerId() != null ? n.getPartnerId() : "",
                "status",       n.getAssignmentStatus() != null ? n.getAssignmentStatus().name() : ""
        );
    }
}
