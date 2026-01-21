package com.godesii.godesii_services.dto;

import com.godesii.godesii_services.entity.restaurant.Menu;
import com.godesii.godesii_services.entity.restaurant.Restaurant;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.util.StringUtils;

public class MenuRequest {

    @NotBlank(message = "Menu name is required")
    @Size(min = 2, max = 100, message = "Menu name must be between 2 and 100 characters")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @Pattern(regexp = "^(DINE_IN|DELIVERY|TAKEOUT|BAR|BREAKFAST|LUNCH|DINNER)$",
             message = "Menu type must be one of: DINE_IN, DELIVERY, TAKEOUT, BAR, BREAKFAST, LUNCH, DINNER")
    private String menuType;

    @Min(value = 0, message = "Sort order must be non-negative")
    private Integer sortOrder;

    private Boolean isActive = true;

    @NotNull(message = "Restaurant ID is required")
    private Long restaurantId;

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMenuType() {
        return menuType;
    }

    public void setMenuType(String menuType) {
        this.menuType = menuType;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    /**
     * Maps MenuRequest DTO to Menu entity
     */
    public static Menu mapToEntity(MenuRequest request, Restaurant restaurant) {
        Menu menu = new Menu();
        menu.setName(request.getName());
        menu.setDescription(request.getDescription());
        menu.setMenuType(request.getMenuType());
        menu.setSortOrder(request.getSortOrder());
        menu.setActive(request.getIsActive() != null ? request.getIsActive() : true);
        menu.setRestaurant(restaurant);
        return menu;
    }

    /**
     * Updates existing Menu entity with non-null values from request
     */
    public static void updateEntity(Menu existing, MenuRequest request, Restaurant restaurant) {
        if (StringUtils.hasText(request.getName())) {
            existing.setName(request.getName());
        }
        if (StringUtils.hasText(request.getDescription())) {
            existing.setDescription(request.getDescription());
        }
        if (StringUtils.hasText(request.getMenuType())) {
            existing.setMenuType(request.getMenuType());
        }
        if (request.getSortOrder() != null) {
            existing.setSortOrder(request.getSortOrder());
        }
        if (request.getIsActive() != null) {
            existing.setActive(request.getIsActive());
        }
        if (restaurant != null) {
            existing.setRestaurant(restaurant);
        }
    }
}
