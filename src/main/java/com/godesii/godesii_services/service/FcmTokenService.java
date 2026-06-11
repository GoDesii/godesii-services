package com.godesii.godesii_services.service;

import com.godesii.godesii_services.dto.FcmTokenRequest;
import com.godesii.godesii_services.entity.auth.UserFcmToken;
import com.godesii.godesii_services.repository.auth.UserFcmTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

/**
 * Manages FCM device token registration and revocation.
 *
 * <p>Mobile apps call the registration endpoint whenever:
 * <ul>
 *   <li>The user logs in for the first time on a device.</li>
 *   <li>Firebase refreshes the token (onTokenRefresh callback).</li>
 * </ul>
 */
@Service
public class FcmTokenService {

    private static final Logger log = LoggerFactory.getLogger(FcmTokenService.class);

    private final UserFcmTokenRepository tokenRepo;
    private final FcmService fcmService;

    public FcmTokenService(UserFcmTokenRepository tokenRepo, FcmService fcmService) {
        this.tokenRepo  = tokenRepo;
        this.fcmService = fcmService;
    }

    /**
     * Register or refresh an FCM token for a user.
     *
     * <p>If the token already exists for this user it is re-activated and its
     * {@code lastRefreshedAt} timestamp is updated. Otherwise a new row is inserted.
     *
     * <p>If a {@code topic} is present in the request, the token is also subscribed
     * to that FCM topic (e.g. "delivery-partner-{partnerId}").
     *
     * @param username app username (from the authenticated JWT principal)
     * @param request  payload containing token, device info, and optional topic
     * @return the saved entity
     */
    @Transactional
    public UserFcmToken registerToken(String username, FcmTokenRequest request) {
        Optional<UserFcmToken> existing =
                tokenRepo.findByUsernameAndFcmToken(username, request.getFcmToken());

        UserFcmToken tokenEntity = existing.orElseGet(UserFcmToken::new);

        if (tokenEntity.getId() == null) {
            // New token
            tokenEntity.setUsername(username);
            tokenEntity.setFcmToken(request.getFcmToken());
            tokenEntity.setRegisteredAt(Instant.now());
            log.info("Registering new FCM token for user '{}' on device '{}'",
                    username, request.getDeviceLabel());
        } else {
            log.info("Refreshing existing FCM token for user '{}' on device '{}'",
                    username, request.getDeviceLabel());
        }

        tokenEntity.setDeviceLabel(request.getDeviceLabel());
        tokenEntity.setPlatform(request.getPlatform());
        tokenEntity.setLastRefreshedAt(Instant.now());
        tokenEntity.setActive(true);

        UserFcmToken saved = tokenRepo.save(tokenEntity);

        // Subscribe to topic if requested (e.g. delivery-partner topic)
        if (request.getTopic() != null && !request.getTopic().isBlank()) {
            fcmService.subscribeToTopic(request.getFcmToken(), request.getTopic());
        }

        return saved;
    }

    /**
     * Deactivate a specific token (e.g. when the user logs out of a device).
     *
     * @param token FCM registration token to deactivate
     */
    @Transactional
    public void revokeToken(String token) {
        log.info("Revoking FCM token …{}", token.substring(Math.max(0, token.length() - 8)));
        tokenRepo.deactivateToken(token);
    }

    /**
     * Deactivate all tokens for a user (e.g. account disabled or global logout).
     *
     * @param username app username
     */
    @Transactional
    public void revokeAllTokensForUser(String username) {
        log.info("Revoking all FCM tokens for user '{}'", username);
        tokenRepo.deactivateAllForUser(username);
    }
}
