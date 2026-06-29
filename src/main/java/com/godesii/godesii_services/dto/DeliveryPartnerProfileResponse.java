package com.godesii.godesii_services.dto;

/**
 * Public-facing delivery partner profile.
 * Exposes only safe fields — sensitive data (Aadhaar, DL number, email) is intentionally excluded.
 */
public class DeliveryPartnerProfileResponse {

    private final String partnerId;
    private final String name;
    private final String phoneNo;
    private final String imageUrl;

    public DeliveryPartnerProfileResponse(String partnerId, String name,
                                          String phoneNo, String imageUrl) {
        this.partnerId = partnerId;
        this.name      = name;
        this.phoneNo   = phoneNo;
        this.imageUrl  = imageUrl;
    }

    public String getPartnerId() { return partnerId; }
    public String getName()      { return name; }
    public String getPhoneNo()   { return phoneNo; }
    public String getImageUrl()  { return imageUrl; }
}
