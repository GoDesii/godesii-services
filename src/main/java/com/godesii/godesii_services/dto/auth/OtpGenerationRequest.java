package com.godesii.godesii_services.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Request to generate OTP for login
 */
public class OtpGenerationRequest {

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be 10 digits")
    private String mobileNo;

    @NotBlank(message = "Country code is required")
    @Pattern(regexp = "^\\+[0-9]{1,3}$", message = "Country code must start with + and contain 1-3 digits")
    private String countryCode = "+91";

    // Constructors
    public OtpGenerationRequest() {
    }

    public OtpGenerationRequest(String mobileNo, String countryCode) {
        this.mobileNo = mobileNo;
        this.countryCode = countryCode;
    }

    // Getters and Setters
    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}
