package com.godesii.godesii_services.entity.restaurant;

import java.util.List;

public class Restaurant {
    private Long id;
    private String restaurantName;
    private String address;
    private String latitude;
    private String longitude;
    private String openingHours;
    private String closingHours;
    private String description;
    private String mealType;
    private boolean isVerified;
    //one to many
   private List<MenuItem> menuItemList;
}
