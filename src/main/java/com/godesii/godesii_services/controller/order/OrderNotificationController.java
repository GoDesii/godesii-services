package com.godesii.godesii_services.controller.order;

import com.godesii.godesii_services.dto.OrderConfirmationRequest;
import com.godesii.godesii_services.entity.order.Order;
import com.godesii.godesii_services.service.order.OrderNotificationService;
import com.godesii.godesii_services.service.order.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

/**
 * WebSocket (STOMP) controller for bidirectional order notifications.
 *
 * <h3>Restaurant subscribes to:</h3>
 * <pre>/topic/restaurant/{restaurantId}/orders</pre>
 *
 * <h3>Customer subscribes to:</h3>
 * <pre>/user/queue/orders</pre>
 *
 * <h3>Restaurant sends actions to:</h3>
 * <pre>
 *   /app/order/{orderId}/status    → fetch & broadcast current status
 *   /app/order/{orderId}/accept    → accept order with prep time
 *   /app/order/{orderId}/reject    → reject order with reason
 *   /app/order/{orderId}/preparing → mark as preparing
 *   /app/order/{orderId}/ready     → mark as ready for pickup
 * </pre>
 */
@Controller
public class OrderNotificationController {

    private static final Logger log = LoggerFactory.getLogger(OrderNotificationController.class);

    private final OrderService orderService;
    private final OrderNotificationService notificationService;

    public OrderNotificationController(OrderService orderService,
                                       OrderNotificationService notificationService) {
        this.orderService = orderService;
        this.notificationService = notificationService;
    }

    // ==================== Status Query ====================

    /**
     * Client sends: /app/order/{orderId}/status
     * Broadcasts current order status to the restaurant topic.
     */
    @MessageMapping("/order/{orderId}/status")
    public void handleOrderStatusRequest(@DestinationVariable String orderId) {
        log.info("WebSocket status request received for order: {}", orderId);

        Order order = orderService.getById(orderId);
        notificationService.notifyOrderStatusUpdate(order);
    }

    // ==================== Restaurant Actions ====================

    /**
     * Restaurant accepts order via WebSocket.
     * Client sends: /app/order/{orderId}/accept
     * Payload: { "estimatedPreparationTime": 25 }
     *
     * Notifies customer: ORDER_CONFIRMED
     */
    @MessageMapping("/order/{orderId}/accept")
    public void handleOrderAccept(@DestinationVariable String orderId,
                                  @Payload OrderConfirmationRequest request) {
        log.info("WebSocket: Restaurant accepting order {}", orderId);

        request.setAction("ACCEPT");
        Order updated = orderService.confirmOrderByRestaurant(orderId, request);

        log.info("Order {} accepted via WebSocket, status: {}", orderId, updated.getOrderStatus());
    }

    /**
     * Restaurant rejects order via WebSocket.
     * Client sends: /app/order/{orderId}/reject
     * Payload: { "rejectionReason": "Out of ingredients" }
     *
     * Notifies customer: ORDER_REJECTED + REFUND_INITIATED
     */
    @MessageMapping("/order/{orderId}/reject")
    public void handleOrderReject(@DestinationVariable String orderId,
                                  @Payload OrderConfirmationRequest request) {
        log.info("WebSocket: Restaurant rejecting order {}", orderId);

        request.setAction("REJECT");
        Order updated = orderService.confirmOrderByRestaurant(orderId, request);

        log.info("Order {} rejected via WebSocket, status: {}", orderId, updated.getOrderStatus());
    }

    /**
     * Restaurant marks order as preparing via WebSocket.
     * Client sends: /app/order/{orderId}/preparing
     *
     * Notifies customer: ORDER_PREPARING
     */
    @MessageMapping("/order/{orderId}/preparing")
    public void handleOrderPreparing(@DestinationVariable String orderId) {
        log.info("WebSocket: Restaurant marking order {} as PREPARING", orderId);

        Order updated = orderService.markAsPreparing(orderId);

        log.info("Order {} marked as PREPARING via WebSocket", orderId);
    }

    /**
     * Restaurant marks order as ready for pickup via WebSocket.
     * Client sends: /app/order/{orderId}/ready
     *
     * Notifies customer: ORDER_STATUS_UPDATED (ready for pickup)
     */
    @MessageMapping("/order/{orderId}/ready")
    public void handleOrderReady(@DestinationVariable String orderId) {
        log.info("WebSocket: Restaurant marking order {} as READY_FOR_PICKUP", orderId);

        Order updated = orderService.markAsReadyForPickup(orderId);

        log.info("Order {} marked as READY_FOR_PICKUP via WebSocket", orderId);
    }

    // ==================== Subscription Events ====================

    /**
     * Triggered when a restaurant first subscribes to their order feed.
     */
    @SubscribeMapping("/restaurant/{restaurantId}/orders")
    public void onRestaurantSubscribe(@DestinationVariable String restaurantId) {
        log.info("Restaurant {} subscribed to live order notifications", restaurantId);
    }
}
