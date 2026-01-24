package com.godesii.godesii_services.entity.delivery;

/**
 * Vehicle types for delivery partners
 */
public enum VehicleType {
    BIKE("Bike"),
    SCOOTER("Scooter"),
    BICYCLE("Bicycle"),
    CAR("Car"),
    VAN("Van");

    private final String displayName;

    VehicleType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
