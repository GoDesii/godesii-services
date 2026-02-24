package com.godesii.godesii_services.entity.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.godesii.godesii_services.entity.BaseEntity;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "shipping_address")
public class ShippingAddress extends BaseEntity {

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

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "latitude")
    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    @Column(name = "lognitude")
    public String getLongitude() {
        return longitude;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
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

//    @ManyToMany(
//            mappedBy = "addresses"
//    )
//    @JsonIgnoreProperties(value = {"addresses","userProfiles"})
//    public List<UserProfile> getUserProfiles() {
//        return userProfiles;
//    }
//
//    public void setUserProfiles(List<UserProfile> userProfiles) {
//        this.userProfiles = userProfiles;
//    }

}