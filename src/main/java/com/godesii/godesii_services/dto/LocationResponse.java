package com.godesii.godesii_services.dto;

/**
 * DTO representing a configured service location (town / service zone).
 */
public class LocationResponse {

    /** Town or area name (e.g. "Lalganj"). */
    private String locationName;

    private double latitude;
    private double longitude;
    private double radius;

    public LocationResponse() {}

    public LocationResponse(String locationName, double latitude, double longitude, double radius) {
        this.locationName = locationName;
        this.latitude     = latitude;
        this.longitude    = longitude;
        this.radius       = radius;
    }

    // ── Getters & Setters ────────────────────────────────────────────────────

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

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
