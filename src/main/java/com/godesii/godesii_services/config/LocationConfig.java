package com.godesii.godesii_services.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for the service location (geofence centre + radius).
 * Values are bound from the {@code app.location} prefix in the active YAML profile.
 */
@Component
@ConfigurationProperties(prefix = "app.location")
public class LocationConfig {

    /** Latitude of the service centre point (decimal degrees). */
    private double latitude;

    /** Longitude of the service centre point (decimal degrees). */
    private double longitude;

    /** Search / geofence radius in kilometres. */
    private double radius;

    // ── Getters & Setters ────────────────────────────────────────────────────

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }
}
