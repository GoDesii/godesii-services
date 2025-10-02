package com.godesii.godesii_services.service;

import com.godesii.godesii_services.entity.restaurant.Restaurant;
import com.godesii.godesii_services.repository.restaurant.RestaurantRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RestaurantService {

    private final RestaurantRepo repo;

    public RestaurantService(RestaurantRepo repo) {
        this.repo = repo;
    }

    public Restaurant create(Restaurant restaurant) {
        return repo.save(restaurant);
    }

    public List<Restaurant> getAll() {
        return repo.findAll();
    }

    public Restaurant getById(Long id) {
        return repo.findById(id).orElseThrow();
    }

    public Restaurant update(Long id, Restaurant restaurant) {
        Restaurant existing = getById(id);
        existing.setRestaurantName(restaurant.getRestaurantName());
        existing.setAddress(restaurant.getAddress());
        existing.setLatitude(restaurant.getLatitude());
        existing.setLongitude(restaurant.getLongitude());
        existing.setOpeningHours(restaurant.getOpeningHours());
        existing.setClosingHours(restaurant.getClosingHours());
        existing.setDescription(restaurant.getDescription());
        existing.setMealType(restaurant.getMealType());
        existing.setVerified(restaurant.isVerified());
        return repo.save(existing);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}

