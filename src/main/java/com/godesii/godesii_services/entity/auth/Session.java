package com.godesii.godesii_services.entity.auth;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * Tracks an active login session for a single-session-per-user enforcement.
 *
 * <p>Currently enforced for the <b>VENDOR</b> role only:
 * a VENDOR must log out from the current session before logging in on another device.
 *
 * <p>Stores browser and IP details so the user/admin can see which device holds the session.
 *
 * <p>DB table: {@code sessions}
 */
@Entity
@Table(
    name = "sessions",
    indexes = {
        @Index(name = "idx_session_username_active", columnList = "username, active")
    }
)
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The DB user ID — FK to {@code user.id}.
     * Stored as a plain Long (not a JPA relationship) to keep this entity lightweight.
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /** Denormalized username for fast lookup without a join. */
    @Column(name = "username", nullable = false, length = 100)
    private String username;

    /**
     * A random UUID generated at login time.
     * Uniquely identifies this specific session — can be embedded in the JWT if needed.
     */
    @Column(name = "session_token", nullable = false, unique = true, length = 36)
    private String sessionToken;

    /** The {@code User-Agent} header value sent by the browser or mobile app at login. */
    @Column(name = "user_agent", length = 512)
    private String userAgent;

    /** The remote IP address of the client at login. */
    @Column(name = "ip_address", length = 45)  // 45 chars covers IPv6
    private String ipAddress;

    /** Timestamp when this session was created (= login time). */
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    /**
     * {@code true}  = this is the current active session.
     * {@code false} = the user has logged out (or session was invalidated).
     */
    @Column(name = "active", nullable = false)
    private boolean active;

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public Long getId() { return id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getSessionToken() { return sessionToken; }
    public void setSessionToken(String sessionToken) { this.sessionToken = sessionToken; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
