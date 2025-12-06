package com.godesii.godesii_services.dto;

import com.godesii.godesii_services.entity.auth.ShippingAddress;
import jakarta.validation.constraints.NotBlank;

public class ShippingAddressCreateRequest {

    private String latitude;
    private String longitude;
    private String houseNumber;
    private String street;
    private String city;
    private String state;
    private String pinCode;
    private String country;
    private String addressType;

    @NotBlank(message = "Address Type is required!")
    public String getAddressType() {
        return addressType;
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }

    @NotBlank(message = "City is required!")
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @NotBlank(message = "Country is required!")
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @NotBlank(message = "House number is required!")
    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    @NotBlank(message = "Latitude is required!")
    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    @NotBlank(message = "Logitude is required!")
    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    @NotBlank(message = "Pin code is required!")
    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    @NotBlank(message = "State is required!")
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @NotBlank(message = "Street No is required!")
    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public static ShippingAddress mapToEntity(ShippingAddressCreateRequest addressRequest) {
        ShippingAddress address = new ShippingAddress();
        address.setLatitude(addressRequest.getLatitude());
        address.setLongitude(addressRequest.getLongitude());
        address.setStreet(addressRequest.getStreet());
        address.setHouseNumber(addressRequest.getHouseNumber());
        address.setCity(addressRequest.getCity());
        address.setState(addressRequest.getState());
        address.setCountry(addressRequest.getCountry());
        address.setPinCode(addressRequest.getPinCode());
        address.setAddressType(addressRequest.getAddressType());
        return address;
    }
}
