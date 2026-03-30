package com.godesii.godesii_services.service.order;

import com.godesii.godesii_services.dto.OrderNotification;
import com.godesii.godesii_services.dto.OrderNotification.NotificationType;
import com.godesii.godesii_services.entity.order.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

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

    private final SimpMessagingTemplate messagingTemplate;

    public OrderNotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
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
        OrderNotification notification = OrderNotification.from(
                order,
                NotificationType.ORDER_CONFIRMED,
                "Your order #" + order.getOrderId() + " has been confirmed!");

        sendToCustomer(order.getUsername(), notification);
    }

    /**
     * Notify the customer that their order has been rejected by the restaurant.
     */
    public void notifyCustomerOrderRejected(Order order, String reason) {
        OrderNotification notification = OrderNotification.from(
                order,
                NotificationType.ORDER_REJECTED,
                "Your order #" + order.getOrderId() + " was declined: " + reason);

        sendToCustomer(order.getUsername(), notification);
    }

    /**
     * Notify the customer that their order is being prepared.
     */
    public void notifyCustomerOrderPreparing(Order order) {
        OrderNotification notification = OrderNotification.from(
                order,
                NotificationType.ORDER_PREPARING,
                "Your order #" + order.getOrderId() + " is being prepared!");

        sendToCustomer(order.getUsername(), notification);
    }

    /**
     * Notify the customer that their order is out for delivery.
     */
    public void notifyCustomerOrderOutForDelivery(Order order) {
        OrderNotification notification = OrderNotification.from(
                order,
                NotificationType.ORDER_OUT_FOR_DELIVERY,
                "Your order #" + order.getOrderId() + " is on the way!");

        sendToCustomer(order.getUsername(), notification);
    }

    /**
     * Notify the customer that their order has been delivered.
     */
    public void notifyCustomerOrderDelivered(Order order) {
        OrderNotification notification = OrderNotification.from(
                order,
                NotificationType.ORDER_DELIVERED,
                "Your order #" + order.getOrderId() + " has been delivered. Enjoy your meal!");

        sendToCustomer(order.getUsername(), notification);
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
