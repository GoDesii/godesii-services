package com.godesii.godesii_services.dto;


import com.godesii.godesii_services.entity.auth.User;

import java.util.List;

public class UserProfileCreateResponse {

    private String id;
    private String firstName;
    private String lastName;
    private String middleName;
    private String gender;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public static UserProfileCreateResponse mapToUserProfileCreateResponse(User user){
        UserProfileCreateResponse  response = new UserProfileCreateResponse();
        response.setFirstName(user.getFirstName());
        response.setMiddleName(user.getMiddleName());
        response.setLastName(user.getLastName());
        response.setGender(user.getGender().getValue());
        return response;
    }
}
