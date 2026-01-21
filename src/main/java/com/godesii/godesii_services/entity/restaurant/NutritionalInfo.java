package com.godesii.godesii_services.entity.restaurant;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "nutritional_info")
public class NutritionalInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long infoId;

    // One-to-One mapping back to the MenuItem
    @OneToOne
    @JoinColumn(name = "item_id", nullable = false)
    private MenuItem menuItem;

    private Integer calories; // kcal

    @Column(precision = 5, scale = 2)
    private BigDecimal protein; // grams

    @Column(precision = 5, scale = 2)
    private BigDecimal totalFat; // grams

    @Column(precision = 5, scale = 2)
    private BigDecimal carbohydrates; // grams

    @Column(precision = 5, scale = 2)
    private BigDecimal sugar; // grams

    // 2026 Health Focus: Comma-separated or JSON list of allergens
    private String allergens; // e.g., "PEANUTS, SOY, WHEAT"

    private boolean isGlutenFree;
    private boolean isDairyFree;

    public Long getInfoId() {
        return infoId;
    }

    public void setInfoId(Long infoId) {
        this.infoId = infoId;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
    }

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

    public boolean isGlutenFree() {
        return isGlutenFree;
    }

    public void setGlutenFree(boolean glutenFree) {
        isGlutenFree = glutenFree;
    }

    public boolean isDairyFree() {
        return isDairyFree;
    }

    public void setDairyFree(boolean dairyFree) {
        isDairyFree = dairyFree;
    }
}
