package com.godesii.godesii_services.service.order;

import com.godesii.godesii_services.dto.OrderNotification;
import com.godesii.godesii_services.dto.OrderNotification.NotificationType;
import com.godesii.godesii_services.entity.order.Order;
import com.godesii.godesii_services.service.FcmService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Service that pushes real-time order notifications to both restaurant websites
 * and customer apps via WebSocket (STOMP).
 *
 * <p>Restaurant subscribes to:
 * <pre>/topic/restaurant/{restaurantId}/orders</pre>
 *
 * <p>Customer subscribes to:
 * <pre>/user/queue/orders</pre>
 * (Spring resolves this per-user via the user destination prefix)
 */
@Service
public class OrderNotificationService {

    private static final Logger log = LoggerFactory.getLogger(OrderNotificationService.class);
    private static final String RESTAURANT_TOPIC = "/topic/restaurant/%s/orders";
    private static final String CUSTOMER_QUEUE = "/queue/orders";

    /** FCM topic prefix for restaurant devices subscribed at login. */
    private static final String FCM_RESTAURANT_TOPIC_PREFIX = "restaurant-";

    private final SimpMessagingTemplate messagingTemplate;
    private final FcmService fcmService;

    public OrderNotificationService(SimpMessagingTemplate messagingTemplate,
                                    FcmService fcmService) {
        this.messagingTemplate = messagingTemplate;
        this.fcmService        = fcmService;
    }

    // ==================== Restaurant Notifications ====================

    /**
     * Notify the restaurant that a new order has been placed and paid.
     */
    public void notifyNewOrder(Order order) {
        OrderNotification notification = OrderNotification.from(
                order,
                NotificationType.NEW_ORDER,
                "New order received! Order #" + order.getOrderId());

        sendToRestaurant(order.getRestaurantId(), notification);
    }

    /**
     * Notify the restaurant that an order has been cancelled.
     */
    public void notifyOrderCancelled(Order order, String reason) {
        OrderNotification notification = OrderNotification.from(
                order,
                NotificationType.ORDER_CANCELLED,
                "Order #" + order.getOrderId() + " cancelled: " + reason);

        sendToRestaurant(order.getRestaurantId(), notification);
    }

    /**
     * Notify the restaurant of a generic order status change.
     */
    public void notifyOrderStatusUpdate(Order order) {
        OrderNotification notification = OrderNotification.from(
                order,
                NotificationType.ORDER_STATUS_UPDATED,
                "Order #" + order.getOrderId() + " status → " + order.getOrderStatus());

        sendToRestaurant(order.getRestaurantId(), notification);
    }

    /**
     * Notify the restaurant that a payment failed for an order.
     */
    public void notifyPaymentFailed(Order order) {
        OrderNotification notification = OrderNotification.from(
                order,
                NotificationType.PAYMENT_FAILED,
                "Payment failed for order #" + order.getOrderId());

        sendToRestaurant(order.getRestaurantId(), notification);
    }

    // ==================== Customer Notifications ====================

    /**
     * Notify the customer that their order has been confirmed by the restaurant.
     */
    public void notifyCustomerOrderConfirmed(Order order) {
        String msg = "Your order #" + order.getOrderId() + " has been confirmed! 🎉";
        OrderNotification notification = OrderNotification.from(
                order, NotificationType.ORDER_CONFIRMED, msg);

        sendToCustomer(order.getUsername(), notification);

        // FCM push — customer may have closed the app
        fcmService.sendToUser(order.getUsername(), "✅ Order Confirmed", msg,
                Map.of("orderId", order.getOrderId(), "type", "ORDER_CONFIRMED"));
    }

    /**
     * Notify the customer that their order has been rejected by the restaurant.
     */
    public void notifyCustomerOrderRejected(Order order, String reason) {
        String msg = "Your order #" + order.getOrderId() + " was declined: " + reason;
        OrderNotification notification = OrderNotification.from(
                order, NotificationType.ORDER_REJECTED, msg);

        sendToCustomer(order.getUsername(), notification);

        fcmService.sendToUser(order.getUsername(), "❌ Order Declined", msg,
                Map.of("orderId", order.getOrderId(), "type", "ORDER_REJECTED"));
    }

    /**
     * Notify the customer that their order is being prepared.
     */
    public void notifyCustomerOrderPreparing(Order order) {
        String msg = "Your order #" + order.getOrderId() + " is being prepared! 👨‍🍳";
        OrderNotification notification = OrderNotification.from(
                order, NotificationType.ORDER_PREPARING, msg);

        sendToCustomer(order.getUsername(), notification);

        fcmService.sendToUser(order.getUsername(), "👨‍🍳 Preparing Your Order", msg,
                Map.of("orderId", order.getOrderId(), "type", "ORDER_PREPARING"));
    }

    /**
     * Notify the customer that their order is out for delivery.
     */
    public void notifyCustomerOrderOutForDelivery(Order order) {
        String msg = "Your order #" + order.getOrderId() + " is on the way! 🛵";
        OrderNotification notification = OrderNotification.from(
                order, NotificationType.ORDER_OUT_FOR_DELIVERY, msg);

        sendToCustomer(order.getUsername(), notification);

        fcmService.sendToUser(order.getUsername(), "🛵 Order Out for Delivery", msg,
                Map.of("orderId", order.getOrderId(), "type", "ORDER_OUT_FOR_DELIVERY"));
    }

    /**
     * Notify the customer that their order has been delivered.
     */
    public void notifyCustomerOrderDelivered(Order order) {
        String msg = "Your order #" + order.getOrderId() + " has been delivered. Enjoy your meal! 😋";
        OrderNotification notification = OrderNotification.from(
                order, NotificationType.ORDER_DELIVERED, msg);

        sendToCustomer(order.getUsername(), notification);

        fcmService.sendToUser(order.getUsername(), "✅ Order Delivered", msg,
                Map.of("orderId", order.getOrderId(), "type", "ORDER_DELIVERED"));
    }

    /**
     * Notify the customer that a refund has been initiated.
     */
    public void notifyCustomerRefundInitiated(Order order) {
        OrderNotification notification = OrderNotification.from(
                order,
                NotificationType.REFUND_INITIATED,
                "Refund initiated for order #" + order.getOrderId() + ". It will be processed shortly.");

        sendToCustomer(order.getUsername(), notification);
    }

    // ==================== Transport ====================

    /**
     * Send a notification to the restaurant-specific STOMP topic.
     */
    private void sendToRestaurant(String restaurantId, OrderNotification notification) {
        String destination = String.format(RESTAURANT_TOPIC, restaurantId);
        log.info("Sending {} notification to {} for order {}",
                notification.getType(), destination, notification.getOrderId());

        try {
            messagingTemplate.convertAndSend(destination, notification);
        } catch (Exception e) {
            log.error("Failed to send WebSocket notification to {}: {}", destination, e.getMessage(), e);
        }
    }

    /**
     * Send a notification to a specific customer via their user queue.
     * Customer subscribes to: /user/queue/orders
     */
    private void sendToCustomer(String username, OrderNotification notification) {
        log.info("Sending {} notification to customer '{}' for order {}",
                notification.getType(), username, notification.getOrderId());

        try {
            messagingTemplate.convertAndSendToUser(username, CUSTOMER_QUEUE, notification);
        } catch (Exception e) {
            log.error("Failed to send WebSocket notification to customer '{}': {}", username, e.getMessage(), e);
        }
    }
}
