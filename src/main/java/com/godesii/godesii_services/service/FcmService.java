package com.godesii.godesii_services.service;

import com.google.firebase.messaging.*;
import com.godesii.godesii_services.entity.auth.UserFcmToken;
import com.godesii.godesii_services.repository.auth.UserFcmTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Core service for sending Firebase Cloud Messaging (FCM) push notifications.
 *
 * <h3>Notification targets</h3>
 * <ul>
 *   <li><b>Single token</b> — {@link #sendToToken} — one specific device</li>
 *   <li><b>Multiple tokens (multicast)</b> — {@link #sendToUser} — all devices of a user</li>
 *   <li><b>Topic</b> — {@link #sendToTopic} — e.g. "restaurant-{restaurantId}"</li>
 * </ul>
 *
 * <h3>Token lifecycle</h3>
 * When FCM returns {@code UNREGISTERED} or {@code INVALID_ARGUMENT} for a token,
 * this service automatically deactivates it in the database so it is never used again.
 */
@Service
public class FcmService {

    private static final Logger log = LoggerFactory.getLogger(FcmService.class);

    private final UserFcmTokenRepository tokenRepo;

    public FcmService(UserFcmTokenRepository tokenRepo) {
        this.tokenRepo = tokenRepo;
    }

    // ── Single token ──────────────────────────────────────────────────────────

    /**
     * Send a notification to a single FCM device token.
     *
     * @param token       FCM registration token
     * @param title       Notification title (shown in system tray)
     * @param body        Notification body text
     * @param data        Optional key-value payload for the app to handle silently
     */
    public void sendToToken(String token, String title, String body, Map<String, String> data) {
        Message.Builder builder = Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build());

        if (data != null && !data.isEmpty()) {
            builder.putAllData(data);
        }

        try {
            String messageId = FirebaseMessaging.getInstance().send(builder.build());
            log.info("FCM sent to token …{} → messageId: {}", token.substring(Math.max(0, token.length() - 8)), messageId);
        } catch (FirebaseMessagingException e) {
            log.error("FCM send failed for token …{}: {} ({})",
                    token.substring(Math.max(0, token.length() - 8)),
                    e.getMessage(), e.getMessagingErrorCode());
            handleTokenError(token, e);
        }
    }

    // ── Multicast (all devices of one user) ───────────────────────────────────

    /**
     * Send a notification to ALL active devices registered for a given username.
     * Uses FCM multicast for efficiency (single HTTP call for up to 500 tokens).
     *
     * @param username  App username (matches {@code user_fcm_tokens.username})
     * @param title     Notification title
     * @param body      Notification body
     * @param data      Optional silent data payload
     */
    public void sendToUser(String username, String title, String body, Map<String, String> data) {
        List<UserFcmToken> tokens = tokenRepo.findByUsernameAndActiveTrue(username);
        if (tokens.isEmpty()) {
            log.debug("No active FCM tokens for user '{}'", username);
            return;
        }

        List<String> tokenStrings = tokens.stream()
                .map(UserFcmToken::getFcmToken)
                .toList();
        log.info("FCM token {}",tokenStrings);
        sendMulticast(tokenStrings, title, body, data);
    }

    // ── Topic ─────────────────────────────────────────────────────────────────

    /**
     * Send a notification to an FCM topic.
     *
     * <p>Topics to use in this app:
     * <ul>
     *   <li>{@code restaurant-{restaurantId}} — all staff devices for a restaurant</li>
     *   <li>{@code delivery-partner-{partnerId}} — all devices of a partner</li>
     * </ul>
     *
     * @param topic  FCM topic name (no leading slash, e.g. "delivery-partner-abc123")
     * @param title  Notification title
     * @param body   Notification body
     * @param data   Optional silent data payload
     */
    public void sendToTopic(String topic, String title, String body, Map<String, String> data) {
        Message.Builder builder = Message.builder()
                .setTopic(topic)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build());

        if (data != null && !data.isEmpty()) {
            builder.putAllData(data);
        }

        try {
            String messageId = FirebaseMessaging.getInstance().send(builder.build());
            log.info("FCM sent to topic '{}' → messageId: {}", topic, messageId);
        } catch (FirebaseMessagingException e) {
            log.error("FCM send to topic '{}' failed: {}", topic, e.getMessage(), e);
        }
    }

    // ── Topic subscription management ─────────────────────────────────────────

    /**
     * Subscribe a device token to an FCM topic.
     * Call this when a delivery partner logs in so their token gets the topic feed.
     *
     * @param token  FCM registration token
     * @param topic  FCM topic name (e.g. "delivery-partner-abc123")
     */
    public void subscribeToTopic(String token, String topic) {
        try {
            TopicManagementResponse response =
                    FirebaseMessaging.getInstance().subscribeToTopic(List.of(token), topic);
            log.info("Subscribed token to topic '{}' — success: {}, fail: {}",
                    topic, response.getSuccessCount(), response.getFailureCount());
        } catch (FirebaseMessagingException e) {
            log.error("Failed to subscribe token to topic '{}': {}", topic, e.getMessage(), e);
        }
    }

    /**
     * Unsubscribe a device token from an FCM topic (e.g. on logout).
     */
    public void unsubscribeFromTopic(String token, String topic) {
        try {
            TopicManagementResponse response =
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(List.of(token), topic);
            log.info("Unsubscribed token from topic '{}' — success: {}, fail: {}",
                    topic, response.getSuccessCount(), response.getFailureCount());
        } catch (FirebaseMessagingException e) {
            log.error("Failed to unsubscribe token from topic '{}': {}", topic, e.getMessage(), e);
        }
    }

    // ── Internal helpers ──────────────────────────────────────────────────────

    private void sendMulticast(List<String> tokens, String title, String body,
                               Map<String, String> data) {
        MulticastMessage.Builder builder = MulticastMessage.builder()
                .addAllTokens(tokens)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build());

        if (data != null && !data.isEmpty()) {
            builder.putAllData(data);
        }

        try {
            BatchResponse response = FirebaseMessaging.getInstance()
                    .sendEachForMulticast(builder.build());

            log.info("FCM multicast to {} tokens — success: {}, failure: {}",
                    tokens.size(), response.getSuccessCount(), response.getFailureCount());

            // Deactivate any invalid tokens
            if (response.getFailureCount() > 0) {
                List<SendResponse> responses = response.getResponses();
                List<String> failedTokens = new ArrayList<>();
                for (int i = 0; i < responses.size(); i++) {
                    if (!responses.get(i).isSuccessful()) {
                        failedTokens.add(tokens.get(i));
                        handleTokenError(tokens.get(i), responses.get(i).getException());
                    }
                }
                log.warn("Deactivated {} invalid FCM tokens", failedTokens.size());
            }
        } catch (FirebaseMessagingException e) {
            log.error("FCM multicast send failed: {}", e.getMessage(), e);
        }
    }

    /**
     * Inspect the FCM error code and deactivate the token in the DB
     * if FCM says it is permanently invalid.
     */
    private void handleTokenError(String token, FirebaseMessagingException e) {
        if (e == null) return;
        MessagingErrorCode code = e.getMessagingErrorCode();
        if (code == MessagingErrorCode.UNREGISTERED
                || code == MessagingErrorCode.INVALID_ARGUMENT) {
            log.warn("Deactivating invalid FCM token due to error code: {}", code);
            tokenRepo.deactivateToken(token);
        }
    }
}
