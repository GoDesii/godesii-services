package com.godesii.godesii_services.controller.auth;

import com.godesii.godesii_services.common.APIResponse;
import com.godesii.godesii_services.constant.GoDesiiConstant;
import com.godesii.godesii_services.dto.UserCreationRequest;
import com.godesii.godesii_services.entity.auth.User;
import com.godesii.godesii_services.service.auth.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping(UserController.ENDPOINT)
public class UserController {

    public static final String ENDPOINT = GoDesiiConstant.API_VERSION + "/users";

    private APIResponse<User> apiResponse;

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<APIResponse<User>> registerUser(@RequestBody UserCreationRequest request){
        this.apiResponse = new APIResponse<>(
                HttpStatus.CREATED,
                userService.createUser(request),
                GoDesiiConstant.SUCCESSFULLY_CREATED);
        return ResponseEntity
                .status(apiResponse.getStatus())
                .build();
    }

    @PostMapping("/mobile/{mobileNo}/register")
    public ResponseEntity<APIResponse<User>> register(@PathVariable("mobileNo") String mobileNo){
        this.apiResponse = new APIResponse<>(
                HttpStatus.CREATED,
                userService.registerMobileUser(mobileNo),
                GoDesiiConstant.SUCCESSFULLY_CREATED);
        return ResponseEntity
                .status(apiResponse.getStatus())
                .build();
    }

}
