package com.godesii.godesii_services.service;

import com.godesii.godesii_services.dto.UserProfileCreateRequest;
import com.godesii.godesii_services.dto.UserProfileCreateResponse;

public interface ProfileService {

    UserProfileCreateResponse saveOrUpdateProfile(UserProfileCreateRequest payload);



}
