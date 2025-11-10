package com.godesii.godesii_services.repository.auth;

import com.godesii.godesii_services.entity.auth.SecureToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaSecureTokenRepository extends JpaRepository<SecureToken, String> {

    SecureToken findByIdentifierAndToken(String identifier, String token);
}
