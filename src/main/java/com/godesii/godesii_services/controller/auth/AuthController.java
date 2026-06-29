package com.godesii.godesii_services.controller.auth;

import com.godesii.godesii_services.constant.GoDesiiConstant;
import com.godesii.godesii_services.dto.LoginPayload;
import com.godesii.godesii_services.service.auth.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping(AuthController.ENDPOINT)
public class AuthController {

    public static final String ENDPOINT = GoDesiiConstant.API_VERSION +"/auth";

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(value = "/login")
    public ResponseEntity<Map<String, Object>>  getAuthToken(@RequestBody LoginPayload payload) {
        Map<String, Object> map = authService.login(payload);
        return ResponseEntity.ok(map);
    }

    /**
     * Logout endpoint — invalidates the VENDOR's active session in the DB.
     *
     * <p>The client must include their JWT in the {@code Authorization: Bearer <token>} header.
     * Spring Security resolves the principal automatically.
     *
     * <p>Safe to call for any role; non-VENDOR users simply have no session to deactivate.
     *
     * <p>POST /api/v1/auth/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(
            @AuthenticationPrincipal UserDetails userDetails) {

        authService.logout(userDetails.getUsername());
        return ResponseEntity.ok(Map.of(
                "message", "Logged out successfully. You may now login from another device."
        ));
    }

}
