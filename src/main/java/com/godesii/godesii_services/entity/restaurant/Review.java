package com.godesii.godesii_services.entity.restaurant;

public class Review {
    private Long id;
    private String rating;
    private String comment;
    //many to one
    private Restaurant restaurant;
}
