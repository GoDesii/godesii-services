package com.godesii.godesii_services.entity.oauth2;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "user")
public class User {

    private Long id;
    private Date createdAt;
    private Date updatedAt;
    private Date lastLoggedIn;
    private String username;
    private String password;
    private String emailId;
    private String role;
    private String countryCode;
    private String mobileNo;
    private Boolean isMobileNoVerified;
    private AuthProvider authProvider;
    private Boolean isEmailVerified =  false;
    private Boolean isPhoneNoVerified =  false;
    private Boolean isAccountNonLocked = false;
    private Boolean isAccountNonExpired = false;
    private Boolean isCredentialsNonExpired = false;
    private UserProfile userProfile;

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

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Column(name = "last_logged_in")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getLastLoggedIn() {
        return lastLoggedIn;
    }

    public void setLastLoggedIn(Date lastLoggedIn) {
        this.lastLoggedIn = lastLoggedIn;
    }

    @Column(name = "user_name", length = 50, unique = true)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Column(name = "password")
    @JsonIgnore
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Column(name = "email_id")
    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    @Column(name = "role")
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Column(name = "country_code", length = 3)
    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    @Column(name = "mobile_no", length = 10, unique = true)
    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    @Column(name = "is_mobile_verified")
    public Boolean getMobileNoVerified() {
        return isMobileNoVerified;
    }

    public void setMobileNoVerified(Boolean mobileNoVerified) {
        isMobileNoVerified = mobileNoVerified;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_provider")
    public AuthProvider getAuthProvider() {
        return authProvider;
    }

    public void setAuthProvider(AuthProvider authProvider) {
        this.authProvider = authProvider;
    }

    @Column(name = "is_email_verified")
    public Boolean getEmailVerified() {
        return isEmailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        isEmailVerified = emailVerified;
    }

    @Column(name = "is_phone_no_verified")
    public Boolean getPhoneNoVerified() {
        return isPhoneNoVerified;
    }

    public void setPhoneNoVerified(Boolean phoneNoVerified) {
        isPhoneNoVerified = phoneNoVerified;
    }

    @Column(name = "is_account_non_locked")
    public Boolean getAccountNonLocked() {
        return isAccountNonLocked;
    }

    public void setAccountNonLocked(Boolean accountNonLocked) {
        isAccountNonLocked = accountNonLocked;
    }

    @Column(name = "is_account_non_expired")
    public Boolean getAccountNonExpired() {
        return isAccountNonExpired;
    }

    public void setAccountNonExpired(Boolean accountNonExpired) {
        isAccountNonExpired = accountNonExpired;
    }

    @Column(name = "is_credentials_non_expired")
    public Boolean getCredentialsNonExpired() {
        return isCredentialsNonExpired;
    }

    public void setCredentialsNonExpired(Boolean credentialsNonExpired) {
        isCredentialsNonExpired = credentialsNonExpired;
    }

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_id", referencedColumnName = "id")
    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

}