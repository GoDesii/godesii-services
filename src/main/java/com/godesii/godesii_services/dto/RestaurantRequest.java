package com.godesii.godesii_services.dto;

import com.godesii.godesii_services.entity.restaurant.Restaurant;
import com.godesii.godesii_services.entity.restaurant.RestaurantAddress;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

public class RestaurantRequest {

    @Size(min = 2, max = 100, message = "Restaurant name must be between 2 and 100 characters")
    private String name;
    @Pattern(regexp = "^[+]?[(]?[0-9]{1,4}[)]?[-\\s\\.]?[(]?[0-9]{1,4}[)]?[-\\s\\.]?[0-9]{1,9}$", message = "Invalid phone number format")
    private String phoneNo;
    @Size(min = 2, max = 50, message = "Cuisine type must be between 2 and 50 characters")
    private String cuisineType;
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
    private boolean isVerified;

    @Valid
    private List<OperationalHourRequest> operationalHourRequest;

    @Valid
    private RestaurantAddressRequest addressRequest;

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

    public List<OperationalHourRequest> getOperationalHourRequest() {
        return operationalHourRequest;
    }

    public void setOperationalHourRequest(List<OperationalHourRequest> operationalHourRequest) {
        this.operationalHourRequest = operationalHourRequest;
    }

    public RestaurantAddressRequest getAddressRequest() {
        return addressRequest;
    }

    public void setAddressRequest(RestaurantAddressRequest addressRequest) {
        this.addressRequest = addressRequest;
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
        restaurant.setOperatingHours(OperationalHourRequest.mapToEntities(request.getOperationalHourRequest()));
        restaurant.setAddress(RestaurantAddressRequest.mapToEntity(request.getAddressRequest()));
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
        if(!request.getOperationalHourRequest().isEmpty()){
            existing.setOperatingHours(OperationalHourRequest
                    .updateEntities(existing.getOperatingHours(), request.getOperationalHourRequest()));
        }
        if(request.getAddressRequest() != null){
            existing.setAddress(RestaurantAddressRequest.updateEntity(existing.getAddress(), request.getAddressRequest()));
        }
        existing.setVerified(request.isVerified());
    }

    public static class RestaurantAddressRequest{

        private String addressLine1; // Primary street address (Building number, Street name).
        private String addressLine2; // Secondary info (Suite, Floor, Landmark).
        private String city;
        private String state;
        private String postalCode;
        private String country;
        //@Pattern(regexp = "^[-+]?([1-8]?\\d(.\\d+)?)", message = "")
        private BigDecimal latitude;
        //@Pattern(regexp = "^[-+]?(180(.0+)?)", message = "")
        private BigDecimal longitude;

        public String getAddressLine1() {
            return addressLine1;
        }

        public void setAddressLine1(String addressLine1) {
            this.addressLine1 = addressLine1;
        }

        public String getAddressLine2() {
            return addressLine2;
        }

        public void setAddressLine2(String addressLine2) {
            this.addressLine2 = addressLine2;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getPostalCode() {
            return postalCode;
        }

        public void setPostalCode(String postalCode) {
            this.postalCode = postalCode;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String countryCode) {
            this.country = countryCode;
        }

        public BigDecimal getLatitude() {
            return latitude;
        }

        public void setLatitude(BigDecimal latitude) {
            this.latitude = latitude;
        }

        public BigDecimal getLongitude() {
            return longitude;
        }

        public void setLongitude(BigDecimal longitude) {
            this.longitude = longitude;
        }

        public static RestaurantAddress mapToEntity(RestaurantAddressRequest addressRequest){
            RestaurantAddress address = new RestaurantAddress();
            address.setLatitude(addressRequest.getLatitude());
            address.setLongitude(addressRequest.getLongitude());
            address.setAddressLine1(addressRequest.getAddressLine1());
            address.setAddressLine2(addressRequest.getAddressLine2());
            address.setCity(addressRequest.getCity());
            address.setState(addressRequest.getState());
            address.setCountry(addressRequest.getCountry());
            return address;
        }

        public static RestaurantAddress updateEntity(RestaurantAddress existing, RestaurantAddressRequest request){
            if(StringUtils.hasText(request.getAddressLine1())){
                existing.setAddressLine1(request.getAddressLine1());
            }
            if(StringUtils.hasText(request.getAddressLine2())){
                existing.setAddressLine2(request.getAddressLine2());
            }
            if(StringUtils.hasText(request.getCity())){
                existing.setCity(request.getCity());
            }
            if(StringUtils.hasText(request.getState())){
                existing.setState(request.getState());
            }
            if(StringUtils.hasText(request.getCountry())){
                existing.setCountry(request.getCountry());
            }
            if(request.getLatitude() != null){
                existing.setLatitude(request.getLatitude());
            }
            if(request.getLongitude() != null){
                existing.setLongitude(request.getLongitude());
            }
            return existing;
        }
    }
}
