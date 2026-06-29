package com.godesii.godesii_services.repository.auth;

import com.godesii.godesii_services.entity.auth.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {

    /**
     * Returns the one active session for a given username, if it exists.
     * Used during login to check whether the user is already logged in on another device.
     */
    Optional<Session> findByUsernameAndActiveTrue(String username);

    /**
     * Deactivates all active sessions for a given username.
     * Called on logout, so the user can log in from another device.
     */
    @Modifying
    @Transactional
    @Query("UPDATE Session s SET s.active = false WHERE s.username = :username AND s.active = true")
    void deactivateAllByUsername(@Param("username") String username);

    /**
     * Cleanup query — used by a scheduled job to expire stale sessions.
     * Deactivates all sessions that were created before the given {@code cutoff} time.
     * This ensures a VENDOR can log in again after their token naturally expires,
     * without requiring an explicit logout.
     *
     * @param cutoff sessions created before this time are considered expired
     */
    @Modifying
    @Transactional
    @Query("UPDATE Session s SET s.active = false WHERE s.active = true AND s.createdAt < :cutoff")
    void deactivateExpiredSessions(@Param("cutoff") Instant cutoff);
}
