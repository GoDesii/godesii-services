package com.godesii.godesii_services.entity.auth;

import java.util.EnumSet;
import java.util.Set;

import static com.godesii.godesii_services.entity.auth.Permission.*;

public enum Role {

    ADMIN(EnumSet.allOf(Permission.class)),

    MANGER(EnumSet.of(
            USER_READ,
            PRODUCT_READ, PRODUCT_UPDATE, PRODUCT_APPROVE,
            INVENTORY_READ, INVENTORY_UPDATE,
            ORDER_READ_ALL, ORDER_APPROVE, ORDER_ASSIGN_DELIVERY,
            PAYMENT_READ_ALL,
            REPORT_VIEW_USERS, REPORT_VIEW_ORDERS, REPORT_VIEW_SALES, REPORT_VIEW_REVENUE,
            SYSTEM_CONFIG_READ
    )),

    VENDOR(EnumSet.of(
            PRODUCT_CREATE, PRODUCT_READ, PRODUCT_UPDATE, PRODUCT_DELETE,
            INVENTORY_READ, INVENTORY_UPDATE,
            ORDER_READ, ORDER_UPDATE,
            PAYMENT_READ,
            REPORT_VIEW_SALES
    )),

    CUSTOMER(EnumSet.of(
            PRODUCT_READ,
            ORDER_CREATE, ORDER_READ, ORDER_CANCEL,
            PAYMENT_INITIATE, PAYMENT_READ,
            REVIEW_CREATE, REVIEW_READ,
            SUPPORT_TICKET_CREATE, SUPPORT_TICKET_READ
    )),

    RESTAURANT_PARTNER(EnumSet.of(
            RESTAURANT_CREATE, RESTAURANT_UPDATE,
            PAYMENT_READ,
            REPORT_VIEW_SALES,
            PRODUCT_CREATE, PRODUCT_READ, PRODUCT_UPDATE, PRODUCT_DELETE,
            ORDER_READ, ORDER_UPDATE
    )),

    DELIVERY_PERSON(EnumSet.of(
            DELIVERY_VIEW_ASSIGNED,
            DELIVERY_ACCEPT, DELIVERY_REJECT,
            DELIVERY_PICKUP, DELIVERY_IN_TRANSIT, DELIVERY_COMPLETE,
            ORDER_READ
    ));

    private final Set<Permission> permissions;

    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }
}

