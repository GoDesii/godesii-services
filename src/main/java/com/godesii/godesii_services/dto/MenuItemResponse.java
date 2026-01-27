package com.godesii.godesii_services.dto;

import com.godesii.godesii_services.entity.restaurant.MenuItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Response DTO for MenuItem to prevent circular reference issues
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemResponse {
    private String itemId;
    private String name;
    private String description;
    private BigDecimal basePrice;
    private String imageUrl;
    private boolean isAvailable;
    private String dietaryType;
    
    // Simplified category information without circular references
    private CategoryInfo category;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryInfo {
        private Long id;
        private String name;
        private String description;
        private Integer displayOrder;
        private String imageUrl;
        private MenuInfo menu;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MenuInfo {
        private Long id;
        private String name;
        private String description;
        private String menuType;
        private Integer sortOrder;
        private RestaurantInfo restaurant;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RestaurantInfo {
        private Long id;
        private String name;
        private String phoneNo;
        private String cuisineType;
        private String description;
        private boolean isActive;
        private boolean isVerified;
    }

    /**
     * Convert MenuItem entity to MenuItemResponse DTO
     * This method breaks the circular reference chain
     */
    public static MenuItemResponse fromEntity(MenuItem menuItem) {
        if (menuItem == null) {
            return null;
        }

        MenuItemResponse response = new MenuItemResponse();
        response.setItemId(menuItem.getItemId());
        response.setName(menuItem.getName());
        response.setDescription(menuItem.getDescription());
        response.setBasePrice(menuItem.getBasePrice());
        response.setImageUrl(menuItem.getImageUrl());
        response.setAvailable(menuItem.isAvailable());
        response.setDietaryType(menuItem.getDietaryType());

        // Map category without circular references
        if (menuItem.getCategory() != null) {
            CategoryInfo categoryInfo = new CategoryInfo();
            categoryInfo.setId(menuItem.getCategory().getId());
            categoryInfo.setName(menuItem.getCategory().getName());
            categoryInfo.setDescription(menuItem.getCategory().getDescription());
            categoryInfo.setDisplayOrder(menuItem.getCategory().getDisplayOrder());
            categoryInfo.setImageUrl(menuItem.getCategory().getImageUrl());

            // Map menu without circular references
            if (menuItem.getCategory().getMenu() != null) {
                MenuInfo menuInfo = new MenuInfo();
                menuInfo.setId(menuItem.getCategory().getMenu().getId());
                menuInfo.setName(menuItem.getCategory().getMenu().getName());
                menuInfo.setDescription(menuItem.getCategory().getMenu().getDescription());
                menuInfo.setMenuType(menuItem.getCategory().getMenu().getMenuType());
                menuInfo.setSortOrder(menuItem.getCategory().getMenu().getSortOrder());

                // Map restaurant without circular references
                if (menuItem.getCategory().getMenu().getRestaurant() != null) {
                    RestaurantInfo restaurantInfo = new RestaurantInfo();
                    restaurantInfo.setId(menuItem.getCategory().getMenu().getRestaurant().getId());
                    restaurantInfo.setName(menuItem.getCategory().getMenu().getRestaurant().getName());
                    restaurantInfo.setPhoneNo(menuItem.getCategory().getMenu().getRestaurant().getPhoneNo());
                    restaurantInfo.setCuisineType(menuItem.getCategory().getMenu().getRestaurant().getCuisineType());
                    restaurantInfo.setDescription(menuItem.getCategory().getMenu().getRestaurant().getDescription());
                    restaurantInfo.setActive(menuItem.getCategory().getMenu().getRestaurant().isActive());
                    restaurantInfo.setVerified(menuItem.getCategory().getMenu().getRestaurant().isVerified());

                    menuInfo.setRestaurant(restaurantInfo);
                }

                categoryInfo.setMenu(menuInfo);
            }

            response.setCategory(categoryInfo);
        }

        return response;
    }
}
