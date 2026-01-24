package com.godesii.godesii_services.dto.auth;

/**
 * Customer-specific registration request
 */
public class CustomerRegistrationRequest extends RegisterRequest {

    private String preferredLanguage;
    private String referralCode;

    // Constructors
    public CustomerRegistrationRequest() {
        super();
    }

    // Getters and Setters
    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    public String getReferralCode() {
        return referralCode;
    }

    public void setReferralCode(String referralCode) {
        this.referralCode = referralCode;
    }
}
