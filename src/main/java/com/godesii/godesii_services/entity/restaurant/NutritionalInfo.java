package com.godesii.godesii_services.entity.restaurant;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "nutritional_info")
@Getter
@Setter
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
}
