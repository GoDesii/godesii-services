package com.godesii.godesii_services.entity.restaurant;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Embeddable
@Getter
@Setter
public class RestaurantAddress {

    private String addressLine1; // Primary street address (Building number, Street name).
    private String addressLine2; // Secondary info (Suite, Floor, Landmark).
    private String city;
    private String state;
    private String postalCode;
    private String countryCode;
    private BigDecimal latitude;
    private BigDecimal longitude;

}
