package com.godesii.godesii_services.dto;

import com.godesii.godesii_services.entity.restaurant.OperationalHour;
import com.godesii.godesii_services.entity.restaurant.Restaurant;
import com.godesii.godesii_services.entity.restaurant.RestaurantAddress;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

public class RestaurantRequest {

    @NotBlank(message = "Restaurant name is required")
    @Size(min = 2, max = 100, message = "Restaurant name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[+]?[(]?[0-9]{1,4}[)]?[-\\s\\.]?[(]?[0-9]{1,4}[)]?[-\\s\\.]?[0-9]{1,9}$", message = "Invalid phone number format")
    private String phoneNo;

    @NotBlank(message = "Cuisine type is required")
    @Size(min = 2, max = 50, message = "Cuisine type must be between 2 and 50 characters")
    private String cuisineType;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    private boolean isVerified;

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getCuisineType() {
        return cuisineType;
    }

    public void setCuisineType(String cuisineType) {
        this.cuisineType = cuisineType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    /*
     * Maps RestaurantRequest DTO to Restaurant entity
     */
    public static Restaurant mapToEntity(RestaurantRequest request) {
        Restaurant restaurant = new Restaurant();
        restaurant.setName(request.getName());
        restaurant.setPhoneNo(request.getPhoneNo());
        restaurant.setCuisineType(request.getCuisineType());
        restaurant.setDescription(request.getDescription());
        restaurant.setVerified(request.isVerified());
        return restaurant;
    }

    /**
     * Updates existing Restaurant entity with non-null values from request
     */
    public static void updateEntity(Restaurant existing, RestaurantRequest request) {
        if (request.getName() != null) {
            existing.setName(request.getName());
        }
        if (request.getPhoneNo() != null) {
            existing.setPhoneNo(request.getPhoneNo());
        }
        if (request.getCuisineType() != null) {
            existing.setCuisineType(request.getCuisineType());
        }
        if (request.getDescription() != null) {
            existing.setDescription(request.getDescription());
        }
        existing.setVerified(request.isVerified());
    }
}
