package com.godesii.godesii_services.entity.restaurant;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "categories")
@Getter
@Setter
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;
    private String name;
    private String description;
    private Integer displayOrder; // Controls the sorting sequence on the digital/print menu.
    private String imageUrl;
    private Boolean isPureVeg; // Optional filter for platforms like Swiggy to show purely vegetarian categories.

    @ManyToOne
    @JoinColumn(name = "menu_id")
    private Menu menu;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<MenuItem> items;

}

