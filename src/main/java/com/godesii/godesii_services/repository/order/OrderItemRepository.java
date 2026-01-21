package com.godesii.godesii_services.repository.order;

import com.godesii.godesii_services.entity.order.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, String> {

    /**
     * Find all order items by product ID
     */
    List<OrderItem> findByProductId(String productId);
}
