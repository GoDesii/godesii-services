package com.godesii.godesii_services.entity.restaurant;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;

@Entity
@Table(name = "menu_items")
@Getter
@Setter
public class MenuItem {
    @Id
    @UuidGenerator
    @Column(name = "item_id")
    private String itemId;
    @Column(nullable = false)
    private String name;
    private String description;
    @Column(precision = 10, scale = 2)
    private BigDecimal basePrice;
    private String imageUrl;
    private boolean isAvailable = true;
    // 2026 Industry Standard: Dietary Markers
    private String dietaryType; // VEG, NON_VEG, EGG, VEGAN
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToOne(mappedBy = "menuItem", cascade = CascadeType.ALL)
    private NutritionalInfo nutritionalInfo;


}
