package com.godesii.godesii_services.dto;

/**
 * Request body for FCM token registration / refresh.
 *
 * <p>Mobile app sends this payload after login or whenever Firebase
 * calls the {@code onTokenRefresh} callback.
 *
 * <p>Example JSON:
 * <pre>
 * {
 *   "fcmToken": "eKz9...token...",
 *   "deviceLabel": "Shashank's Pixel 8",
 *   "platform": "ANDROID",
 *   "topic": "delivery-partner-abc123"
 * }
 * </pre>
 */
public class FcmTokenRequest {

    /** The FCM registration token provided by the Firebase SDK on the device. */
    private String fcmToken;

    /** Human-readable device name. Optional but helpful for support. */
    private String deviceLabel;

    /**
     * Device platform. Expected values: "ANDROID", "IOS", "WEB".
     * Stored for analytics; may affect payload construction in the future.
     */
    private String platform;

    /**
     * Optional FCM topic to subscribe this token to immediately after registration.
     * E.g. "delivery-partner-{partnerId}" for partner apps.
     */
    private String topic;

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public String getFcmToken() { return fcmToken; }
    public void setFcmToken(String fcmToken) { this.fcmToken = fcmToken; }

    public String getDeviceLabel() { return deviceLabel; }
    public void setDeviceLabel(String deviceLabel) { this.deviceLabel = deviceLabel; }

    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
}
