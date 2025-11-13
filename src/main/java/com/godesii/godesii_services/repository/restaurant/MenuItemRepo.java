package com.godesii.godesii_services.repository.restaurant;

import com.godesii.godesii_services.entity.restaurant.MenuItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuItemRepo extends JpaRepository<MenuItem, Long> {
    Page<MenuItem> findByRestaurantId(Long restaurantId, Pageable pageable);

    // Find menus by menuName
    List<MenuItem> findByMenuName(String menuName);

    // Find menus by restaurant name
    List<MenuItem> findByRestaurant_RestaurantName(String restaurantName);

    // Find menus by both restaurant and menu
    List<MenuItem> findByRestaurant_RestaurantNameAndMenuName(String restaurantName, String menuName);
}
