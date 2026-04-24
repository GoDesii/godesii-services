package com.godesii.godesii_services.repository.restaurant;

import com.godesii.godesii_services.entity.restaurant.RestaurantCommission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RestaurantCommissionRepository extends JpaRepository<RestaurantCommission, Long> {

    /**
     * Find the active commission config for a given restaurant.
     * Used during analytics calculation to determine the platform's cut.
     */
    Optional<RestaurantCommission> findByRestaurantIdAndIsActiveTrue(Long restaurantId);
}
