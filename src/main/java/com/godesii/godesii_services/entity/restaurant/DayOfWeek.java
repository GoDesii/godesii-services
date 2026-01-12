package com.godesii.godesii_services.entity.restaurant;

public enum DayOfWeek {
    SUNDAY(0, "Sunday"),
    MONDAY(1, "Monday"),
    TUESDAY(2, "Tuesday"),
    WEDNESDAY(3, "Wednesday"),
    THURSDAY(4, "Thursday"),
    FRIDAY(5, "Friday"),
    SATURDAY(6, "Saturday");

    private final int value;
    private final String displayName;

    DayOfWeek(int value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    public int getValue() {
        return value;
    }

    public String getDisplayName() {
        return displayName;
    }

    // Helper method to convert database integer to Enum
    public static DayOfWeek fromInt(int day) {
        for (DayOfWeek d : DayOfWeek.values()) {
            if (d.value == day) return d;
        }
        throw new IllegalArgumentException("Invalid day of week: " + day);
    }
}