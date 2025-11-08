package com.godesii.godesii_services.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(AuthController.ENDPOINT)
public class AuthController {

    public static final String ENDPOINT = "/auth";

    @Autowired
    private AuthService authService;

    @PostMapping(value = "/login")
    public ResponseEntity<String>  getAuthToken(@RequestBody LoginPayload payload) {

        String token = authService.login(payload);

        return ResponseEntity.ok(token);
    }
}
