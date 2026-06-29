package com.godesii.godesii_services.dto;

/**
 * Lightweight DTO that carries only the restaurant ID and name.
 * Used for list/dropdown endpoints where the full Restaurant payload is unnecessary.
 */
public class RestaurantSummaryDto {

    private Long id;
    private String name;

    public RestaurantSummaryDto() {
    }

    public RestaurantSummaryDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
