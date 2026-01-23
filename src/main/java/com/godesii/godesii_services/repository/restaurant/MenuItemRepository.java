package com.godesii.godesii_services.repository.restaurant;

import com.godesii.godesii_services.entity.restaurant.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, String> {

    /**
     * Find menu item by ID if it's available
     * 
     * @param itemId Menu item ID
     * @return Optional containing MenuItem if exists and available
     */
    Optional<MenuItem> findByItemIdAndIsAvailableTrue(String itemId);
}
