package com.godesii.godesii_services.service.order;

import com.godesii.godesii_services.config.OrderConfig;
import com.godesii.godesii_services.dto.*;
import com.godesii.godesii_services.entity.order.Order;
import com.godesii.godesii_services.entity.order.OrderStatus;
import com.godesii.godesii_services.entity.payment.PaymentMethod;
import com.godesii.godesii_services.exception.*;
import com.godesii.godesii_services.repository.order.OrderRepository;
import com.godesii.godesii_services.service.delivery.DeliveryService;
import com.godesii.godesii_services.service.payment.PaymentService;
import com.godesii.godesii_services.service.payment.PaymentResponse;
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
    private final PaymentService paymentService;
    private final DeliveryService deliveryService;

    public static final Logger log = LoggerFactory.getLogger(OrderService.class);

    public OrderService(OrderRepository repo, CartService cartService, PaymentService paymentService,
            DeliveryService deliveryService) {
        this.repo = repo;
        this.cartService = cartService;
        this.paymentService = paymentService;
        this.deliveryService = deliveryService;
    }

    /**
     * Place order from cart
     * 1. Fetch active cart
     * 2. Lock cart
     * 3. Recalculate prices
     * 4. Create order
     * 5. Initiate payment
     */
    @Transactional
    public PlaceOrderResponse placeOrder(PlaceOrderRequest request) {
        log.info("Placing order for user: {}", request.getUsername());

        // 1. Get active cart (will throw if not found)
        CartResponse cartResponse = cartService.getActiveCart(request.getUsername());
        String cartId = cartResponse.getCartId();

        // 2 & 3. Lock cart and recalculate prices
        // Since we need to lock first to prevent race conditions
        String tempOrderId = "PENDING-" + System.currentTimeMillis(); // Temp ID for locking
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

            // Map items from cart response
            // Note: In real app, we should fetch Cart entity to get full details
            // For now, assume mapping is handled or we use CartResponse items
            // Ideally we should use Cart entity directly here via a method in CartService
            // that returns entity
            // But let's stick to using what we have.
            // We need to map cart items to order items.
            // This part requires access to Cart entity or replicating logic.
            // Let's rely on updateEntity logic or creating items.
            // Simplified: We need OrderItem logic.
            // Let's save order first to get ID.
            Order savedOrder = repo.save(order);

            // Update lock with real order ID
            cartService.lockCart(cartId, savedOrder.getOrderId());

            // 5. Initiate Payment
            PaymentResponse paymentResponse = paymentService.initiatePayment(savedOrder, request.getPaymentMethod());

            if (paymentResponse.isSuccess()) {
                savedOrder.setPaymentId(paymentResponse.getPaymentId());
                savedOrder.setOrderStatus(OrderStatus.PENDING_PAYMENT);
                savedOrder = repo.save(savedOrder);

                return new PlaceOrderResponse(
                        savedOrder.getOrderId(),
                        savedOrder.getOrderStatus(),
                        paymentResponse.getPaymentId(),
                        paymentResponse.getPaymentUrl(),
                        savedOrder.getTotalAmount(),
                        Instant.now().plus(OrderConfig.PAYMENT_TIMEOUT_MINUTES, ChronoUnit.MINUTES),
                        cartResponse);
            } else {
                throw new PaymentFailedException("Payment initiation failed", null, paymentResponse.getErrorMessage());
            }

        } catch (Exception e) {
            // Unlock cart on failure
            cartService.unlockCart(cartId);
            throw e;
        }
    }

    /**
     * Handle payment success callback
     */
    @Transactional
    public Order handlePaymentSuccess(PaymentCallbackRequest callback) {
        log.info("Processing payment success for order: {}", callback.getOrderId());

        Order order = getById(callback.getOrderId());

        // Verify payment signature
        boolean isValid = paymentService.verifyPaymentSignature(
                callback.getRazorpayOrderId() != null ? callback.getRazorpayOrderId() : order.getRazorpayOrderId(),
                callback.getPaymentId(),
                callback.getSignature());

        if (!isValid) {
            log.error("Invalid payment signature for order: {}", callback.getOrderId());
            throw new PaymentFailedException("Invalid payment signature",
                    callback.getPaymentId(), "Signature verification failed");
        }

        if (order.getOrderStatus() != OrderStatus.PENDING_PAYMENT && order.getOrderStatus() != OrderStatus.CREATED) {
            log.warn("Payment success received for order in status: {}", order.getOrderStatus());
            return order; // Already processed
        }

        order.setPaymentStatus("SUCCESS");
        order.setPaymentId(callback.getPaymentId());
        order.setOrderStatus(OrderStatus.PAYMENT_SUCCESS);
        order.setOrderDate(Instant.now()); // Update time to payment time

        Order saved = repo.save(order);

        // Clear cart after successful order
        try {
            cartService.clearCartByOrderId(order.getOrderId());
            log.info("Cart cleared for order: {}", order.getOrderId());
        } catch (Exception e) {
            log.error("Failed to clear cart for order: {}", order.getOrderId(), e);
        }

        // Notify restaurant (TODO: implement notification service)
        log.info("Order {} payment successful, restaurant should be notified", order.getOrderId());

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
        } else if ("REJECT".equalsIgnoreCase(request.getAction())) {
            order.setOrderStatus(OrderStatus.CANCELLED);
            order.setCancelledAt(Instant.now());
            order.setCancellationReason("Restaurant rejected: " + request.getRejectionReason());

            // Initiate Refund
            if ("SUCCESS".equalsIgnoreCase(order.getPaymentStatus())) {
                String refundId = paymentService.initiateRefund(order, "Restaurant rejected order");
                order.setRefundId(refundId);
                order.setOrderStatus(OrderStatus.REFUNDED);
                order.setRefundedAt(Instant.now());
            }
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

        // Process refund if paid
        if ("SUCCESS".equalsIgnoreCase(order.getPaymentStatus())) {
            try {
                String refundId = paymentService.initiateRefund(order, reason);
                order.setRefundId(refundId);
                order.setOrderStatus(OrderStatus.REFUNDED);
                order.setRefundedAt(Instant.now());
            } catch (Exception e) {
                log.error("Failed to initiate refund for order: {}", orderId, e);
                // Keep as CANCELLED but log error, admin intervention needed
            }
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
        return repo.save(order);
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
        return repo.save(order);
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
        return repo.save(order);
    }

    /**
     * Retry payment for failed order
     */
    @Transactional
    public PlaceOrderResponse retryPayment(@NonNull String orderId, @NonNull String newPaymentMethod) {
        Order order = getById(orderId);

        if (order.getOrderStatus() != OrderStatus.PAYMENT_FAILED) {
            throw new InvalidOrderStateException("Can only retry payment for failed orders",
                    orderId, order.getOrderStatus(), OrderStatus.PENDING_PAYMENT);
        }

        // Update payment method
        PaymentMethod paymentMethod = PaymentMethod.fromString(newPaymentMethod);
        order.setPaymentMethod(paymentMethod);
        order.setOrderStatus(OrderStatus.PENDING_PAYMENT);
        order.setPaymentStatus("PENDING");
        order.setCancellationReason(null);

        Order updated = repo.save(order);

        // Initiate new payment
        PaymentResponse paymentResponse = paymentService.initiatePayment(updated, newPaymentMethod);

        if (paymentResponse.isSuccess()) {
            updated.setPaymentId(paymentResponse.getPaymentId());
            updated = repo.save(updated);

            return new PlaceOrderResponse(
                    updated.getOrderId(),
                    updated.getOrderStatus(),
                    paymentResponse.getPaymentId(),
                    paymentResponse.getPaymentUrl(),
                    updated.getTotalAmount(),
                    Instant.now().plus(OrderConfig.PAYMENT_TIMEOUT_MINUTES, ChronoUnit.MINUTES),
                    null); // No cart response for retry
        } else {
            throw new PaymentFailedException("Payment retry failed", null, paymentResponse.getErrorMessage());
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

        // Full refund for early cancellation
        if ("SUCCESS".equalsIgnoreCase(order.getPaymentStatus())) {
            String refundId = paymentService.initiateRefund(order, reason);
            order.setRefundId(refundId);
            order.setOrderStatus(OrderStatus.REFUNDED);
            order.setRefundedAt(Instant.now());
            order.setRefundIssued(true);
        }

        log.info("User cancelled order: {}, Reason: {}", orderId, reason);
        return repo.save(order);
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

        // Full refund + potential compensation
        if ("SUCCESS".equalsIgnoreCase(order.getPaymentStatus())) {
            String refundId = paymentService.initiateRefund(order, "Restaurant cancelled: " + details);
            order.setRefundId(refundId);
            order.setOrderStatus(OrderStatus.REFUNDED);
            order.setRefundedAt(Instant.now());
            order.setRefundIssued(true);
            // TODO: Add compensation voucher for customer inconvenience
        }

        log.warn("Restaurant cancelled order: {}, Reason: {}", orderId, reason);
        return repo.save(order);
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
