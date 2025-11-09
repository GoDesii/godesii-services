package com.godesii.godesii_services.controller.auth;

import com.godesii.godesii_services.controller.AuthService;
import com.godesii.godesii_services.dto.LoginPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
@RequestMapping(AuthController.ENDPOINT)
public class AuthController {

    public static final String ENDPOINT = "/auth";

    @Autowired
    private AuthService authService;

    @PostMapping(value = "/login")
    public ResponseEntity<Map<String, Object>>  getAuthToken(@RequestBody LoginPayload payload) {

        Map<String, Object> map = authService.login(payload);
        return ResponseEntity.ok(map);
    }
}
