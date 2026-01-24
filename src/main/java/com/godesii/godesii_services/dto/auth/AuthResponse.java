package com.godesii.godesii_services.dto.auth;

import com.godesii.godesii_services.entity.auth.Role;

/**
 * Unified authentication response for all login/registration flows
 */
public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private String expiresIn;
    private UserInfo user;

    // Constructors
    public AuthResponse() {
    }

    public AuthResponse(String accessToken, String refreshToken, String expiresIn, UserInfo user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.user = user;
    }

    // Getters and Setters
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(String expiresIn) {
        this.expiresIn = expiresIn;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    /**
     * Nested class for user information in the response
     */
    public static class UserInfo {
        private Long id;
        private String username;
        private String mobileNo;
        private String countryCode;
        private Role role;
        private String firstName;
        private String lastName;
        private String emailId;
        private Boolean isMobileVerified;
        private Boolean isEmailVerified;

        // Constructors
        public UserInfo() {
        }

        public UserInfo(Long id, String username, String mobileNo, Role role, String firstName) {
            this.id = id;
            this.username = username;
            this.mobileNo = mobileNo;
            this.role = role;
            this.firstName = firstName;
        }

        // Getters and Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

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

        public Role getRole() {
            return role;
        }

        public void setRole(Role role) {
            this.role = role;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
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

        public Boolean getMobileVerified() {
            return isMobileVerified;
        }

        public void setMobileVerified(Boolean mobileVerified) {
            isMobileVerified = mobileVerified;
        }

        public Boolean getEmailVerified() {
            return isEmailVerified;
        }

        public void setEmailVerified(Boolean emailVerified) {
            isEmailVerified = emailVerified;
        }
    }
}
