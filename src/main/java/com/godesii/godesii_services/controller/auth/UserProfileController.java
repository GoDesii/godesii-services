package com.godesii.godesii_services.controller.auth;

import com.godesii.godesii_services.constant.GoDesiiConstant;
import com.godesii.godesii_services.dto.UserProfileCreateRequest;
import com.godesii.godesii_services.entity.auth.UserProfile;
import com.godesii.godesii_services.repository.auth.UserProfileRepository;
import com.godesii.godesii_services.service.ProfileService;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(UserProfileController.ENDPOINT)
public class UserProfileController {

    public static final String ENDPOINT = GoDesiiConstant.API_VERSION + "/user/profile";

    private final ProfileService profileService;

    public UserProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @PostMapping(
            value = "/create",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
//    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserProfile> createProfile(@RequestBody UserProfileCreateRequest payload) {
        this.profileService.saveOrUpdateProfile(payload);
        return null;
    }

}
