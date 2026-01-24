package com.godesii.godesii_services.dto.auth;

import com.godesii.godesii_services.entity.auth.Gender;
import jakarta.validation.constraints.*;

/**
 * Base registration request with common fields for all user types
 */
public class RegisterRequest {

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be 10 digits")
    private String mobileNo;

    @NotBlank(message = "Country code is required")
    @Pattern(regexp = "^\\+[0-9]{1,3}$", message = "Country code must start with + and contain 1-3 digits")
    private String countryCode = "+91";

    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @Size(max = 50, message = "Middle name must not exceed 50 characters")
    private String middleName;

    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;

    @Email(message = "Email must be valid")
    private String emailId;

    private Gender gender;

    // Constructors
    public RegisterRequest() {
    }

    public RegisterRequest(String mobileNo, String firstName) {
        this.mobileNo = mobileNo;
        this.firstName = firstName;
        this.countryCode = "+91";
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }
}
