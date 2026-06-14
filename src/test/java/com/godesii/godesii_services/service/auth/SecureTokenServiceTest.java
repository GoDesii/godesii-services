package com.godesii.godesii_services.service.auth;

import com.godesii.godesii_services.entity.auth.SecureToken;
import com.godesii.godesii_services.repository.auth.JpaSecureTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SecureTokenServiceTest {

    @Mock
    private JpaSecureTokenRepository secureTokenRepository;

    @InjectMocks
    private SecureTokenService secureTokenService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ── generateOTP ──────────────────────────────────────────────────────────

    @Test
    void generateOTP_shouldReturnSixDigitString() {
        String otp = SecureTokenService.generateOTP();

        assertNotNull(otp);
        assertEquals(6, otp.length());
        assertTrue(otp.matches("\\d{6}"), "OTP should contain only digits");
    }

    @Test
    void generateOTP_multipleCalls_shouldGenerateValues() {
        // Not guaranteed unique due to randomness, but should always be 6 digits
        String otp1 = SecureTokenService.generateOTP();
        String otp2 = SecureTokenService.generateOTP();

        assertEquals(6, otp1.length());
        assertEquals(6, otp2.length());
    }

    // ── savesecureToken ──────────────────────────────────────────────────────

    @Test
    void saveSecureToken_shouldCallRepositorySave() {
        SecureToken token = new SecureToken();
        token.setIdentifier("9876543210");
        token.setToken("123456");

        when(secureTokenRepository.save(token)).thenReturn(token);

        secureTokenService.savesecureToken(token);

        verify(secureTokenRepository, times(1)).save(token);
    }

    // ── findByOTP (stub — always returns null) ───────────────────────────────

    @Test
    void findByOTP_shouldReturnNull() {
        assertNull(secureTokenService.findByOTP("123456"));
    }

    // ── removeOTP (no-op) ────────────────────────────────────────────────────

    @Test
    void removeOTP_shouldNotThrow() {
        SecureToken token = new SecureToken();
        assertDoesNotThrow(() -> secureTokenService.removeOTP(token));
    }

    // ── isExpired (stub — always returns null) ───────────────────────────────

    @Test
    void isExpired_shouldReturnNull() {
        SecureToken token = new SecureToken();
        assertNull(secureTokenService.isExpired(token));
    }
}
