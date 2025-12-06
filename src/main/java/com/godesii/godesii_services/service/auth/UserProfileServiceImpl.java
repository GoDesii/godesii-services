//package com.godesii.godesii_services.service.auth;
//
//import com.godesii.godesii_services.dto.ShippingAddressCreateRequest;
//import com.godesii.godesii_services.dto.UserProfileCreateRequest;
//import com.godesii.godesii_services.entity.auth.Address;
//import com.godesii.godesii_services.entity.auth.Gender;
//import com.godesii.godesii_services.entity.auth.User;
//import com.godesii.godesii_services.entity.auth.UserProfile;
//import com.godesii.godesii_services.exception.ResourceNotFoundException;
//import com.godesii.godesii_services.repository.auth.UserProfileRepository;
//import com.godesii.godesii_services.repository.auth.UserRepository;
//import com.godesii.godesii_services.service.ProfileService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.ArrayList;
//
//@Service
//@Transactional
//public class UserProfileServiceImpl implements ProfileService {
//
//    public static final Logger LOGGER = LoggerFactory.getLogger(UserProfileServiceImpl.class);
//
//    private final UserProfileRepository userProfileRepository;
//    private final UserRepository userRepository;
//
//    public UserProfileServiceImpl(UserProfileRepository userProfileRepository,
//                                  UserRepository userRepository) {
//        this.userProfileRepository = userProfileRepository;
//        this.userRepository = userRepository;
//    }
//
//    @Override
//    public UserProfile createNewProfile(UserProfileCreateRequest request, Long useId) {
//
//        User user = this.userRepository.findById(useId)
//                .orElseThrow(() -> {
//                    LOGGER.error("User not found with id {}", useId);
//                    return new ResourceNotFoundException("User not found with id: " + useId);
//                });
//
//        UserProfile userProfile = user.getUserProfile();
//        if (userProfile == null) {
//            userProfile = new UserProfile();
//            userProfile.setUser(user);
//            user.setUserProfile(userProfile);
//        }
//
//        updateProfileDetails(userProfile, request);
//
//        return this.userProfileRepository.save(userProfile);
//    }
//
//    @Override
//    public UserProfile addNewAddress(ShippingAddressCreateRequest request, Long profileId) {
//
//        UserProfile userProfile = this.userProfileRepository.findById(profileId).orElseThrow(
//                () -> new ResourceNotFoundException("User not found with id " + profileId)
//        );
////        userProfile.getAddresses().add(getAddress(request));
//
//        return this.userProfileRepository.save(userProfile);
//    }
//
//
//
//
//    private static void updateProfileDetails(UserProfile userProfile, UserProfileCreateRequest request) {
//        userProfile.setFirstName(request.getFirstName());
//        userProfile.setLastName(request.getLastName());
//        userProfile.setMiddleName(request.getMiddleName());
//        userProfile.setGender(Gender.FEMALE);
//        // Assuming Gender is part of the request, if not, handle accordingly.
//        // For now, removing hardcoded MALE and leaving it null if not provided, or
//        // mapping if available.
//        // If request has gender field: userProfile.setGender(request.getGender());
//    }
//}
