package com.godesii.godesii_services.dto;

public class MobileUserCreationRequest {

    private String mobile;
    private String countryCode;


    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
