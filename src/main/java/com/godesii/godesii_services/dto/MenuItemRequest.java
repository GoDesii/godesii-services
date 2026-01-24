package com.godesii.godesii_services.dto;

import com.godesii.godesii_services.entity.restaurant.Category;
import com.godesii.godesii_services.entity.restaurant.MenuItem;
import jakarta.validation.constraints.*;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

public class MenuItemRequest {

    @NotBlank(message = "Menu item name is required")
    @Size(min = 2, max = 100, message = "Menu item name must be between 2 and 100 characters")
    private String name;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @NotNull(message = "Base price is required")
    @DecimalMin(value = "0.01", message = "Base price must be at least 0.01")
    private BigDecimal basePrice;

    private String imageUrl;

    private Boolean isAvailable = true;

    @Pattern(regexp = "^(VEG|NON_VEG|EGG|VEGAN)$",
             message = "Dietary type must be one of: VEG, NON_VEG, EGG, VEGAN")
    private String dietaryType;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

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

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public String getDietaryType() {
        return dietaryType;
    }

    public void setDietaryType(String dietaryType) {
        this.dietaryType = dietaryType;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * Maps MenuItemRequest DTO to MenuItem entity
     */
    public static MenuItem mapToEntity(MenuItemRequest request, Category category) {
        MenuItem menuItem = new MenuItem();
        menuItem.setName(request.getName());
        menuItem.setDescription(request.getDescription());
        menuItem.setBasePrice(request.getBasePrice());
        menuItem.setImageUrl(request.getImageUrl());
        menuItem.setAvailable(request.getIsAvailable() != null ? request.getIsAvailable() : true);
        menuItem.setDietaryType(request.getDietaryType());
        menuItem.setCategory(category);
        return menuItem;
    }

    /**
     * Updates existing MenuItem entity with non-null values from request
     */
    public static void updateEntity(MenuItem existing, MenuItemRequest request, Category category) {
        if (StringUtils.hasText(request.getName())) {
            existing.setName(request.getName());
        }
        if (StringUtils.hasText(request.getDescription())) {
            existing.setDescription(request.getDescription());
        }
        if (request.getBasePrice() != null) {
            existing.setBasePrice(request.getBasePrice());
        }
        if (StringUtils.hasText(request.getImageUrl())) {
            existing.setImageUrl(request.getImageUrl());
        }
        if (request.getIsAvailable() != null) {
            existing.setAvailable(request.getIsAvailable());
        }
        if (StringUtils.hasText(request.getDietaryType())) {
            existing.setDietaryType(request.getDietaryType());
        }
        if (category != null) {
            existing.setCategory(category);
        }
    }
}
