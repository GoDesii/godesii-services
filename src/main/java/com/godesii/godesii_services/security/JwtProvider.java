package com.godesii.godesii_services.security;

import com.godesii.godesii_services.entity.auth.RSAKeys;
import com.godesii.godesii_services.repository.auth.JpaRSAKeysRepository;
import com.godesii.godesii_services.security.management.rotation_key.RSAPrivateKeyConverter;
import com.godesii.godesii_services.security.management.rotation_key.RSAPublicKeyConverter;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import java.io.IOException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public final class JwtProvider {

    public static final Logger LOGGER = LoggerFactory.getLogger(JwtProvider.class);;

    @Value("${app.jwt.access-token.expiration}")
    private String accessTokenExpiryTime;

    @Value("${app.jwt.refresh-token.expiration}")
    private String refreshTokenExpiryTime;

    private final JpaRSAKeysRepository jpaRSAKeysRepository;
    private final RSAPublicKeyConverter rsaPublicKeyConverter;
    private final RSAPrivateKeyConverter rsaPrivateKeyConverter;

    public JwtProvider(JpaRSAKeysRepository jpaRSAKeysRepository,
                       RSAPublicKeyConverter rsaPublicKeyConverter,
                       RSAPrivateKeyConverter rsaPrivateKeyConverter) {
        this.jpaRSAKeysRepository = jpaRSAKeysRepository;
        this.rsaPublicKeyConverter = rsaPublicKeyConverter;
        this.rsaPrivateKeyConverter = rsaPrivateKeyConverter;
    }

    @SuppressWarnings("all")
    public String generateAccessToken(Authentication authentication) {
        Map<String, String> claims = new HashMap<>();
        claims.put("claim_type", "access_token");
        String username = authentication.getName();
        Date currentDate = new Date();
        Date accessTokenExpiryDate =
                new Date(currentDate.getTime() + (Integer.parseInt(this.refreshTokenExpiryTime) * 1000));

        return Jwts
                .builder()
                .setClaims(claims)
                .subject(username)
                .issuedAt(currentDate)
                .expiration(accessTokenExpiryDate)
                .signWith(this.getRSAKeys().privateKey(), SignatureAlgorithm.RS256)
                .compact();
    }

    @SuppressWarnings("all")
    public String generateRefreshToken(Authentication authentication) {
        Map<String, String> claims = new HashMap<>();
        claims.put("claim_type", "refresh_token");
        String username = authentication.getName();
        Date currentDate = new Date();
        Date refreshTokenExpiryDate =
                new Date(currentDate.getTime() + (Integer.parseInt(this.refreshTokenExpiryTime) * 1000));

        return Jwts
                .builder()
                .setClaims(claims)
                .subject(username)
                .issuedAt(currentDate)
                .expiration(refreshTokenExpiryDate)
                .signWith(this.getRSAKeys().privateKey(), SignatureAlgorithm.RS256)
                .compact();
    }


    public String getUsername(String token) {
        return Jwts.parser()
                .verifyWith(this.getRSAKeys().publicKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateToken(String token, String username) {
        String subject = getUsername(token);
        return (subject.equals(username) && !isTokenExpired(token, this.getRSAKeys().publicKey())) ;
    }

    public boolean isTokenExpired(String token, PublicKey publicKey) {
        Date expiration = Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload().getExpiration();
        return expiration.before(new Date());
    }

    public JpaRSAKeysRepository.RsaKeyPair getRSAKeys()  {
        try{
            RSAKeys rsaKeys = this.jpaRSAKeysRepository
                    .findAll(Sort.by(Sort.Direction.DESC, "createdAt")).get(0);
            RSAPublicKey publicKey = this.rsaPublicKeyConverter
                    .deserializeFromByteArray(rsaKeys.getPublicKey().getBytes());
            RSAPrivateKey privateKey = this.rsaPrivateKeyConverter
                    .deserializeFromByteArray(rsaKeys.getPrivateKey().getBytes());
            return new JpaRSAKeysRepository.RsaKeyPair(
                    rsaKeys.getId(),
                    rsaKeys.getCreatedAt(),
                    publicKey,
                    privateKey
            );
        }
        catch (IOException ex){
            LOGGER.error(ex.getMessage(), ex);
        }
        return null;
    }

    public String getAccessTokenExpiryTime(){
        return accessTokenExpiryTime;
    }

    public String getRefreshTokenExpiryTime(){
        return refreshTokenExpiryTime;
    }

}
