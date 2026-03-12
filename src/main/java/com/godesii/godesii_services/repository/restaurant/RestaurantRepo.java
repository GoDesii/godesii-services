package com.godesii.godesii_services.repository.restaurant;

import com.godesii.godesii_services.common.FoodCategory;
import com.godesii.godesii_services.entity.restaurant.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepo extends JpaRepository<Restaurant, Long>{

    Page<Restaurant> findAllByFoodCategory(FoodCategory foodType, Pageable pageable);

}
