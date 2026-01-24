package com.godesii.godesii_services.service.auth;

import com.godesii.godesii_services.config.twilio.SmsRequest;
import com.godesii.godesii_services.config.twilio.TwilioSmsSenderService;
import com.godesii.godesii_services.entity.auth.User;
import com.godesii.godesii_services.repository.auth.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Service for OTP generation, validation, and sending
 */
@Service
public class OtpService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OtpService.class);
    private static final int OTP_LENGTH = 6;
    private static final long OTP_EXPIRY_MINUTES = 5;
    private static final int MAX_OTP_ATTEMPTS = 3;

    // In-memory storage for OTP expiry and attempt tracking
    // For production, consider using Redis or database
    private final Map<String, OtpData> otpExpiryMap = new HashMap<>();

    private final UserRepository userRepository;
    private final TwilioSmsSenderService twilioSmsSenderService;
    private final SecureRandom secureRandom;

    public OtpService(UserRepository userRepository, TwilioSmsSenderService twilioSmsSenderService) {
        this.userRepository = userRepository;
        this.twilioSmsSenderService = twilioSmsSenderService;
        this.secureRandom = new SecureRandom();
    }

    /**
     * Generate a 6-digit OTP
     */
    public String generateOtp() {
        int otp = 100000 + secureRandom.nextInt(900000);
        return String.valueOf(otp);
    }

    /**
     * Generate OTP for a user and send via SMS
     * 
     * @param mobileNo User's mobile number
     * @return Generated OTP (for testing purposes, remove in production)
     */
    @Transactional
    public String generateAndSendOtp(String mobileNo) {
        Optional<User> userOptional = userRepository.findByMobileNo(mobileNo);

        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User with mobile number " + mobileNo + " not found");
        }

        User user = userOptional.get();

        // Check rate limiting
        String key = mobileNo;
        OtpData otpData = otpExpiryMap.get(key);
        if (otpData != null && otpData.getAttemptCount() >= MAX_OTP_ATTEMPTS) {
            long timeSinceFirstAttempt = System.currentTimeMillis() - otpData.getFirstAttemptTime();
            if (timeSinceFirstAttempt < 15 * 60 * 1000) { // 15 minutes
                throw new IllegalStateException("Too many OTP requests. Please try again after 15 minutes.");
            } else {
                // Reset after 15 minutes
                otpExpiryMap.remove(key);
            }
        }

        // Generate OTP
        String otp = generateOtp();

        // Store OTP in user entity
        user.setLoginOtp(otp);
        user.setUpdatedAt(new Date());
        userRepository.save(user);

        // Store expiry time
        long expiryTime = System.currentTimeMillis() + (OTP_EXPIRY_MINUTES * 60 * 1000);
        if (otpData == null) {
            otpData = new OtpData(expiryTime, System.currentTimeMillis(), 1);
        } else {
            otpData.setExpiryTime(expiryTime);
            otpData.incrementAttempt();
        }
        otpExpiryMap.put(key, otpData);

        // Send OTP via SMS
        sendOtpSms(mobileNo, otp);

        LOGGER.info("OTP generated and sent to mobile: {}", mobileNo);

        return otp; // Return for testing, remove in production
    }

    /**
     * Send OTP via SMS using Twilio
     */
    private void sendOtpSms(String mobileNo, String otp) {
        try {
            String message = String.format(
                    "Your Go Desii login OTP is: %s. Valid for %d minutes. Do not share this OTP with anyone.",
                    otp, OTP_EXPIRY_MINUTES);
            SmsRequest smsRequest = new SmsRequest(mobileNo, message);
            twilioSmsSenderService.sendSms(smsRequest);
            LOGGER.info("OTP SMS sent successfully to: {}", mobileNo);
        } catch (Exception e) {
            LOGGER.error("Failed to send OTP SMS to: {}", mobileNo, e);
            throw new RuntimeException("Failed to send OTP SMS", e);
        }
    }

    /**
     * Validate OTP for a user
     * 
     * @param mobileNo User's mobile number
     * @param otp      OTP to validate
     * @return true if OTP is valid, false otherwise
     */
    @Transactional
    public boolean validateOtp(String mobileNo, String otp) {
        Optional<User> userOptional = userRepository.findByMobileNo(mobileNo);

        if (userOptional.isEmpty()) {
            LOGGER.warn("Validation failed: User not found for mobile: {}", mobileNo);
            return false;
        }

        User user = userOptional.get();

        // Check if OTP exists
        if (user.getLoginOtp() == null || user.getLoginOtp().isEmpty()) {
            LOGGER.warn("Validation failed: No OTP found for mobile: {}", mobileNo);
            return false;
        }

        // Check if OTP has expired
        String key = mobileNo;
        OtpData otpData = otpExpiryMap.get(key);
        if (otpData == null || System.currentTimeMillis() > otpData.getExpiryTime()) {
            LOGGER.warn("Validation failed: OTP expired for mobile: {}", mobileNo);
            clearOtp(mobileNo);
            return false;
        }

        // Validate OTP
        boolean isValid = user.getLoginOtp().equals(otp);

        if (isValid) {
            LOGGER.info("OTP validated successfully for mobile: {}", mobileNo);
            clearOtp(mobileNo);
        } else {
            LOGGER.warn("Validation failed: Invalid OTP for mobile: {}", mobileNo);
        }

        return isValid;
    }

    /**
     * Clear OTP after successful validation or expiry
     */
    @Transactional
    public void clearOtp(String mobileNo) {
        Optional<User> userOptional = userRepository.findByMobileNo(mobileNo);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setLoginOtp(null);
            user.setUpdatedAt(new Date());
            userRepository.save(user);

            // Remove from expiry map
            otpExpiryMap.remove(mobileNo);

            LOGGER.info("OTP cleared for mobile: {}", mobileNo);
        }
    }

    /**
     * Send welcome SMS after successful registration
     */
    public void sendWelcomeSms(String mobileNo, String firstName) {
        try {
            String message = String.format(
                    "Welcome to Go Desii, %s! Your account has been created successfully. Enjoy delicious food delivered to your doorstep!",
                    firstName);
            SmsRequest smsRequest = new SmsRequest(mobileNo, message);
            twilioSmsSenderService.sendSms(smsRequest);
            LOGGER.info("Welcome SMS sent successfully to: {}", mobileNo);
        } catch (Exception e) {
            LOGGER.error("Failed to send welcome SMS to: {}", mobileNo, e);
            // Don't throw exception, welcome SMS is not critical
        }
    }

    /**
     * Inner class to store OTP expiry and attempt data
     */
    private static class OtpData {
        private long expiryTime;
        private long firstAttemptTime;
        private int attemptCount;

        public OtpData(long expiryTime, long firstAttemptTime, int attemptCount) {
            this.expiryTime = expiryTime;
            this.firstAttemptTime = firstAttemptTime;
            this.attemptCount = attemptCount;
        }

        public long getExpiryTime() {
            return expiryTime;
        }

        public void setExpiryTime(long expiryTime) {
            this.expiryTime = expiryTime;
        }

        public long getFirstAttemptTime() {
            return firstAttemptTime;
        }

        public int getAttemptCount() {
            return attemptCount;
        }

        public void incrementAttempt() {
            this.attemptCount++;
        }
    }
}
