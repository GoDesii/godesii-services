package com.godesii.godesii_services.service.auth;

import com.godesii.godesii_services.entity.auth.Session;
import com.godesii.godesii_services.repository.auth.SessionRepository;
import com.godesii.godesii_services.repository.auth.UserRepository;
import com.godesii.godesii_services.security.JwtProvider;
import com.godesii.godesii_services.dto.LoginPayload;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.godesii.godesii_services.security.UserPrincipal;
import org.springframework.security.access.AccessDeniedException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final com.godesii.godesii_services.repository.auth.UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final HttpServletRequest httpRequest;

    public AuthService(AuthenticationManager authenticationManager,
                       JwtProvider jwtProvider,
                       UserRepository userRepository,
                       SessionRepository sessionRepository,
                       HttpServletRequest httpRequest) {
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.httpRequest = httpRequest;
    }

    @Transactional
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

        // ── Single-session enforcement for VENDOR ───────────────────────────────
        if ("VENDOR".equals(requestedRole)) {
            sessionRepository.findByUsernameAndActiveTrue(payload.getUsername())
                    .ifPresent(existing -> {
                        throw new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "You are already logged in on another device (" +
                                        summariseBrowser(existing.getUserAgent()) + ", " +
                                        existing.getIpAddress() + "). " +
                                        "Please logout from that session first."
                        );
                    });
        }
        // ───────────────────────────────────────────────────────────────

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

        // ── Persist session record for VENDOR ───────────────────────────────────
        if ("VENDOR".equals(requestedRole)) {
            Session session = new Session();
            session.setUserId(userId);
            session.setUsername(payload.getUsername());
            session.setSessionToken(UUID.randomUUID().toString());
            session.setUserAgent(httpRequest.getHeader("User-Agent"));
            session.setIpAddress(resolveClientIp(httpRequest));
            session.setCreatedAt(Instant.now());
            session.setActive(true);
            sessionRepository.save(session);
        }
        // ───────────────────────────────────────────────────────────────

        map.put("access_token", accessToken);
        map.put("refresh_token", refreshToken);
        map.put("token_type", "Bearer");
        map.put("expires_in", jwtProvider.getAccessTokenExpiryTime());

        return map;
    }

    // ── Logout ───────────────────────────────────────────────────────────────

    /**
     * Deactivates the VENDOR's active session so they can log in from another device.
     * Safe to call for non-VENDOR users — it simply finds no sessions to deactivate.
     *
     * @param username the authenticated user's username (from Spring Security context)
     */
    @Transactional
    public void logout(String username) {
        sessionRepository.deactivateAllByUsername(username);
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    /**
     * Returns the real client IP, honouring common reverse-proxy headers.
     */
    private String resolveClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim(); // first IP in chain is the real client
        }
        return request.getRemoteAddr();
    }

    /**
     * Extracts a human-readable browser/device name from the full User-Agent string.
     * e.g. "Chrome on Windows", "Safari on iPhone"
     */
    private String summariseBrowser(String userAgent) {
        if (userAgent == null || userAgent.isBlank()) return "Unknown device";
        if (userAgent.contains("Edg")) return "Edge";
        if (userAgent.contains("Chrome")) return "Chrome";
        if (userAgent.contains("Firefox")) return "Firefox";
        if (userAgent.contains("Safari")) return "Safari";
        if (userAgent.contains("PostmanRuntime")) return "Postman";
        return "Browser";
    }
}
