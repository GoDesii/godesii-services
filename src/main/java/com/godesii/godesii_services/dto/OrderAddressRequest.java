package com.godesii.godesii_services.dto;

import com.godesii.godesii_services.entity.order.OrderAddress;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.util.StringUtils;

public class OrderAddressRequest {

    @Pattern(regexp = "^[-+]?([1-8]?\\d(\\.\\d+)?|90(\\.0+)?)$", message = "Invalid latitude format")
    private String latitude;

    @Pattern(regexp = "^[-+]?((1[0-7]\\d)|([1-9]?\\d))(\\.\\d+)?|180(\\.0+)?$", message = "Invalid longitude format")
    private String longitude;

    @Size(max = 50, message = "House number cannot exceed 50 characters")
    private String houseNumber;

    @Size(max = 200, message = "Street cannot exceed 200 characters")
    private String street;

    @NotBlank(message = "City is required")
    @Size(min = 2, max = 100, message = "City must be between 2 and 100 characters")
    private String city;

    @NotBlank(message = "State is required")
    @Size(min = 2, max = 100, message = "State must be between 2 and 100 characters")
    private String state;

    @NotBlank(message = "Pin code is required")
    @Pattern(regexp = "^[0-9]{5,10}$", message = "Invalid pin code format")
    private String pinCode;

    @NotBlank(message = "Country is required")
    @Size(min = 2, max = 100, message = "Country must be between 2 and 100 characters")
    private String country;

    @Size(max = 20, message = "Address type cannot exceed 20 characters")
    private String addressType;

    // Getters and Setters
    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
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

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAddressType() {
        return addressType;
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }

    /**
     * Maps OrderAddressRequest DTO to OrderAddress entity
     */
    public static OrderAddress mapToEntity(OrderAddressRequest request) {
        if (request == null) {
            return null;
        }
        OrderAddress address = new OrderAddress();
        address.setLatitude(request.getLatitude());
        address.setLongitude(request.getLongitude());
        address.setHouseNumber(request.getHouseNumber());
        address.setStreet(request.getStreet());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPinCode(request.getPinCode());
        address.setCountry(request.getCountry());
        address.setAddressType(request.getAddressType());
        return address;
    }

    /**
     * Updates existing OrderAddress entity with non-null values from request
     */
    public static OrderAddress updateEntity(OrderAddress existing, OrderAddressRequest request) {
        if (existing == null) {
            existing = new OrderAddress();
        }
        if (StringUtils.hasText(request.getLatitude())) {
            existing.setLatitude(request.getLatitude());
        }
        if (StringUtils.hasText(request.getLongitude())) {
            existing.setLongitude(request.getLongitude());
        }
        if (StringUtils.hasText(request.getHouseNumber())) {
            existing.setHouseNumber(request.getHouseNumber());
        }
        if (StringUtils.hasText(request.getStreet())) {
            existing.setStreet(request.getStreet());
        }
        if (StringUtils.hasText(request.getCity())) {
            existing.setCity(request.getCity());
        }
        if (StringUtils.hasText(request.getState())) {
            existing.setState(request.getState());
        }
        if (StringUtils.hasText(request.getPinCode())) {
            existing.setPinCode(request.getPinCode());
        }
        if (StringUtils.hasText(request.getCountry())) {
            existing.setCountry(request.getCountry());
        }
        if (StringUtils.hasText(request.getAddressType())) {
            existing.setAddressType(request.getAddressType());
        }
        return existing;
    }
}
