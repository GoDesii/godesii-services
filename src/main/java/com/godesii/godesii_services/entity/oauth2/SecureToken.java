package com.godesii.godesii_services.entity.oauth2;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;

@Entity
@Table(name = "secure_token")
public class SecureToken {

    private String id;
    private Instant issuedAt;
    private Instant expiredAt;
    private String usernameOrMobileNo;
    private String token;

    @Id
    @UuidGenerator
    @Column(name = "secure_otp_id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column(name = "issued_at")
    public Instant getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(Instant issuedAt) {
        this.issuedAt = issuedAt;
    }

    @Column(name = "expired_at")
    public Instant getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(Instant expiredAt) {
        this.expiredAt = expiredAt;
    }

    @Column(name = "username_or_mobile_no")
    public String getUsernameOrMobileNo() {
        return usernameOrMobileNo;
    }

    public void setUsernameOrMobileNo(String usernameOrMobileNo) {
        this.usernameOrMobileNo = usernameOrMobileNo;
    }

    @Column(name = "token", unique = true, updatable = false)
    public String getToken() {
        return token;
    }

    public void setToken(String otp) {
        this.token = token;
    }
}
