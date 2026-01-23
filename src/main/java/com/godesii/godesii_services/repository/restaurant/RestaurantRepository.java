package com.godesii.godesii_services.repository.restaurant;

import com.godesii.godesii_services.entity.restaurant.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    /**
     * Find restaurant by ID if it's active
     * 
     * @param id Restaurant ID
     * @return Optional containing Restaurant if exists and active
     */
    Optional<Restaurant> findByIdAndIsActiveTrue(Long id);
}
