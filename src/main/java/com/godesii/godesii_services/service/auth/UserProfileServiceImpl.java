package com.godesii.godesii_services.service.auth;

import com.godesii.godesii_services.dto.UserProfileCreateRequest;
import com.godesii.godesii_services.dto.UserProfileCreateResponse;
import com.godesii.godesii_services.entity.auth.Gender;
import com.godesii.godesii_services.entity.auth.User;
import com.godesii.godesii_services.entity.auth.UserProfile;
import com.godesii.godesii_services.exception.ResourceNotFoundException;
import com.godesii.godesii_services.repository.auth.UserProfileRepository;
import com.godesii.godesii_services.repository.auth.UserRepository;
import com.godesii.godesii_services.service.ProfileService;

public final class UserProfileServiceImpl implements ProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;


    public UserProfileServiceImpl(final UserProfileRepository userProfileRepository, UserRepository userRepository) {
        this.userProfileRepository = userProfileRepository;
        this.userRepository = userRepository;
    }


    @Override
    public UserProfileCreateResponse saveOrUpdateProfile(UserProfileCreateRequest request) {
        User user = this.userRepository.findById(Long.parseLong(request.getUserId()))
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UserProfile userProfile = new UserProfile();
        userProfile.setFirstName(request.getFirstName());
        userProfile.setLastName(request.getLastName());
        userProfile.setGender(Gender.valueOf(request.getGender()));
        userProfile.setMiddleName(request.getMiddleName());
        userProfile.setUser(user);
        user.setUserProfile(userProfile);

        return UserProfileCreateResponse.mapToResponse(this.userProfileRepository.save(userProfile));
    }

}
