package com.godesii.godesii_services.controller.auth;

import com.godesii.godesii_services.dto.LoginPayload;
import com.godesii.godesii_services.dto.auth.AuthResponse;
import com.godesii.godesii_services.dto.auth.OtpGenerationRequest;
import com.godesii.godesii_services.dto.auth.OtpLoginRequest;
import com.godesii.godesii_services.dto.auth.PasswordLoginRequest;
import com.godesii.godesii_services.service.auth.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller for authentication operations (login, OTP, token management)
 */
@RestController
@RequestMapping(AuthController.ENDPOINT)
public class AuthController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    public static final String ENDPOINT = "/auth";

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Legacy login endpoint (supports username/password)
     * Kept for backward compatibility
     */
    @PostMapping(value = "/login")
    public ResponseEntity<Map<String, Object>> getAuthToken(@RequestBody LoginPayload payload) {
        LOGGER.info("Legacy login attempt for user: {}", payload.getUsername());
        Map<String, Object> map = authService.login(payload);
        return ResponseEntity.ok(map);
    }

    /**
     * Login with password (mobile or username)
     */
    @PostMapping("/login/password")
    public ResponseEntity<AuthResponse> loginWithPassword(
            @Valid @RequestBody PasswordLoginRequest request) {
        LOGGER.info("Password login attempt for identifier: {}",
                request.getIdentifier());

        AuthResponse response = authService.loginWithPassword(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Request OTP for login
     * First step in OTP-based login flow
     */
    @PostMapping("/otp/generate")
    public ResponseEntity<Map<String, String>> requestLoginOtp(
            @Valid @RequestBody OtpGenerationRequest request) {
        LOGGER.info("OTP generation requested for mobile: {}", request.getMobileNo());

        Map<String, String> response = authService.requestLoginOtp(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Login with OTP
     * Second step in OTP-based login flow
     */
    @PostMapping("/login/otp")
    public ResponseEntity<AuthResponse> loginWithOtp(
            @Valid @RequestBody OtpLoginRequest request) {
        LOGGER.info("OTP login attempt for mobile: {}", request.getMobileNo());

        AuthResponse response = authService.loginWithOtp(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Alternative OTP generation endpoint (more RESTful path)
     */
    @PostMapping("/login/otp/request")
    public ResponseEntity<Map<String, String>> requestOtpForLogin(
            @Valid @RequestBody OtpGenerationRequest request) {
        LOGGER.info("OTP generation requested (alternative endpoint) for mobile: {}",
                request.getMobileNo());

        Map<String, String> response = authService.requestLoginOtp(request);
        return ResponseEntity.ok(response);
    }
}
