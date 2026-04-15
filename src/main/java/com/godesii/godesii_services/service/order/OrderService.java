package com.godesii.godesii_services.service.order;

import com.godesii.godesii_services.config.OrderConfig;
import com.godesii.godesii_services.dto.*;
import com.godesii.godesii_services.entity.order.Order;
import com.godesii.godesii_services.entity.order.OrderStatus;
import com.godesii.godesii_services.entity.payment.PaymentMethod;
import com.godesii.godesii_services.exception.*;
import com.godesii.godesii_services.repository.order.OrderRepository;
import com.godesii.godesii_services.service.delivery.DeliveryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository repo;
    private final CartService cartService;
    private final DeliveryService deliveryService;
    private final OrderNotificationService notificationService;

    public static final Logger log = LoggerFactory.getLogger(OrderService.class);

    public OrderService(OrderRepository repo, CartService cartService,
            DeliveryService deliveryService, OrderNotificationService notificationService) {
        this.repo = repo;
        this.cartService = cartService;
        this.deliveryService = deliveryService;
        this.notificationService = notificationService;
    }

    /**
     * Place order from cart.
     * <p>
     * For COD orders: order goes directly to PAYMENT_SUCCESS and the cart is cleared immediately.
     * For online payment methods: order is left at PENDING_PAYMENT for the frontend to redirect
     * the customer to the payment gateway independently.
     * </p>
     */
    @Transactional
    public PlaceOrderResponse placeOrder(PlaceOrderRequest request) {
        log.info("Placing order for user: {}", request.getUsername());

        // 1. Get active cart (will throw if not found)
        CartResponse cartResponse = cartService.getActiveCart(request.getUsername());
        Long cartId = cartResponse.getCartId();

        // 2 & 3. Lock cart and recalculate prices
        String tempOrderId = "PENDING-" + System.currentTimeMillis();
        cartService.lockCart(cartId, tempOrderId);

        try {
            // Get final prices (this will update cart total if prices changed)
            PriceBreakdown pricing = cartService.getFinalPriceBreakdown(cartId);

            // 4. Create order
            Order order = new Order();
            order.setUsername(request.getUsername());
            order.setRestaurantId(String.valueOf(cartResponse.getRestaurantId()));
            order.setOrderStatus(OrderStatus.CREATED);
            order.setOrderDate(Instant.now());
            order.setTotalAmount(pricing.getTotalAmount());

            // Payment method - convert string to enum
            PaymentMethod paymentMethod = PaymentMethod.fromString(request.getPaymentMethod());
            order.setPaymentMethod(paymentMethod);
            order.setPaymentStatus("PENDING");

            // Delivery notes from special instructions
            order.setDeliveryNotes(request.getSpecialInstructions());

            Order savedOrder = repo.save(order);

            // Update lock with real order ID
            cartService.updateLockOrderId(cartId, tempOrderId, savedOrder.getOrderId());

            // 5. Handle payment based on method
            if (paymentMethod == PaymentMethod.COD) {
                // Cash on Delivery — no gateway required, confirm immediately
                savedOrder.setPaymentStatus("PENDING_COD");
                savedOrder.setOrderStatus(OrderStatus.PAYMENT_SUCCESS);
                savedOrder = repo.save(savedOrder);

                // Clear cart and notify restaurant
                try {
                    cartService.clearCartByOrderId(savedOrder.getOrderId());
                } catch (Exception e) {
                    log.error("Failed to clear cart after COD order: {}", savedOrder.getOrderId(), e);
                }
                notificationService.notifyNewOrder(savedOrder);

                log.info("COD order placed successfully: {}", savedOrder.getOrderId());
                return new PlaceOrderResponse(
                        savedOrder.getOrderId(),
                        savedOrder.getOrderStatus(),
                        null,   // no payment ID for COD
                        null,   // no payment URL for COD
                        savedOrder.getTotalAmount(),
                        null,   // no payment expiry for COD
                        cartResponse);
            } else {
                // Online payment — set to PENDING_PAYMENT, frontend handles gateway redirect
                savedOrder.setOrderStatus(OrderStatus.PENDING_PAYMENT);
                savedOrder = repo.save(savedOrder);

                log.info("Online payment order created, awaiting payment: {}", savedOrder.getOrderId());
                return new PlaceOrderResponse(
                        savedOrder.getOrderId(),
                        savedOrder.getOrderStatus(),
                        null,   // payment ID assigned after gateway callback
                        null,   // payment URL provided by gateway separately
                        savedOrder.getTotalAmount(),
                        Instant.now().plus(OrderConfig.PAYMENT_TIMEOUT_MINUTES, ChronoUnit.MINUTES),
                        cartResponse);
            }

        } catch (Exception e) {
            // Unlock cart on failure
            cartService.unlockCart(cartId);
            throw e;
        }
    }

    /**
     * Handle payment success callback (for online payment methods).
     * Signature verification is skipped — validate signatures at the gateway/webhook level.
     */
    @Transactional
    public Order handlePaymentSuccess(PaymentCallbackRequest callback) {
        log.info("Processing payment success for order: {}", callback.getOrderId());

        Order order = getById(callback.getOrderId());

        if (order.getOrderStatus() != OrderStatus.PENDING_PAYMENT && order.getOrderStatus() != OrderStatus.CREATED) {
            log.warn("Payment success received for order in status: {}", order.getOrderStatus());
            return order; // Already processed
        }

        order.setPaymentStatus("SUCCESS");
        order.setPaymentId(callback.getPaymentId());
        order.setOrderStatus(OrderStatus.PAYMENT_SUCCESS);
        order.setOrderDate(Instant.now());

        Order saved = repo.save(order);

        // Clear cart after successful payment
        try {
            cartService.clearCartByOrderId(order.getOrderId());
            log.info("Cart cleared for order: {}", order.getOrderId());
        } catch (Exception e) {
            log.error("Failed to clear cart for order: {}", order.getOrderId(), e);
        }

        // Notify restaurant via WebSocket
        notificationService.notifyNewOrder(saved);

        return saved;
    }

    /**
     * Handle payment failure
     */
    @Transactional
    public Order handlePaymentFailure(PaymentCallbackRequest callback) {
        log.info("Processing payment failure for order: {}", callback.getOrderId());

        Order order = getById(callback.getOrderId());
        order.setPaymentStatus("FAILED");
        order.setOrderStatus(OrderStatus.PAYMENT_FAILED);
        order.setCancellationReason("Payment failed: " + callback.getFailureReason());

        Order saved = repo.save(order);

        // Unlock cart so user can retry
        try {
            // Find cart by order ID and unlock it
            cartService.clearCartByOrderId(order.getOrderId()); // Clear it since payment failed
            log.info("Cart cleared for failed payment order: {}", order.getOrderId());
        } catch (Exception e) {
            log.error("Failed to clear cart for order: {}", order.getOrderId(), e);
        }

        // Notify restaurant of payment failure
        notificationService.notifyPaymentFailed(saved);

        return saved;
    }

    /**
     * Confirm or reject order by restaurant
     */
    @Transactional
    public Order confirmOrderByRestaurant(String orderId, OrderConfirmationRequest request) {
        Order order = getById(orderId);

        if (order.getOrderStatus() != OrderStatus.PAYMENT_SUCCESS) {
            throw new InvalidOrderStateException("Order cannot be confirmed in current state",
                    orderId, order.getOrderStatus(), OrderStatus.CONFIRMED);
        }

        if ("ACCEPT".equalsIgnoreCase(request.getAction())) {
            order.setOrderStatus(OrderStatus.CONFIRMED);
            order.setConfirmedAt(Instant.now());

            // Store preparation time and calculate estimated delivery
            if (request.getEstimatedPreparationTime() != null) {
                order.setPreparationTime(request.getEstimatedPreparationTime());
                // Calculate estimated delivery: preparation time + 20 minutes for delivery
                order.setEstimatedDeliveryTime(
                        Instant.now().plusSeconds((request.getEstimatedPreparationTime() + 20) * 60L));
            }

            Order saved = repo.save(order);
            notificationService.notifyCustomerOrderConfirmed(saved);
            return saved;

        } else if ("REJECT".equalsIgnoreCase(request.getAction())) {
            order.setOrderStatus(OrderStatus.CANCELLED);
            order.setCancelledAt(Instant.now());
            order.setCancellationReason("Restaurant rejected: " + request.getRejectionReason());

            // Mark refund as issued for non-COD paid orders — actual refund handled externally
            if ("SUCCESS".equalsIgnoreCase(order.getPaymentStatus())
                    && order.getPaymentMethod() != PaymentMethod.COD) {
                log.warn("Refund required for rejected order: {} — process manually via payment gateway.",
                        order.getOrderId());
                order.setRefundIssued(true);
                order.setOrderStatus(OrderStatus.REFUNDED);
                order.setRefundedAt(Instant.now());
            }

            Order saved = repo.save(order);
            notificationService.notifyCustomerOrderRejected(saved, request.getRejectionReason());
            if (Boolean.TRUE.equals(saved.getRefundIssued())) {
                notificationService.notifyCustomerRefundInitiated(saved);
            }
            return saved;
        }

        return repo.save(order);
    }

    /**
     * Cancel order by user
     */
    @Transactional
    public Order cancelOrder(String orderId, String reason) {
        Order order = getById(orderId);

        if (!order.getOrderStatus().isCancellable()) {
            throw new InvalidOrderStateException("Order cannot be cancelled in current state",
                    orderId, order.getOrderStatus(), OrderStatus.CANCELLED);
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        order.setCancelledAt(Instant.now());
        order.setCancellationReason(reason);

        // For non-COD paid orders, log that manual refund is needed
        if ("SUCCESS".equalsIgnoreCase(order.getPaymentStatus())
                && order.getPaymentMethod() != PaymentMethod.COD) {
            log.warn("Refund required for cancelled order: {} — process manually via payment gateway.", orderId);
            order.setRefundIssued(true);
            order.setOrderStatus(OrderStatus.REFUNDED);
            order.setRefundedAt(Instant.now());
        }

        return repo.save(order);
    }

    /**
     * Process payment timeouts (Scheduled)
     */
    @Scheduled(fixedRate = 60000) // Every minute
    @Transactional
    public int processPaymentTimeouts() {
        if (!OrderConfig.AUTO_CANCEL_ON_PAYMENT_TIMEOUT)
            return 0;

        Instant cutoff = Instant.now().minus(OrderConfig.PAYMENT_TIMEOUT_MINUTES, ChronoUnit.MINUTES);
        List<Order> expiredOrders = repo.findByPaymentStatusAndOrderDateBefore("PENDING", cutoff);

        int count = 0;
        for (Order order : expiredOrders) {
            if (order.getOrderStatus() == OrderStatus.PENDING_PAYMENT
                    || order.getOrderStatus() == OrderStatus.CREATED) {
                order.setOrderStatus(OrderStatus.CANCELLED);
                order.setCancellationReason("Payment timeout");
                order.setCancelledAt(Instant.now());
                repo.save(order);
                count++;
            }
        }

        return count;
    }

    // ========== Basic CRUD ==========

    public Order getById(@NonNull String id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + id));
    }

    @Transactional
    public Order updateStatus(@NonNull String id, @NonNull String status) {
        Order existing = getById(id);
        try {
            OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
            // Validate transition
            existing.setOrderStatus(orderStatus);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid order status: " + status);
        }
        return repo.save(existing);
    }

    // ========== Order Status Progression Methods ==========

    /**
     * Mark order as preparing (restaurant started cooking)
     */
    @Transactional
    public Order markAsPreparing(@NonNull String orderId) {
        Order order = getById(orderId);
        validateStateTransition(order, OrderStatus.PREPARING);

        order.setOrderStatus(OrderStatus.PREPARING);
        log.info("Order {} marked as PREPARING", orderId);
        Order saved = repo.save(order);
        notificationService.notifyCustomerOrderPreparing(saved);
        return saved;
    }

    /**
     * Mark order as ready for pickup
     */
    @Transactional
    public Order markAsReadyForPickup(@NonNull String orderId) {
        Order order = getById(orderId);
        validateStateTransition(order, OrderStatus.READY_FOR_PICKUP);

        order.setOrderStatus(OrderStatus.READY_FOR_PICKUP);
        log.info("Order {} marked as READY_FOR_PICKUP", orderId);
        return repo.save(order);
    }

    /**
     * Mark order as out for delivery
     */
    @Transactional
    public Order markAsOutForDelivery(@NonNull String orderId) {
        Order order = getById(orderId);
        validateStateTransition(order, OrderStatus.OUT_FOR_DELIVERY);

        order.setOrderStatus(OrderStatus.OUT_FOR_DELIVERY);
        log.info("Order {} marked as OUT_FOR_DELIVERY", orderId);
        Order saved = repo.save(order);
        notificationService.notifyCustomerOrderOutForDelivery(saved);
        return saved;
    }

    /**
     * Mark order as delivered
     */
    @Transactional
    public Order markAsDelivered(@NonNull String orderId) {
        Order order = getById(orderId);
        validateStateTransition(order, OrderStatus.DELIVERED);

        order.setOrderStatus(OrderStatus.DELIVERED);
        log.info("Order {} marked as DELIVERED", orderId);
        Order saved = repo.save(order);
        notificationService.notifyCustomerOrderDelivered(saved);
        return saved;
    }

    /**
     * Retry payment for a failed order.
     * Switches payment method and resets status to PENDING_PAYMENT (online) or PAYMENT_SUCCESS (COD).
     */
    @Transactional
    public PlaceOrderResponse retryPayment(@NonNull String orderId, @NonNull String newPaymentMethod) {
        Order order = getById(orderId);

        if (order.getOrderStatus() != OrderStatus.PAYMENT_FAILED) {
            throw new InvalidOrderStateException("Can only retry payment for failed orders",
                    orderId, order.getOrderStatus(), OrderStatus.PENDING_PAYMENT);
        }

        PaymentMethod paymentMethod = PaymentMethod.fromString(newPaymentMethod);
        order.setPaymentMethod(paymentMethod);
        order.setCancellationReason(null);

        if (paymentMethod == PaymentMethod.COD) {
            order.setOrderStatus(OrderStatus.PAYMENT_SUCCESS);
            order.setPaymentStatus("PENDING_COD");
            Order updated = repo.save(order);
            notificationService.notifyNewOrder(updated);
            log.info("Order {} switched to COD successfully", orderId);
            return new PlaceOrderResponse(
                    updated.getOrderId(),
                    updated.getOrderStatus(),
                    null,
                    null,
                    updated.getTotalAmount(),
                    null,
                    null);
        } else {
            order.setOrderStatus(OrderStatus.PENDING_PAYMENT);
            order.setPaymentStatus("PENDING");
            Order updated = repo.save(order);
            log.info("Order {} reset to PENDING_PAYMENT for method: {}", orderId, newPaymentMethod);
            return new PlaceOrderResponse(
                    updated.getOrderId(),
                    updated.getOrderStatus(),
                    null,
                    null,
                    updated.getTotalAmount(),
                    Instant.now().plus(OrderConfig.PAYMENT_TIMEOUT_MINUTES, ChronoUnit.MINUTES),
                    null);
        }
    }

    /**
     * Update order details (for OrderController)
     */
    @Transactional
    public Order update(@NonNull String id, OrderRequest request) {
        Order existing = getById(id);

        // Update fields from request as needed
        // For now, just update status if provided
        if (request.getOrderStatus() != null) {
            OrderStatus newStatus = OrderStatus.valueOf(request.getOrderStatus());
            validateStateTransition(existing, newStatus);
            existing.setOrderStatus(newStatus);
        }

        return repo.save(existing);
    }

    /**
     * Validate order status transition
     */
    private void validateStateTransition(Order order, OrderStatus targetStatus) {
        if (!order.getOrderStatus().canTransitionTo(targetStatus)) {
            throw new InvalidStateTransitionException(
                    "Invalid status transition",
                    order.getOrderId(),
                    order.getOrderStatus(),
                    targetStatus);
        }
    }

    // ========== Cancellation Logic ==========

    /**
     * User cancels order (only before PREPARING)
     */
    @Transactional
    public Order cancelOrderByUser(String orderId, String reason) {
        Order order = getById(orderId);

        // Validate: Only before PREPARING
        if (order.getOrderStatus().ordinal() >= OrderStatus.PREPARING.ordinal()) {
            throw new CancellationNotAllowedException(
                    "Cannot cancel order after preparation started",
                    orderId,
                    order.getOrderStatus());
        }

        order.setCancelledBy("USER");
        order.setCancellationCategory(com.godesii.godesii_services.entity.order.CancellationReason.USER_CHANGED_MIND);
        order.setCancellationReason(reason);
        order.setOrderStatus(OrderStatus.CANCELLED);
        order.setCancelledAt(Instant.now());

        // For non-COD paid orders, flag for manual refund processing
        if ("SUCCESS".equalsIgnoreCase(order.getPaymentStatus())
                && order.getPaymentMethod() != PaymentMethod.COD) {
            log.warn("Refund required for user-cancelled order: {} — process manually via payment gateway.", orderId);
            order.setRefundIssued(true);
            order.setOrderStatus(OrderStatus.REFUNDED);
            order.setRefundedAt(Instant.now());
        }

        log.info("User cancelled order: {}, Reason: {}", orderId, reason);
        Order saved = repo.save(order);
        notificationService.notifyOrderCancelled(saved, reason);
        if (saved.getRefundIssued() != null && saved.getRefundIssued()) {
            notificationService.notifyCustomerRefundInitiated(saved);
        }
        return saved;
    }

    /**
     * Restaurant cancels order (stock issues, etc.)
     */
    @Transactional
    public Order cancelOrderByRestaurant(String orderId,
            com.godesii.godesii_services.entity.order.CancellationReason reason,
            String details) {
        Order order = getById(orderId);

        // Can cancel up to READY_FOR_PICKUP
        if (order.getOrderStatus() == OrderStatus.OUT_FOR_DELIVERY ||
                order.getOrderStatus() == OrderStatus.DELIVERED) {
            throw new CancellationNotAllowedException(
                    "Cannot cancel order after dispatch",
                    orderId,
                    order.getOrderStatus());
        }

        order.setCancelledBy("RESTAURANT");
        order.setCancellationCategory(reason);
        order.setCancellationReason(details);
        order.setOrderStatus(OrderStatus.CANCELLED);
        order.setCancelledAt(Instant.now());

        // For non-COD paid orders, flag for manual refund processing
        if ("SUCCESS".equalsIgnoreCase(order.getPaymentStatus())
                && order.getPaymentMethod() != PaymentMethod.COD) {
            log.warn("Refund required for restaurant-cancelled order: {} — process manually via payment gateway.",
                    orderId);
            order.setRefundIssued(true);
            order.setOrderStatus(OrderStatus.REFUNDED);
            order.setRefundedAt(Instant.now());
            // TODO: Add compensation voucher for customer inconvenience
        }

        log.warn("Restaurant cancelled order: {}, Reason: {}", orderId, reason);
        Order saved = repo.save(order);
        notificationService.notifyOrderCancelled(saved, details);
        return saved;
    }

    // ========== CRUD Methods ==========

    // Other CRUD methods...
    public Page<Order> getAll(Pageable pageable) {
        return repo.findAll(pageable);
    }

    @Transactional
    public void delete(@NonNull String id) {
        repo.deleteById(id);
    }
}
