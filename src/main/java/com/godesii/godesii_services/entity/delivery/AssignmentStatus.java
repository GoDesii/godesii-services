package com.godesii.godesii_services.entity.delivery;

/**
 * Delivery assignment status lifecycle
 */
public enum AssignmentStatus {

    /**
     * Assignment created, waiting for partner acceptance
     */
    ASSIGNED("Assigned to partner"),

    /**
     * Partner accepted the delivery
     */
    ACCEPTED("Partner accepted"),

    /**
     * Partner rejected the delivery
     */
    REJECTED("Partner rejected"),

    /**
     * Partner picked up the order from restaurant
     */
    PICKED_UP("Order picked up"),

    /**
     * Order successfully delivered
     */
    DELIVERED("Order delivered"),

    /**
     * Assignment cancelled (order cancelled or partner reassigned)
     */
    CANCELLED("Assignment cancelled");

    private final String description;

    AssignmentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
