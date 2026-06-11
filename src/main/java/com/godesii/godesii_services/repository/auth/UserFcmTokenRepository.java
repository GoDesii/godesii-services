package com.godesii.godesii_services.repository.auth;

import com.godesii.godesii_services.entity.auth.UserFcmToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Repository for FCM device tokens.
 */
@Repository
public interface UserFcmTokenRepository extends JpaRepository<UserFcmToken, Long> {

    /**
     * All active tokens for a user — used when broadcasting push notifications.
     */
    List<UserFcmToken> findByUsernameAndActiveTrue(String username);

    /**
     * Look up a specific token to avoid duplicates on re-registration.
     */
    Optional<UserFcmToken> findByUsernameAndFcmToken(String username, String fcmToken);

    /**
     * Deactivate a specific token (e.g. on logout or FCM error).
     */
    @Modifying
    @Transactional
    @Query("UPDATE UserFcmToken t SET t.active = false WHERE t.fcmToken = :token")
    void deactivateToken(@Param("token") String token);

    /**
     * Deactivate all tokens for a user (e.g. full logout / account disable).
     */
    @Modifying
    @Transactional
    @Query("UPDATE UserFcmToken t SET t.active = false WHERE t.username = :username")
    void deactivateAllForUser(@Param("username") String username);
}
