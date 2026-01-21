package com.godesii.godesii_services.service.restaurant;

import com.godesii.godesii_services.dto.MenuRequest;
import com.godesii.godesii_services.entity.restaurant.Menu;
import com.godesii.godesii_services.entity.restaurant.Restaurant;
import com.godesii.godesii_services.exception.ResourceNotFoundException;
import com.godesii.godesii_services.repository.restaurant.MenuRepository;
import com.godesii.godesii_services.repository.restaurant.RestaurantRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class MenuService {

    private final MenuRepository menuRepository;
    private final RestaurantRepo restaurantRepository;

    public MenuService(MenuRepository menuRepository, RestaurantRepo restaurantRepository) {
        this.menuRepository = menuRepository;
        this.restaurantRepository = restaurantRepository;
    }

    /**
     * Create a new menu
     */
    public Menu create(MenuRequest request) {
        // Validate restaurant exists
        Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Restaurant not found with ID: " + request.getRestaurantId()
                ));

        // Map and save
        Menu menu = MenuRequest.mapToEntity(request, restaurant);
        Menu saved = menuRepository.save(menu);
        log.info("Created menu: {} for restaurant ID: {}", saved.getName(), restaurant.getId());
        return saved;
    }

    /**
     * Get all menus
     */
    public List<Menu> getAll() {
        return menuRepository.findAll();
    }

    /**
     * Get menu by ID
     */
    public Menu getById(Long id) {
        return menuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Menu not found with ID: " + id
                ));
    }

    /**
     * Get menus by restaurant ID
     */
    public List<Menu> getByRestaurantId(Long restaurantId) {
        // Validate restaurant exists
        restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Restaurant not found with ID: " + restaurantId
                ));
        
        return menuRepository.findAll().stream()
                .filter(menu -> menu.getRestaurant() != null && 
                               menu.getRestaurant().getId().equals(restaurantId))
                .toList();
    }

    /**
     * Update an existing menu
     */
    public Menu update(Long id, MenuRequest request) {
        // Find existing menu
        Menu existing = getById(id);

        // Validate restaurant exists if restaurant ID is being changed
        Restaurant restaurant = null;
        if (request.getRestaurantId() != null && 
            !request.getRestaurantId().equals(existing.getRestaurant().getId())) {
            restaurant = restaurantRepository.findById(request.getRestaurantId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Restaurant not found with ID: " + request.getRestaurantId()
                    ));
        }

        // Update entity
        MenuRequest.updateEntity(existing, request, restaurant);
        Menu updated = menuRepository.save(existing);
        log.info("Updated menu ID: {}", id);
        return updated;
    }

    /**
     * Delete a menu
     */
    public void delete(Long id) {
        Menu existing = getById(id);
        menuRepository.delete(existing);
        log.info("Deleted menu ID: {}", id);
    }
}
