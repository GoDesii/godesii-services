package com.godesii.godesii_services.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Password-based login request
 * Supports login with either mobile number or username
 */
public class PasswordLoginRequest {

    private String mobileNo;
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    private String countryCode = "+91";

    // Constructors
    public PasswordLoginRequest() {
    }

    public PasswordLoginRequest(String identifier, String password) {
        // Auto-detect if identifier is mobile or username
        if (identifier != null && identifier.matches("^[0-9]{10}$")) {
            this.mobileNo = identifier;
        } else {
            this.username = identifier;
        }
        this.password = password;
    }

    // Getters and Setters
    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    /**
     * Get the login identifier (mobile or username)
     */
    public String getIdentifier() {
        return mobileNo != null ? mobileNo : username;
    }
}
