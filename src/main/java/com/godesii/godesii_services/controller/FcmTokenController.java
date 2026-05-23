package com.godesii.godesii_services.controller;

import com.godesii.godesii_services.common.APIResponse;
import com.godesii.godesii_services.dto.FcmTokenRequest;
import com.godesii.godesii_services.service.FcmTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * REST endpoints for managing FCM device tokens.
 *
 * <p>Mobile apps must call {@code POST /api/v1/fcm/token} on every login
 * and whenever Firebase fires the {@code onTokenRefresh} callback.
 *
 * <h3>Endpoints</h3>
 * <pre>
 *   POST   /api/v1/fcm/token          → Register / refresh a device token
 *   DELETE /api/v1/fcm/token          → Revoke a specific device token (logout)
 *   DELETE /api/v1/fcm/token/all      → Revoke all tokens for the current user
 * </pre>
 */
@RestController
@RequestMapping("/api/v1/fcm")
@Tag(name = "FCM Token Management", description = "Firebase Cloud Messaging device token registration")
public class FcmTokenController {

    private final FcmTokenService fcmTokenService;

    public FcmTokenController(FcmTokenService fcmTokenService) {
        this.fcmTokenService = fcmTokenService;
    }

    /**
     * Register or refresh an FCM device token for the authenticated user.
     *
     * <p>Call this:
     * <ul>
     *   <li>After login (always)</li>
     *   <li>When Firebase calls {@code onTokenRefresh} on the device</li>
     * </ul>
     */
    @PostMapping(value = "/token", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Register FCM token",
            description = "Register or refresh the device's FCM token for push notifications")
    public ResponseEntity<APIResponse<String>> registerToken(
            @AuthenticationPrincipal UserDetails principal,
            @RequestBody FcmTokenRequest request) {

        fcmTokenService.registerToken(principal.getUsername(), request);

        return ResponseEntity.ok(new APIResponse<>(
                HttpStatus.OK,
                "Token registered successfully",
                "FCM token registered"));
    }

    /**
     * Revoke a specific device token (e.g. user logs out of one device).
     *
     * @param token the FCM token to deactivate (passed as a request parameter)
     */
    @DeleteMapping(value = "/token", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Revoke FCM token",
            description = "Deactivate a specific device token — call on logout")
    public ResponseEntity<APIResponse<String>> revokeToken(
            @RequestParam String token) {

        fcmTokenService.revokeToken(token);

        return ResponseEntity.ok(new APIResponse<>(
                HttpStatus.OK,
                "Token revoked",
                "FCM token deactivated"));
    }

    /**
     * Revoke ALL device tokens for the authenticated user (global logout / account disable).
     */
    @DeleteMapping(value = "/token/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Revoke all FCM tokens",
            description = "Deactivate all device tokens for the current user")
    public ResponseEntity<APIResponse<String>> revokeAllTokens(
            @AuthenticationPrincipal UserDetails principal) {

        fcmTokenService.revokeAllTokensForUser(principal.getUsername());

        return ResponseEntity.ok(new APIResponse<>(
                HttpStatus.OK,
                "All tokens revoked",
                "All FCM tokens deactivated"));
    }
}
