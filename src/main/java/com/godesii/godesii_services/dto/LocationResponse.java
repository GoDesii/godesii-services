package com.godesii.godesii_services.dto;

/**
 * DTO representing the configured service location.
 */
public class LocationResponse {

    private double latitude;
    private double longitude;
    private double radius;

    public LocationResponse() {}

    public LocationResponse(double latitude, double longitude, double radius) {
        this.latitude  = latitude;
        this.longitude = longitude;
        this.radius    = radius;
    }

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
