package com.godesii.godesii_services.repository.restaurant;

import com.godesii.godesii_services.entity.restaurant.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepo extends JpaRepository<MenuItem, String> {
    
    /**
     * Find all menu items for a specific category
     */
    List<MenuItem> findByCategoryId(Long categoryId);
}
