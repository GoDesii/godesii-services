//package com.godesii.godesii_services.controller.auth;
//
//import com.godesii.godesii_services.common.APIResponse;
//import com.godesii.godesii_services.constant.GoDesiiConstant;
//import com.godesii.godesii_services.dto.ShippingAddressCreateRequest;
//import com.godesii.godesii_services.dto.UserProfileCreateRequest;
//import com.godesii.godesii_services.service.ProfileService;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping(UserProfileController.ENDPOINT)
//public class UserProfileController {
//
//        public static final String ENDPOINT = GoDesiiConstant.API_VERSION + "/user/profile";
//
//        private final ProfileService profileService;
//
//        public UserProfileController(ProfileService profileService) {
//                this.profileService = profileService;
//        }
//
//        @PostMapping(
//                value = "/id/{userId}/create",
//                produces = MediaType.APPLICATION_JSON_VALUE,
//                consumes = MediaType.APPLICATION_JSON_VALUE
//        )
//        public ResponseEntity<APIResponse<UserProfile>> createProfile(
//                        @RequestBody UserProfileCreateRequest payload,
//                        @PathVariable(name = "userId") Long userId) {
//                APIResponse<UserProfile> apiResponse = new APIResponse<>(
//                        HttpStatus.CREATED,
//                        this.profileService.createNewProfile(payload, userId),
//                        GoDesiiConstant.SUCCESSFULLY_CREATED
//                );
//                return ResponseEntity
//                                .status(apiResponse.getStatus())
//                                .body(apiResponse);
//        }
//
//        @PostMapping(
//                value = "/id/{profileId}/address/create",
//                produces = MediaType.APPLICATION_JSON_VALUE,
//                consumes = MediaType.APPLICATION_JSON_VALUE
//        )
//        public ResponseEntity<APIResponse<UserProfile>> createAddress(@RequestBody ShippingAddressCreateRequest request,
//                                                                      @PathVariable(name = "profileId") Long profileId) {
//            APIResponse<UserProfile> apiResponse = new APIResponse<>(
//                    HttpStatus.CREATED,
//                    this.profileService.addNewAddress(request, profileId),
//                    GoDesiiConstant.SUCCESSFULLY_CREATED
//            );
//            return ResponseEntity
//                    .status(apiResponse.getStatus())
//                    .body(apiResponse);
//        }
//
//
//}
