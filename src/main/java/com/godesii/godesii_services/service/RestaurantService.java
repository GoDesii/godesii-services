package com.godesii.godesii_services.service;

import com.godesii.godesii_services.entity.restaurant.Restaurant;
import com.godesii.godesii_services.repository.restaurant.RestaurantRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RestaurantService {

    private final RestaurantRepo repo;

    public RestaurantService(RestaurantRepo repo) {
        this.repo = repo;
    }

    public Restaurant create(Restaurant restaurant) {
        return repo.save(restaurant);
    }

    public Page<Restaurant> getAll(Pageable pageable) {
        return repo.findAll(pageable);
    }

    public Restaurant getById(Long id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
    }

    public Restaurant update(Long id, Restaurant restaurant) {
        Restaurant existing = getById(id);

        if (restaurant != null) {
            Optional.ofNullable(restaurant.getRestaurantName()).ifPresent(existing::setRestaurantName);
            Optional.ofNullable(restaurant.getAddress()).ifPresent(existing::setAddress);
            Optional.ofNullable(restaurant.getLatitude()).ifPresent(existing::setLatitude);
            Optional.ofNullable(restaurant.getLongitude()).ifPresent(existing::setLongitude);
            Optional.ofNullable(restaurant.getOpeningHours()).ifPresent(existing::setOpeningHours);
            Optional.ofNullable(restaurant.getClosingHours()).ifPresent(existing::setClosingHours);
            Optional.ofNullable(restaurant.getDescription()).ifPresent(existing::setDescription);
            Optional.ofNullable(restaurant.getMealType()).ifPresent(existing::setMealType);

            // boolean should be handled separately since primitive can't be null
            existing.setVerified(restaurant.isVerified());
        }

        return repo.save(existing);
    }


    public void delete(Long id) {
        repo.deleteById(id);
    }
}

