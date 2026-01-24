package com.godesii.godesii_services.entity.auth;

/**
 * User roles in the Go Desii application
 */
public enum Role {
    /** Customer who orders food */
    CUSTOMER,
    
    /** Vendor/supplier of products */
    VENDOR,
    
    /** Restaurant owner */
    RESTAURANT,
    
    /** Delivery person who delivers orders */
    DELIVERY_PERSON,
    
    /** Manager who manages operations */
    MANAGER,
    
    /** Admin with elevated privileges */
    ADMIN,
    
    /** Super admin with full system access */
    SUPER_ADMIN
}
