package com.godesii.godesii_services.repository.order;

import com.godesii.godesii_services.entity.order.Order;
import com.godesii.godesii_services.entity.order.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

    List<Order> findByUsername(String username);

    List<Order> findByOrderStatus(OrderStatus orderStatus);

    List<Order> findByPaymentStatusAndOrderDateBefore(String paymentStatus, Instant cutoffTime);

    List<Order> findByOrderStatusAndConfirmedAtBefore(OrderStatus status, Instant cutoffTime);

    // User order history
    List<Order> findByUsernameOrderByOrderDateDesc(String username);

    // Restaurant orders
    List<Order> findByRestaurantIdAndOrderStatus(String restaurantId, OrderStatus status);

    org.springframework.data.domain.Page<Order> findByRestaurantId(String restaurantId,
            org.springframework.data.domain.Pageable pageable);

    List<Order> findByRestaurantIdAndOrderStatusIn(String restaurantId, List<OrderStatus> statuses);

    // Find by payment ID (for webhooks)
    java.util.Optional<Order> findByPaymentId(String paymentId);

    // Find by Razorpay order ID
    java.util.Optional<Order> findByRazorpayOrderId(String razorpayOrderId);
}
