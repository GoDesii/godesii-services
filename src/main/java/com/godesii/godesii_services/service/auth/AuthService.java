package com.godesii.godesii_services.service.auth;

import com.godesii.godesii_services.security.JwtProvider;
import com.godesii.godesii_services.dto.LoginPayload;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {


    private final AuthenticationManager authenticationManager;
    private final JwtProvider  jwtProvider;

    public AuthService(AuthenticationManager authenticationManager, JwtProvider jwtProvider) {
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
    }

    public Map<String, Object> login(LoginPayload payload) {
        Map<String, Object> map = new HashMap<>();

        Authentication authentication = this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(payload.getUsername(), payload.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String accessToken = jwtProvider.generateAccessToken(authentication);
        String refreshToken = jwtProvider.generateRefreshToken(authentication);

        map.put("access_token", accessToken);
        map.put("refresh_token", refreshToken);
        map.put("token_type", "Bearer");
        map.put("expires_in", jwtProvider.getAccessTokenExpiryTime());

        return map;

    }
}
