package com.godesii.godesii_services.service.restaurant;

import com.godesii.godesii_services.entity.restaurant.Restaurant;
import com.godesii.godesii_services.exception.ResourceNotFoundException;
import com.godesii.godesii_services.repository.restaurant.RestaurantRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class RestaurantService {

    private final RestaurantRepo repo;

    public RestaurantService(RestaurantRepo repo) {
        this.repo = repo;
    }

    //CREATE
    public Restaurant create(Restaurant restaurant) {
        Restaurant saved = repo.save(restaurant);
        log.info("Created Restaurant: {}", saved);
        return saved;
    }

    //GET ALL
    public Page<Restaurant> getAll(Pageable pageable) {
        Page<Restaurant> restaurants = repo.findAll(pageable);
        log.info("Fetched {} restaurants", restaurants.getTotalElements());
        return restaurants;
    }

    //GET BY ID (Exception Safe)
    public Restaurant getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> {
                    log.warn("Restaurant not found with ID {}", id);
                    return new ResourceNotFoundException("Restaurant not found with ID: " + id);
                });
    }

    //UPDATE
    public Restaurant update(Long id, Restaurant restaurant) {
        Restaurant existing = getById(id); // throws ResourceNotFoundException

        Optional.ofNullable(restaurant.getRestaurantName()).ifPresent(existing::setRestaurantName);
        Optional.ofNullable(restaurant.getAddress()).ifPresent(existing::setAddress);
        Optional.ofNullable(restaurant.getLatitude()).ifPresent(existing::setLatitude);
        Optional.ofNullable(restaurant.getLongitude()).ifPresent(existing::setLongitude);
        Optional.ofNullable(restaurant.getOpeningHours()).ifPresent(existing::setOpeningHours);
        Optional.ofNullable(restaurant.getClosingHours()).ifPresent(existing::setClosingHours);
        Optional.ofNullable(restaurant.getDescription()).ifPresent(existing::setDescription);
        Optional.ofNullable(restaurant.getMealType()).ifPresent(existing::setMealType);

        //Boolean fields
        existing.setVerified(restaurant.isVerified());

        Restaurant updated = repo.save(existing);
        log.info("Updated Restaurant with ID {}: {}", id, updated);
        return updated;
    }

    //DELETE
    public void delete(Long id) {
        Restaurant existing = getById(id);
        repo.delete(existing);
        log.info("Deleted Restaurant with ID {}", id);
    }
}
