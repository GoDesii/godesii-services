package com.godesii.godesii_services.dto;

import com.godesii.godesii_services.entity.auth.UserProfile;

public class UserProfileCreateResponse {

    private String firstName;
    private String lastName;
    private String middleName;
    private String gender;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public static UserProfileCreateResponse mapToResponse(UserProfile userProfile) {
        if(userProfile == null) {
            throw new IllegalArgumentException("UserProfile cannot be null");
        }
        UserProfileCreateResponse userProfileCreateResponse = new UserProfileCreateResponse();
        userProfileCreateResponse.setFirstName(userProfile.getFirstName());
        userProfileCreateResponse.setLastName(userProfile.getLastName());
        userProfileCreateResponse.setMiddleName(userProfile.getMiddleName());
        userProfileCreateResponse.setGender(userProfile.getGender().toString());
        return userProfileCreateResponse;
    }
}
