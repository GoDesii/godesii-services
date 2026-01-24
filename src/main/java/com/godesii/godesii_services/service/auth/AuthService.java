package com.godesii.godesii_services.service.auth;

import com.godesii.godesii_services.dto.LoginPayload;
import com.godesii.godesii_services.dto.auth.*;
import com.godesii.godesii_services.entity.auth.User;
import com.godesii.godesii_services.repository.auth.UserRepository;
import com.godesii.godesii_services.security.JwtProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Service for handling authentication operations
 */
@Service
public class AuthService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final OtpService otpService;

    public AuthService(AuthenticationManager authenticationManager,
            JwtProvider jwtProvider,
            UserRepository userRepository,
            OtpService otpService) {
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
        this.userRepository = userRepository;
        this.otpService = otpService;
    }

    /**
     * Login with username/mobile and password (legacy/existing method)
     */
    public Map<String, Object> login(LoginPayload payload) {
        LOGGER.info("Password login attempt for user: {}", payload.getUsername());

        Authentication authentication = this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(payload.getUsername(), payload.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Update last login time
        updateLastLogin(payload.getUsername());

        return generateTokenResponse(authentication);
    }

    /**
     * Login with password (supports mobile or username)
     */
    @Transactional
    public AuthResponse loginWithPassword(PasswordLoginRequest request) {
        String identifier = request.getIdentifier();
        LOGGER.info("Password login attempt for identifier: {}", identifier);

        // Determine if identifier is mobile or username
        User user;
        if (request.getMobileNo() != null) {
            user = userRepository.findByMobileNo(request.getMobileNo())
                    .orElseThrow(
                            () -> new IllegalArgumentException("User not found with mobile: " + request.getMobileNo()));
            identifier = user.getUsername();
        } else {
            user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "User not found with username: " + request.getUsername()));
        }

        // Authenticate
        Authentication authentication = this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(identifier, request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Update last login time
        user.setLastLoggedIn(new Date());
        userRepository.save(user);

        LOGGER.info("Password login successful for user: {}", user.getId());

        return buildAuthResponse(authentication, user);
    }

    /**
     * Request OTP for login
     */
    @Transactional
    public Map<String, String> requestLoginOtp(OtpGenerationRequest request) {
        LOGGER.info("OTP generation requested for mobile: {}", request.getMobileNo());

        Optional<User> userOptional = userRepository.findByMobileNo(request.getMobileNo());
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found with mobile number: " + request.getMobileNo());
        }

        // Generate and send OTP
        otpService.generateAndSendOtp(request.getMobileNo());

        Map<String, String> response = new HashMap<>();
        response.put("message", "OTP sent successfully to " + request.getMobileNo());
        response.put("mobile", request.getMobileNo());
        response.put("expiresIn", "5 minutes");

        LOGGER.info("OTP sent successfully to mobile: {}", request.getMobileNo());

        return response;
    }

    /**
     * Login with OTP
     */
    @Transactional
    public AuthResponse loginWithOtp(OtpLoginRequest request) {
        LOGGER.info("OTP login attempt for mobile: {}", request.getMobileNo());

        // Validate OTP
        boolean isValid = otpService.validateOtp(request.getMobileNo(), request.getOtp());
        if (!isValid) {
            throw new IllegalArgumentException("Invalid or expired OTP");
        }

        // Find user
        User user = userRepository.findByMobileNo(request.getMobileNo())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Create authentication object
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getUsername(),
                null,
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name())));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Update last login time
        user.setLastLoggedIn(new Date());
        userRepository.save(user);

        LOGGER.info("OTP login successful for user: {}", user.getId());

        return buildAuthResponse(authentication, user);
    }

    /**
     * Generate token response (legacy format)
     */
    private Map<String, Object> generateTokenResponse(Authentication authentication) {
        Map<String, Object> map = new HashMap<>();
        String accessToken = jwtProvider.generateAccessToken(authentication);
        String refreshToken = jwtProvider.generateRefreshToken(authentication);

        map.put("access_token", accessToken);
        map.put("refresh_token", refreshToken);
        map.put("token_type", "Bearer");
        map.put("expires_in", jwtProvider.getAccessTokenExpiryTime());

        return map;
    }

    /**
     * Build AuthResponse with user information
     */
    private AuthResponse buildAuthResponse(Authentication authentication, User user) {
        String accessToken = jwtProvider.generateAccessToken(authentication);
        String refreshToken = jwtProvider.generateRefreshToken(authentication);

        // Build user info
        AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setMobileNo(user.getMobileNo());
        userInfo.setCountryCode(user.getCountryCode());
        userInfo.setRole(user.getRole());
        userInfo.setFirstName(user.getFirstName());
        userInfo.setLastName(user.getLastName());
        userInfo.setEmailId(user.getEmailId());
        userInfo.setMobileVerified(user.getMobileNoVerified());
        userInfo.setEmailVerified(user.getEmailVerified());

        // Build response
        AuthResponse response = new AuthResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setTokenType("Bearer");
        response.setExpiresIn(jwtProvider.getAccessTokenExpiryTime());
        response.setUser(userInfo);

        return response;
    }

    /**
     * Update last login time for a user
     */
    private void updateLastLogin(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setLastLoggedIn(new Date());
            userRepository.save(user);
        }
    }
}
