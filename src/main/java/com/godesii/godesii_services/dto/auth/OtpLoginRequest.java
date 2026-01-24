package com.godesii.godesii_services.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * OTP-based login request
 */
public class OtpLoginRequest {

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be 10 digits")
    private String mobileNo;

    @NotBlank(message = "Country code is required")
    @Pattern(regexp = "^\\+[0-9]{1,3}$", message = "Country code must start with + and contain 1-3 digits")
    private String countryCode = "+91";

    @NotBlank(message = "OTP is required")
    @Size(min = 6, max = 6, message = "OTP must be 6 digits")
    @Pattern(regexp = "^[0-9]{6}$", message = "OTP must contain only digits")
    private String otp;

    // Constructors
    public OtpLoginRequest() {
    }

    public OtpLoginRequest(String mobileNo, String countryCode, String otp) {
        this.mobileNo = mobileNo;
        this.countryCode = countryCode;
        this.otp = otp;
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

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
