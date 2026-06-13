package com.godesii.godesii_services.controller.auth;

import com.godesii.godesii_services.common.APIResponse;
import com.godesii.godesii_services.dto.MobileUserCreationRequest;
import com.godesii.godesii_services.dto.UserProfileCreateRequest;
import com.godesii.godesii_services.dto.UserProfileCreateResponse;
import com.godesii.godesii_services.service.auth.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ── register ─────────────────────────────────────────────────────────────

    @Test
    void register_shouldReturn201WithOTP() {
        MobileUserCreationRequest request = new MobileUserCreationRequest();
        request.setMobile("9876543210");
        request.setRoleType("CUSTOMER");

        when(userService.registerMobileUser(request)).thenReturn("123456");

        ResponseEntity<Object> response = userController.register(request);

        assertEquals(HttpStatus.CREATED.value(), response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertInstanceOf(APIResponse.class, response.getBody());

        @SuppressWarnings("unchecked")
        APIResponse<String> apiResponse = (APIResponse<String>) response.getBody();
        assertEquals("123456", apiResponse.getData());
        assertEquals("Successfully Created", apiResponse.getMessage());
    }

    // ── updateProfile ────────────────────────────────────────────────────────

    @Test
    void updateProfile_shouldReturn200WithResponse() {
        UserProfileCreateRequest request = new UserProfileCreateRequest();
        request.setUserId("john");
        request.setFirstName("John");

        UserProfileCreateResponse profileResponse = new UserProfileCreateResponse();
        profileResponse.setFirstName("John");

        when(userService.updateProfile(request)).thenReturn(profileResponse);

        ResponseEntity<APIResponse<UserProfileCreateResponse>> response =
                userController.updateProfile(request);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("John", response.getBody().getData().getFirstName());
        assertEquals("Successfully Updated", response.getBody().getMessage());
    }
}
