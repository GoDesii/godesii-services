package com.godesii.godesii_services.service.restaurant;

import com.godesii.godesii_services.dto.RestaurantRequest;
import com.godesii.godesii_services.entity.restaurant.Restaurant;
import com.godesii.godesii_services.exception.ResourceNotFoundException;
import com.godesii.godesii_services.repository.restaurant.RestaurantRepo;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RestaurantService {

    private final RestaurantRepo repo;

    public static final Logger log = LoggerFactory.getLogger(RestaurantService.class);

    public RestaurantService(RestaurantRepo repo) {
        this.repo = repo;
    }

    /**
     * Create a new restaurant
     * 
     * @param request RestaurantRequest DTO with validated data
     * @return Created restaurant entity
     */
    @Transactional
    public Restaurant create(RestaurantRequest request) {
        Restaurant restaurant = RestaurantRequest.mapToEntity(request);
        Restaurant saved = repo.save(restaurant);
        return saved;
    }

    /**
     * Get all restaurants with pagination
     * 
     * @param pageable Pagination information
     * @return Page of restaurants
     */
    public Page<Restaurant> getAll(Pageable pageable) {
        Page<Restaurant> restaurants = repo.findAll(pageable);
        // log.info("Fetched {} restaurants out of {} total",
        // restaurants.getNumberOfElements(), restaurants.getTotalElements());
        return restaurants;
    }

    /**
     * Get restaurant by ID
     * 
     * @param id Restaurant ID
     * @return Restaurant entity
     * @throws ResourceNotFoundException if restaurant not found
     */
    public Restaurant getById(@NonNull Long id) {
        return repo.findById(id)
                .orElseThrow(() -> {
                    // log.warn("Restaurant not found with ID: {}", id);
                    return new ResourceNotFoundException("Restaurant not found with ID: " + id);
                });
    }

    /**
     * Update existing restaurant
     * 
     * @param id      Restaurant ID
     * @param request RestaurantRequest with update data
     * @return Updated restaurant entity
     * @throws ResourceNotFoundException if restaurant not found
     */
    @Transactional
    public Restaurant update(@NonNull Long id, RestaurantRequest request) {
        Restaurant existing = getById(id);

        // Update only non-null fields
        RestaurantRequest.updateEntity(existing, request);

        Restaurant updated = repo.save(existing);
        // log.info("Updated Restaurant with ID: {}, Name: {}", id, updated.getName());
        return updated;
    }

    /**
     * Delete restaurant by ID
     * 
     * @param id Restaurant ID
     * @throws ResourceNotFoundException if restaurant not found
     */
    @Transactional
    public void delete(@NonNull Long id) {
        Restaurant existing = getById(id);
        repo.delete(existing);
        // log.info("Deleted Restaurant with ID: {}, Name: {}", id, existing.getName());
    }
}
