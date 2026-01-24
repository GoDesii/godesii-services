package com.godesii.godesii_services.service.auth;

import com.godesii.godesii_services.dto.auth.*;
import com.godesii.godesii_services.entity.auth.AuthProvider;
import com.godesii.godesii_services.entity.auth.Role;
import com.godesii.godesii_services.entity.auth.User;
import com.godesii.godesii_services.repository.auth.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

/**
 * Service for handling user registration across all user types
 */
@Service
public class RegistrationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegistrationService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;

    public RegistrationService(UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            OtpService otpService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.otpService = otpService;
    }

    /**
     * Register a new customer
     */
    @Transactional
    public User registerCustomer(CustomerRegistrationRequest request) {
        LOGGER.info("Registering new customer with mobile: {}", request.getMobileNo());

        validateMobileUnique(request.getMobileNo());

        User user = createBaseUser(request, Role.CUSTOMER);
        user = userRepository.save(user);

        // Generate and send OTP for mobile verification
        otpService.generateAndSendOtp(user.getMobileNo());

        LOGGER.info("Customer registered successfully with ID: {}", user.getId());
        return user;
    }

    /**
     * Register a new vendor
     */
    @Transactional
    public User registerVendor(VendorRegistrationRequest request) {
        LOGGER.info("Registering new vendor with mobile: {}", request.getMobileNo());

        validateMobileUnique(request.getMobileNo());

        User user = createBaseUser(request, Role.VENDOR);
        // Note: Vendor-specific details (business info, GST) would be stored in a
        // separate Vendor entity
        user = userRepository.save(user);

        // Generate and send OTP for mobile verification
        otpService.generateAndSendOtp(user.getMobileNo());

        LOGGER.info("Vendor registered successfully with ID: {}", user.getId());
        return user;
    }

    /**
     * Register a new restaurant owner
     */
    @Transactional
    public User registerRestaurant(RestaurantRegistrationRequest request) {
        LOGGER.info("Registering new restaurant owner with mobile: {}", request.getMobileNo());

        validateMobileUnique(request.getMobileNo());

        User user = createBaseUser(request, Role.RESTAURANT);
        // Note: Restaurant-specific details would be stored in Restaurant entity
        user = userRepository.save(user);

        // Generate and send OTP for mobile verification
        otpService.generateAndSendOtp(user.getMobileNo());

        LOGGER.info("Restaurant owner registered successfully with ID: {}", user.getId());
        return user;
    }

    /**
     * Register a new delivery person
     */
    @Transactional
    public User registerDeliveryPerson(DeliveryPersonRegistrationRequest request) {
        LOGGER.info("Registering new delivery person with mobile: {}", request.getMobileNo());

        validateMobileUnique(request.getMobileNo());

        User user = createBaseUser(request, Role.DELIVERY_PERSON);
        // Note: Delivery person-specific details would be stored in a separate
        // DeliveryPerson entity
        user = userRepository.save(user);

        // Generate and send OTP for mobile verification
        otpService.generateAndSendOtp(user.getMobileNo());

        LOGGER.info("Delivery person registered successfully with ID: {}", user.getId());
        return user;
    }

    /**
     * Register a new manager (admin-only operation)
     * This should be called only by admin users
     */
    @Transactional
    public User registerManager(RegisterRequest request) {
        LOGGER.info("Registering new manager with mobile: {}", request.getMobileNo());

        validateMobileUnique(request.getMobileNo());

        User user = createBaseUser(request, Role.MANAGER);
        user = userRepository.save(user);

        // Generate and send OTP for mobile verification
        otpService.generateAndSendOtp(user.getMobileNo());

        LOGGER.info("Manager registered successfully with ID: {}", user.getId());
        return user;
    }

    /**
     * Register a new admin (super-admin only operation)
     */
    @Transactional
    public User registerAdmin(RegisterRequest request) {
        LOGGER.info("Registering new admin with mobile: {}", request.getMobileNo());

        validateMobileUnique(request.getMobileNo());

        User user = createBaseUser(request, Role.ADMIN);
        user = userRepository.save(user);

        // Generate and send OTP for mobile verification
        otpService.generateAndSendOtp(user.getMobileNo());

        LOGGER.info("Admin registered successfully with ID: {}", user.getId());
        return user;
    }

    /**
     * Verify mobile number with OTP
     */
    @Transactional
    public boolean verifyMobile(MobileVerificationRequest request) {
        LOGGER.info("Verifying mobile: {}", request.getMobileNo());

        boolean isValid = otpService.validateOtp(request.getMobileNo(), request.getOtp());

        if (isValid) {
            Optional<User> userOptional = userRepository.findByMobileNo(request.getMobileNo());
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                user.setMobileNoVerified(true);
                user.setPhoneNoVerified(true);
                user.setUpdatedAt(new Date());
                userRepository.save(user);

                // Send welcome SMS
                otpService.sendWelcomeSms(user.getMobileNo(), user.getFirstName());

                LOGGER.info("Mobile verified successfully for user: {}", user.getId());
                return true;
            }
        }

        LOGGER.warn("Mobile verification failed for: {}", request.getMobileNo());
        return false;
    }

    /**
     * Create base user from registration request
     */
    private User createBaseUser(RegisterRequest request, Role role) {
        User user = new User();

        // Basic details
        user.setMobileNo(request.getMobileNo());
        user.setCountryCode(request.getCountryCode());
        user.setFirstName(request.getFirstName());
        user.setMiddleName(request.getMiddleName());
        user.setLastName(request.getLastName());
        user.setEmailId(request.getEmailId());
        user.setGender(request.getGender());
        user.setRole(role);

        // Generate username from mobile if not provided
        user.setUsername(generateUsername(request.getMobileNo(), request.getFirstName()));

        // Encode password if provided
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        // Set auth provider
        user.setAuthProvider(AuthProvider.DEFAULT);

        // Set account flags
        user.setMobileNoVerified(false);
        user.setEmailVerified(false);
        user.setPhoneNoVerified(false);
        user.setAccountNonLocked(true);
        user.setAccountNonExpired(true);
        user.setCredentialsNonExpired(true);

        // Set timestamps
        Date now = new Date();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        return user;
    }

    /**
     * Generate username from mobile number and first name
     */
    private String generateUsername(String mobileNo, String firstName) {
        String baseUsername = firstName.toLowerCase().replaceAll("\\s+", "") + "_"
                + mobileNo.substring(mobileNo.length() - 4);

        // Check if username exists and add suffix if needed
        String username = baseUsername;
        int suffix = 1;
        while (userRepository.findByUsername(username).isPresent()) {
            username = baseUsername + suffix;
            suffix++;
        }

        return username;
    }

    /**
     * Validate that mobile number is unique
     */
    private void validateMobileUnique(String mobileNo) {
        Optional<User> existingUser = userRepository.findByMobileNo(mobileNo);
        if (existingUser.isPresent()) {
            LOGGER.warn("Mobile number already registered: {}", mobileNo);
            throw new IllegalArgumentException("Mobile number is already registered");
        }
    }
}
