package com.godesii.godesii_services.dto;

import com.godesii.godesii_services.entity.delivery.VehicleType;
import jakarta.validation.constraints.*;

/**
 * Request body for registering a new delivery partner.
 *
 * <h3>Validation rules</h3>
 * <ul>
 *   <li><b>name</b>            — 2–100 characters, letters and spaces only</li>
 *   <li><b>phoneNo</b>         — 10 digits, must start with 6–9 (Indian mobile)</li>
 *   <li><b>aadhaarCardNo</b>   — exactly 12 digits</li>
 *   <li><b>drivingLicenseNo</b>— Indian DL format: 2 uppercase letters (state) +
 *       2 digits (RTO) + 4 digits (year) + 7 digits (serial) = 15 chars total,
 *       e.g. {@code DL0119900000001}</li>
 *   <li><b>emailId</b>         — standard email format</li>
 * </ul>
 */
public class DeliveryPartnerRegistrationRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Pattern(regexp = "^[A-Za-z ]+$", message = "Name must contain only letters and spaces")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String emailId;

    /**
     * Indian Aadhaar card number — exactly 12 digits, no spaces or hyphens.
     * Example: {@code 123456789012}
     */
    @NotBlank(message = "Aadhaar card number is required")
    @Pattern(
        regexp = "^[0-9]{12}$",
        message = "Aadhaar card number must be exactly 12 digits (e.g. 123456789012)"
    )
    private String aadhaarCardNo;

    /**
     * Indian driving licence number — state code (2 letters) + RTO code (2 digits) +
     * year of issue (4 digits) + serial number (7 digits) = 15 characters total.
     *
     * <p>Examples:
     * <ul>
     *   <li>{@code DL0119900000001} (Delhi)</li>
     *   <li>{@code MH0220050012345} (Maharashtra)</li>
     *   <li>{@code KA0320100098765} (Karnataka)</li>
     * </ul>
     */
    @NotBlank(message = "Driving licence number is required")
    @Pattern(
        regexp = "^[A-Z]{2}[0-9]{2}[0-9]{4}[0-9]{7}$",
        message = "Driving licence must be 15 chars: 2 uppercase letters + 2 digits + 4-digit year + 7 digits (e.g. DL0119900000001)"
    )
    private String drivingLicenseNo;

    /** URL pointing to the partner's profile photo / selfie with ID card. */
    private String imageUrl;

    /**
     * Indian mobile number — exactly 10 digits, starting with 6, 7, 8, or 9.
     * Example: {@code 9876543210}
     */
    @NotBlank(message = "Phone number is required")
    @Pattern(
        regexp = "^[6-9][0-9]{9}$",
        message = "Phone number must be a valid 10-digit Indian mobile number starting with 6–9 (e.g. 9876543210)"
    )
    private String phoneNo;

    /** Optional — vehicle the partner uses for deliveries. */
    private VehicleType vehicleType;

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmailId() { return emailId; }
    public void setEmailId(String emailId) { this.emailId = emailId; }

    public String getAadhaarCardNo() { return aadhaarCardNo; }
    public void setAadhaarCardNo(String aadhaarCardNo) { this.aadhaarCardNo = aadhaarCardNo; }

    public String getDrivingLicenseNo() { return drivingLicenseNo; }
    public void setDrivingLicenseNo(String drivingLicenseNo) { this.drivingLicenseNo = drivingLicenseNo; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getPhoneNo() { return phoneNo; }
    public void setPhoneNo(String phoneNo) { this.phoneNo = phoneNo; }

    public VehicleType getVehicleType() { return vehicleType; }
    public void setVehicleType(VehicleType vehicleType) { this.vehicleType = vehicleType; }
}
