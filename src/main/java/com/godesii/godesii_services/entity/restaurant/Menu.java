package com.godesii.godesii_services.entity.restaurant;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "menus")
@Getter
@Setter
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_id")
    private String id;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    private String name;
    private String description;
    private boolean isActive = true;
    private String menuType; // Categorizes the menu by service (e.g., DINE_IN, DELIVERY, BAR).
    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;
    private Integer sortOrder; // Determines the display priority when multiple menus exist for one restaurant.
    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL)
    @OrderBy("displayOrder ASC")
    private List<Category> categories;


}

