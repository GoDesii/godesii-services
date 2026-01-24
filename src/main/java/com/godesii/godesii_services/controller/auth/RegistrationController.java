package com.godesii.godesii_services.controller.auth;

import com.godesii.godesii_services.dto.auth.*;
import com.godesii.godesii_services.entity.auth.User;
import com.godesii.godesii_services.service.auth.RegistrationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for user registration across all user types
 */
@RestController
@RequestMapping("/auth/register")
public class RegistrationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegistrationController.class);

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    /**
     * Register a new customer
     * Public endpoint - no authentication required
     */
    @PostMapping("/customer")
    public ResponseEntity<Map<String, Object>> registerCustomer(
            @Valid @RequestBody CustomerRegistrationRequest request) {
        LOGGER.info("Customer registration request received for mobile: {}", request.getMobileNo());

        User user = registrationService.registerCustomer(request);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Customer registered successfully. Please verify your mobile number.");
        response.put("userId", user.getId());
        response.put("username", user.getUsername());
        response.put("mobile", user.getMobileNo());
        response.put("role", user.getRole());
        response.put("mobileVerified", user.getMobileNoVerified());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Register a new vendor
     * Public endpoint - no authentication required
     */
    @PostMapping("/vendor")
    public ResponseEntity<Map<String, Object>> registerVendor(
            @Valid @RequestBody VendorRegistrationRequest request) {
        LOGGER.info("Vendor registration request received for mobile: {}", request.getMobileNo());

        User user = registrationService.registerVendor(request);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Vendor registered successfully. Please verify your mobile number.");
        response.put("userId", user.getId());
        response.put("username", user.getUsername());
        response.put("mobile", user.getMobileNo());
        response.put("role", user.getRole());
        response.put("businessName", request.getBusinessName());
        response.put("mobileVerified", user.getMobileNoVerified());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Register a new restaurant owner
     * Public endpoint - no authentication required
     */
    @PostMapping("/restaurant")
    public ResponseEntity<Map<String, Object>> registerRestaurant(
            @Valid @RequestBody RestaurantRegistrationRequest request) {
        LOGGER.info("Restaurant registration request received for mobile: {}", request.getMobileNo());

        User user = registrationService.registerRestaurant(request);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Restaurant owner registered successfully. Please verify your mobile number.");
        response.put("userId", user.getId());
        response.put("username", user.getUsername());
        response.put("mobile", user.getMobileNo());
        response.put("role", user.getRole());
        response.put("restaurantName", request.getRestaurantName());
        response.put("mobileVerified", user.getMobileNoVerified());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Register a new delivery person
     * Public endpoint - no authentication required
     */
    @PostMapping("/delivery-person")
    public ResponseEntity<Map<String, Object>> registerDeliveryPerson(
            @Valid @RequestBody DeliveryPersonRegistrationRequest request) {
        LOGGER.info("Delivery person registration request received for mobile: {}", request.getMobileNo());

        User user = registrationService.registerDeliveryPerson(request);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Delivery person registered successfully. Please verify your mobile number.");
        response.put("userId", user.getId());
        response.put("username", user.getUsername());
        response.put("mobile", user.getMobileNo());
        response.put("role", user.getRole());
        response.put("vehicleType", request.getVehicleType());
        response.put("mobileVerified", user.getMobileNoVerified());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Register a new manager
     * Restricted to ADMIN and SUPER_ADMIN only
     */
    @PostMapping("/manager")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Map<String, Object>> registerManager(
            @Valid @RequestBody RegisterRequest request) {
        LOGGER.info("Manager registration request received for mobile: {}", request.getMobileNo());

        User user = registrationService.registerManager(request);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Manager registered successfully. Please verify mobile number.");
        response.put("userId", user.getId());
        response.put("username", user.getUsername());
        response.put("mobile", user.getMobileNo());
        response.put("role", user.getRole());
        response.put("mobileVerified", user.getMobileNoVerified());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Register a new admin
     * Restricted to SUPER_ADMIN only
     */
    @PostMapping("/admin")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<Map<String, Object>> registerAdmin(
            @Valid @RequestBody RegisterRequest request) {
        LOGGER.info("Admin registration request received for mobile: {}", request.getMobileNo());

        User user = registrationService.registerAdmin(request);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Admin registered successfully. Please verify mobile number.");
        response.put("userId", user.getId());
        response.put("username", user.getUsername());
        response.put("mobile", user.getMobileNo());
        response.put("role", user.getRole());
        response.put("mobileVerified", user.getMobileNoVerified());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Verify mobile number with OTP
     * Public endpoint - used after registration
     */
    @PostMapping("/verify-mobile")
    public ResponseEntity<Map<String, String>> verifyMobile(
            @Valid @RequestBody MobileVerificationRequest request) {
        LOGGER.info("Mobile verification request for: {}", request.getMobileNo());

        boolean verified = registrationService.verifyMobile(request);

        Map<String, String> response = new HashMap<>();
        if (verified) {
            response.put("message", "Mobile number verified successfully. You can now login.");
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Invalid or expired OTP");
            response.put("status", "failed");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
