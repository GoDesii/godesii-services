package com.godesii.godesii_services.repository.order;

import com.godesii.godesii_services.entity.order.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, String> {

    /**
     * Find all cart items by product ID
     */
    List<CartItem> findByProductId(String productId);
}
