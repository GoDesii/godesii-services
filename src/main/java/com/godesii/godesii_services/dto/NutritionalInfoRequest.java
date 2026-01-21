package com.godesii.godesii_services.dto;

import com.godesii.godesii_services.entity.restaurant.MenuItem;
import com.godesii.godesii_services.entity.restaurant.NutritionalInfo;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class NutritionalInfoRequest {

    @Min(value = 0, message = "Calories must be non-negative")
    private Integer calories;

    @DecimalMin(value = "0.0", message = "Protein must be non-negative")
    private BigDecimal protein;

    @DecimalMin(value = "0.0", message = "Total fat must be non-negative")
    private BigDecimal totalFat;

    @DecimalMin(value = "0.0", message = "Carbohydrates must be non-negative")
    private BigDecimal carbohydrates;

    @DecimalMin(value = "0.0", message = "Sugar must be non-negative")
    private BigDecimal sugar;

    @Size(max = 500, message = "Allergens cannot exceed 500 characters")
    private String allergens;

    private Boolean isGlutenFree = false;

    private Boolean isDairyFree = false;

    @NotNull(message = "Menu item ID is required")
    private String menuItemId;

    // Getters and Setters
    public Integer getCalories() {
        return calories;
    }

    public void setCalories(Integer calories) {
        this.calories = calories;
    }

    public BigDecimal getProtein() {
        return protein;
    }

    public void setProtein(BigDecimal protein) {
        this.protein = protein;
    }

    public BigDecimal getTotalFat() {
        return totalFat;
    }

    public void setTotalFat(BigDecimal totalFat) {
        this.totalFat = totalFat;
    }

    public BigDecimal getCarbohydrates() {
        return carbohydrates;
    }

    public void setCarbohydrates(BigDecimal carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    public BigDecimal getSugar() {
        return sugar;
    }

    public void setSugar(BigDecimal sugar) {
        this.sugar = sugar;
    }

    public String getAllergens() {
        return allergens;
    }

    public void setAllergens(String allergens) {
        this.allergens = allergens;
    }

    public Boolean getIsGlutenFree() {
        return isGlutenFree;
    }

    public void setIsGlutenFree(Boolean isGlutenFree) {
        this.isGlutenFree = isGlutenFree;
    }

    public Boolean getIsDairyFree() {
        return isDairyFree;
    }

    public void setIsDairyFree(Boolean isDairyFree) {
        this.isDairyFree = isDairyFree;
    }

    public String getMenuItemId() {
        return menuItemId;
    }

    public void setMenuItemId(String menuItemId) {
        this.menuItemId = menuItemId;
    }

    /**
     * Maps NutritionalInfoRequest DTO to NutritionalInfo entity
     */
    public static NutritionalInfo mapToEntity(NutritionalInfoRequest request, MenuItem menuItem) {
        NutritionalInfo info = new NutritionalInfo();
        info.setMenuItem(menuItem);
        info.setCalories(request.getCalories());
        info.setProtein(request.getProtein());
        info.setTotalFat(request.getTotalFat());
        info.setCarbohydrates(request.getCarbohydrates());
        info.setSugar(request.getSugar());
        info.setAllergens(request.getAllergens());
        info.setGlutenFree(request.getIsGlutenFree() != null ? request.getIsGlutenFree() : false);
        info.setDairyFree(request.getIsDairyFree() != null ? request.getIsDairyFree() : false);
        return info;
    }

    /**
     * Updates existing NutritionalInfo entity with non-null values from request
     */
    public static void updateEntity(NutritionalInfo existing, NutritionalInfoRequest request, MenuItem menuItem) {
        if (request.getCalories() != null) {
            existing.setCalories(request.getCalories());
        }
        if (request.getProtein() != null) {
            existing.setProtein(request.getProtein());
        }
        if (request.getTotalFat() != null) {
            existing.setTotalFat(request.getTotalFat());
        }
        if (request.getCarbohydrates() != null) {
            existing.setCarbohydrates(request.getCarbohydrates());
        }
        if (request.getSugar() != null) {
            existing.setSugar(request.getSugar());
        }
        if (request.getAllergens() != null) {
            existing.setAllergens(request.getAllergens());
        }
        if (request.getIsGlutenFree() != null) {
            existing.setGlutenFree(request.getIsGlutenFree());
        }
        if (request.getIsDairyFree() != null) {
            existing.setDairyFree(request.getIsDairyFree());
        }
        if (menuItem != null) {
            existing.setMenuItem(menuItem);
        }
    }
}
