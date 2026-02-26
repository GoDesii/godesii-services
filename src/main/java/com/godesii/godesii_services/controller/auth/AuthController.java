package com.godesii.godesii_services.controller.auth;

import com.godesii.godesii_services.constant.GoDesiiConstant;
import com.godesii.godesii_services.dto.LoginPayload;
import com.godesii.godesii_services.service.auth.AuthService;
import org.springframework.http.ResponseEntity;
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

}
