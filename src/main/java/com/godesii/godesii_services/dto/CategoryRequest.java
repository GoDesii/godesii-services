package com.godesii.godesii_services.dto;

import com.godesii.godesii_services.entity.restaurant.Category;
import com.godesii.godesii_services.entity.restaurant.Menu;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.util.StringUtils;

public class CategoryRequest {

    @NotBlank(message = "Category name is required")
    @Size(min = 2, max = 100, message = "Category name must be between 2 and 100 characters")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @Min(value = 0, message = "Display order must be non-negative")
    private Integer displayOrder;

    @Pattern(regexp = "^(https?://)?([\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?$", 
             message = "Invalid image URL format")
    private String imageUrl;

    private Boolean isPureVeg;

    @NotNull(message = "Menu ID is required")
    private Long menuId;

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

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getIsPureVeg() {
        return isPureVeg;
    }

    public void setIsPureVeg(Boolean isPureVeg) {
        this.isPureVeg = isPureVeg;
    }

    public Long getMenuId() {
        return menuId;
    }

    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }

    /**
     * Maps CategoryRequest DTO to Category entity
     */
    public static Category mapToEntity(CategoryRequest request, Menu menu) {
        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setDisplayOrder(request.getDisplayOrder());
        category.setImageUrl(request.getImageUrl());
        category.setPureVeg(request.getIsPureVeg());
        category.setMenu(menu);
        return category;
    }

    /**
     * Updates existing Category entity with non-null values from request
     */
    public static void updateEntity(Category existing, CategoryRequest request, Menu menu) {
        if (StringUtils.hasText(request.getName())) {
            existing.setName(request.getName());
        }
        if (StringUtils.hasText(request.getDescription())) {
            existing.setDescription(request.getDescription());
        }
        if (request.getDisplayOrder() != null) {
            existing.setDisplayOrder(request.getDisplayOrder());
        }
        if (StringUtils.hasText(request.getImageUrl())) {
            existing.setImageUrl(request.getImageUrl());
        }
        if (request.getIsPureVeg() != null) {
            existing.setPureVeg(request.getIsPureVeg());
        }
        if (menu != null) {
            existing.setMenu(menu);
        }
    }
}
