package com.godesii.godesii_services.repository.auth;

import com.godesii.godesii_services.entity.auth.RSAKeys;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;

@Repository
public interface JpaRSAKeysRepository extends JpaRepository<RSAKeys, Long> {

    record RsaKeyPair(String id, Instant created, RSAPublicKey publicKey, RSAPrivateKey privateKey) { }

}
