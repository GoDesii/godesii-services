package com.godesii.godesii_services.service.restaurant;

import com.godesii.godesii_services.dto.DashboardStatsDto;
import com.godesii.godesii_services.repository.restaurant.MenuItemRepo;
import com.godesii.godesii_services.repository.restaurant.MenuRepository;
import com.godesii.godesii_services.repository.restaurant.RestaurantRepo;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final RestaurantRepo restaurantRepo;
    private final MenuRepository menuRepo;
    private final MenuItemRepo menuItemRepo;

    public DashboardService(RestaurantRepo restaurantRepo, MenuRepository menuRepo, MenuItemRepo menuItemRepo) {
        this.restaurantRepo = restaurantRepo;
        this.menuRepo = menuRepo;
        this.menuItemRepo = menuItemRepo;
    }

    public DashboardStatsDto getDashboardStats(String username) {
        long totalRestaurants = restaurantRepo.countByCreatedBy(username);
        long totalMenus = menuRepo.countByRestaurantCreatedBy(username);
        long totalMenuItems = menuItemRepo.countByCategoryMenuRestaurantCreatedBy(username);
        long availableItems = menuItemRepo.countByCategoryMenuRestaurantCreatedByAndIsAvailableTrue(username);

        return new DashboardStatsDto(totalRestaurants, totalMenus, totalMenuItems, availableItems);
    }
}
