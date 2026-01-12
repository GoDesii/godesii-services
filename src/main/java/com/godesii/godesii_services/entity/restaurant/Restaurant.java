package com.godesii.godesii_services.entity.restaurant;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


import java.util.List;


@Entity
@Table(name = "restaurant")
@Setter
@Getter
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String phoneNo;
    private String cuisineType;
    private String description;
    private boolean isVerified;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    private List<OperationalHour> operatingHours;

    @Embedded
    private RestaurantAddress address;

}

