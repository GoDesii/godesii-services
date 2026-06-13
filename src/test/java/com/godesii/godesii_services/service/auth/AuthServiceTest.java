package com.godesii.godesii_services.service.auth;

import com.godesii.godesii_services.dto.LoginPayload;
import com.godesii.godesii_services.security.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void login_shouldReturnTokenMap() {
        // Arrange
        LoginPayload payload = new LoginPayload();
        payload.setUsername("9876543210");
        payload.setPassword("123456");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtProvider.generateAccessToken(authentication)).thenReturn("access-token-value");
        when(jwtProvider.generateRefreshToken(authentication)).thenReturn("refresh-token-value");
        when(jwtProvider.getAccessTokenExpiryTime()).thenReturn("360000");

        // Act
        Map<String, Object> result = authService.login(payload);

        // Assert
        assertNotNull(result);
        assertEquals("access-token-value", result.get("access_token"));
        assertEquals("refresh-token-value", result.get("refresh_token"));
        assertEquals("Bearer", result.get("token_type"));
        assertEquals("360000", result.get("expires_in"));

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtProvider).generateAccessToken(authentication);
        verify(jwtProvider).generateRefreshToken(authentication);
        verify(jwtProvider).getAccessTokenExpiryTime();
    }

    @Test
    void login_shouldAuthenticateWithCorrectCredentials() {
        // Arrange
        LoginPayload payload = new LoginPayload();
        payload.setUsername("testuser");
        payload.setPassword("testpass");

        ArgumentCaptor<UsernamePasswordAuthenticationToken> captor =
                ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);

        when(authenticationManager.authenticate(captor.capture())).thenReturn(authentication);
        when(jwtProvider.generateAccessToken(any())).thenReturn("token");
        when(jwtProvider.generateRefreshToken(any())).thenReturn("refresh");
        when(jwtProvider.getAccessTokenExpiryTime()).thenReturn("3600");

        // Act
        authService.login(payload);

        // Assert
        UsernamePasswordAuthenticationToken captured = captor.getValue();
        assertEquals("testuser", captured.getPrincipal());
        assertEquals("testpass", captured.getCredentials());
    }

    @Test
    void login_shouldSetSecurityContext() {
        // Arrange
        LoginPayload payload = new LoginPayload();
        payload.setUsername("user");
        payload.setPassword("pass");

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtProvider.generateAccessToken(any())).thenReturn("at");
        when(jwtProvider.generateRefreshToken(any())).thenReturn("rt");
        when(jwtProvider.getAccessTokenExpiryTime()).thenReturn("100");

        // Act
        Map<String, Object> result = authService.login(payload);

        // Assert — map always has exactly 4 keys
        assertEquals(4, result.size());
        assertTrue(result.containsKey("access_token"));
        assertTrue(result.containsKey("refresh_token"));
        assertTrue(result.containsKey("token_type"));
        assertTrue(result.containsKey("expires_in"));
    }
}
