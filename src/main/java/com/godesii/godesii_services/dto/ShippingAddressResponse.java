package com.godesii.godesii_services.dto;

import com.godesii.godesii_services.entity.auth.ShippingAddress;

import java.util.ArrayList;
import java.util.List;

public class ShippingAddressResponse {

    private Long id;
    private String latitude;
    private String longitude;
    private String houseNumber;
    private String street;
    private String city;
    private String state;
    private String pinCode;
    private String country;
    private String addressType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddressType() {
        return addressType;
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

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

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public static ShippingAddressResponse mapToUserAddressCreateResponse(ShippingAddress shippingAddress){
        ShippingAddressResponse response = new ShippingAddressResponse();
        response.setId(shippingAddress.getId());
        response.setStreet(shippingAddress.getStreet());
        response.setHouseNumber(shippingAddress.getHouseNumber());
        response.setLatitude(shippingAddress.getLatitude());
        response.setLongitude(shippingAddress.getLongitude());
        response.setAddressType(shippingAddress.getAddressType());
        response.setCity(shippingAddress.getCity());
        response.setState(shippingAddress.getState());
        response.setCountry(shippingAddress.getCountry());
        response.setPinCode(shippingAddress.getPinCode());
        return response;
    }

    public static List<ShippingAddressResponse> mapToUserAddressCreateResponses(List<ShippingAddress> shippingAddresses){
        List<ShippingAddressResponse> responses = new ArrayList<>(shippingAddresses.size());
        for(ShippingAddress shippingAddress: shippingAddresses){
            responses.add(mapToUserAddressCreateResponse(shippingAddress));
        }
        return responses;
    }


}
