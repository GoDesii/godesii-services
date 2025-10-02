package com.godesii.godesii_services.entity.restaurant;

import java.util.List;

public class MenuItem {
    private Long id;
    private String menuName;
    private String cuisine;
    private String price;
    private List<String> imageUrl;
    private boolean isAvailable;
    private String Description;
    private String ingredients;
    private String menuType;
    // many to one
    private Restaurant restaurant;
}
