package com.godesii.godesii_services.repository.order;

import com.godesii.godesii_services.entity.order.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, String> {

    Optional<Cart> findByUserId(Long userId);

    /**
     * Find active (non-expired) cart for a user
     * 
     * @param userId User ID
     * @param now    Current timestamp
     * @return Optional containing active Cart if exists
     */
    Optional<Cart> findByUserIdAndExpiresAtAfter(Long userId, Instant now);

    /**
     * Find all expired carts
     * 
     * @param now Current timestamp
     * @return List of expired carts
     */
    List<Cart> findByExpiresAtBefore(Instant now);

    /**
     * Find active and unlocked cart
     */
    Optional<Cart> findByUserIdAndIsLockedFalseAndExpiresAtAfter(Long userId, Instant now);

    /**
     * Find locked cart by order ID
     */
    Optional<Cart> findByLockedForOrderId(String orderId);

    /**
     * Find stale locked carts for unlocking
     */
    List<Cart> findByIsLockedTrueAndLockedAtBefore(Instant timestamp);
}
