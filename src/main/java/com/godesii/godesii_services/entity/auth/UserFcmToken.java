package com.godesii.godesii_services.entity.auth;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * Stores the Firebase Cloud Messaging (FCM) device tokens for a user.
 *
 * <p>A single user may have multiple devices (Android phone + tablet, etc.).
 * Each row represents one registered device token.
 *
 * <p>Tokens are automatically revoked when:
 * <ul>
 *   <li>The user logs out of a device (client sends a DELETE request).</li>
 *   <li>FCM reports the token as invalid (handled in {@code FcmService}).</li>
 * </ul>
 *
 * <p>DB table: {@code user_fcm_tokens}
 */
@Entity
@Table(name = "user_fcm_tokens",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_user_fcm_token",
                columnNames = {"username", "fcm_token"}))
public class UserFcmToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The application username — matches {@link User#getUsername()}.
     */
    @Column(name = "username", nullable = false, length = 100)
    private String username;

    /**
     * The FCM registration token sent by the mobile app.
     * Tokens are typically ~160 chars, 512 is a safe limit that allows unique indexing.
     */
    @Column(name = "fcm_token", nullable = false, length = 512)
    private String fcmToken;

    /**
     * Free-form label for the device (e.g. "Shashank's Pixel 8").
     * Populated by the mobile client, optional.
     */
    @Column(name = "device_label", length = 100)
    private String deviceLabel;

    /**
     * Platform/OS — helps with analytics and platform-specific payloads.
     * E.g. "ANDROID", "IOS"
     */
    @Column(name = "platform", length = 20)
    private String platform;

    /**
     * When this token was first registered.
     */
    @Column(name = "registered_at", nullable = false)
    private Instant registeredAt;

    /**
     * Last time the app refreshed this token.
     * FCM tokens can rotate — the client must call the register endpoint again when this happens.
     */
    @Column(name = "last_refreshed_at")
    private Instant lastRefreshedAt;

    /**
     * Whether this token is currently considered active.
     * Set to {@code false} when FCM returns UNREGISTERED or INVALID_ARGUMENT errors.
     */
    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFcmToken() { return fcmToken; }
    public void setFcmToken(String fcmToken) { this.fcmToken = fcmToken; }

    public String getDeviceLabel() { return deviceLabel; }
    public void setDeviceLabel(String deviceLabel) { this.deviceLabel = deviceLabel; }

    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }

    public Instant getRegisteredAt() { return registeredAt; }
    public void setRegisteredAt(Instant registeredAt) { this.registeredAt = registeredAt; }

    public Instant getLastRefreshedAt() { return lastRefreshedAt; }
    public void setLastRefreshedAt(Instant lastRefreshedAt) { this.lastRefreshedAt = lastRefreshedAt; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
