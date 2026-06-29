package com.godesii.godesii_services.service.auth;

import com.godesii.godesii_services.security.JwtProvider;
import com.godesii.godesii_services.dto.LoginPayload;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.godesii.godesii_services.security.UserPrincipal;
import org.springframework.security.access.AccessDeniedException;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {


    private final AuthenticationManager authenticationManager;
    private final JwtProvider  jwtProvider;
    private final com.godesii.godesii_services.repository.auth.UserRepository userRepository;

    public AuthService(AuthenticationManager authenticationManager, JwtProvider jwtProvider, com.godesii.godesii_services.repository.auth.UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
        this.userRepository = userRepository;
    }

    public Map<String, Object> login(LoginPayload payload) {
        Map<String, Object> map = new HashMap<>();

        Authentication authentication = this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(payload.getUsername(), payload.getPassword())
        );
        
        if (payload.getRoleType() == null || payload.getRoleType().trim().isEmpty()) {
            throw new org.springframework.security.authentication.BadCredentialsException("roleType is mandatory for login");
        }

        String requestedRole = payload.getRoleType().toUpperCase();
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        
        if (principal.getRoles() == null || !principal.getRoles().contains(requestedRole)) {
            throw new AccessDeniedException("User does not have the requested role: " + requestedRole);
        }

        com.godesii.godesii_services.security.UserPrincipal restrictedPrincipal = 
            UserDetailServiceImpl.buildRestrictedPrincipal(principal, requestedRole);

        authentication = new UsernamePasswordAuthenticationToken(
            restrictedPrincipal, authentication.getCredentials(), restrictedPrincipal.getAuthorities()
        );

        // OTP is successfully validated; clear it from the database
        Long userId = Long.valueOf(((UserPrincipal) authentication.getPrincipal()).getId());
        userRepository.findById(userId).ifPresent(user -> {
            user.setLoginOtp(null);
            userRepository.save(user);
        });

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
