package com.godesii.godesii_services.repository.restaurant;

import com.godesii.godesii_services.common.FoodCategory;
import com.godesii.godesii_services.entity.restaurant.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RestaurantRepo extends JpaRepository<Restaurant, Long>{

    Page<Restaurant> findAllByFoodCategory(FoodCategory foodType, Pageable pageable);

    Page<Restaurant> findAllByCreatedBy(String createdBy, Pageable pageable);

    long countByCreatedBy(String createdBy);

    Optional<Restaurant> findByIdAndIsActiveTrue(Long id);


}
