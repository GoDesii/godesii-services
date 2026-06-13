package com.godesii.godesii_services.controller.auth;

import com.godesii.godesii_services.dto.LoginPayload;
import com.godesii.godesii_services.service.auth.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAuthToken_shouldReturnOkWithTokenMap() {
        // Arrange
        LoginPayload payload = new LoginPayload();
        payload.setUsername("9876543210");
        payload.setPassword("123456");

        Map<String, Object> tokenMap = new HashMap<>();
        tokenMap.put("access_token", "at");
        tokenMap.put("refresh_token", "rt");
        tokenMap.put("token_type", "Bearer");
        tokenMap.put("expires_in", "360000");

        when(authService.login(payload)).thenReturn(tokenMap);

        // Act
        ResponseEntity<Map<String, Object>> response = authController.getAuthToken(payload);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("at", response.getBody().get("access_token"));
        assertEquals("rt", response.getBody().get("refresh_token"));
        assertEquals("Bearer", response.getBody().get("token_type"));
        verify(authService).login(payload);
    }

    @Test
    void getAuthToken_shouldDelegateToService() {
        LoginPayload payload = new LoginPayload();
        payload.setUsername("user");
        payload.setPassword("pass");

        Map<String, Object> map = Map.of("access_token", "token123");
        when(authService.login(payload)).thenReturn(map);

        ResponseEntity<Map<String, Object>> response = authController.getAuthToken(payload);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("token123", response.getBody().get("access_token"));
    }
}
