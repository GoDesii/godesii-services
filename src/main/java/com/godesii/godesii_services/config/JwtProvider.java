package com.godesii.godesii_services.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.*;
import java.security.interfaces.RSAKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtProvider {


    @Value("${app.jwt.access-token.expiration}")
    private String accessTokenExpiryTime;

    @Value("${app.jwt.refresh-token.expiration}")
    private String refreshTokenExpiryTime;

    private final KeyPair keyPair = Keys.keyPairFor(SignatureAlgorithm.RS256);
    private final PrivateKey privateKey = keyPair.getPrivate();
    private final PublicKey publicKey = keyPair.getPublic();

    @SuppressWarnings("all")
    public String generateAccessToken(Authentication authentication) {
        Map<String, String> claims = new HashMap<>();
        claims.put("claim_type", "access_token");
        String username = authentication.getName();
        Date currentDate = new Date();
        Date accessTokenExpiryDate = new Date(currentDate.getTime() + (Integer.parseInt(this.refreshTokenExpiryTime) * 1000));

        return Jwts
                .builder()
                .setClaims(claims)
                .subject(username)
                .issuedAt(currentDate)
                .expiration(accessTokenExpiryDate)
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    @SuppressWarnings("all")
    public String generateRefreshToken(Authentication authentication) {
        Map<String, String> claims = new HashMap<>();
        claims.put("claim_type", "refresh_token");
        String username = authentication.getName();
        Date currentDate = new Date();
        Date refreshTokenExpiryDate = new Date(currentDate.getTime() + (Integer.parseInt(this.refreshTokenExpiryTime) * 1000));

        return Jwts
                .builder()
                .setClaims(claims)
                .subject(username)
                .issuedAt(currentDate)
                .expiration(refreshTokenExpiryDate)
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }


    public String getUsername(String token) {
        return Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public String getAccessTokenExpiryTime(){
        return accessTokenExpiryTime;
    }

    public String getRefreshTokenExpiryTime(){
        return refreshTokenExpiryTime;
    }

    public PublicKey getPublicKey(){
        return this.publicKey;
    }

    public PrivateKey getPrivateKey(){
        return this.privateKey;
    }

    public String generateSecretKey() {
        // length means (32 bytes are required for 256-bit key)
        int length = 32;

        // Create a secure random generator
        SecureRandom secureRandom = new SecureRandom();

        // Create a byte array to hold the random bytes
        byte[] keyBytes = new byte[length];

        // Generate the random bytes
        secureRandom.nextBytes(keyBytes);

        // Encode the key in Base64 format for easier storage and usage
        return Base64.getEncoder().encodeToString(keyBytes);
    }
}
