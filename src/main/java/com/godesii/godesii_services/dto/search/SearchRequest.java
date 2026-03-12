package com.godesii.godesii_services.dto.search;

import java.math.BigDecimal;

public class SearchRequest {

    private String query; // "Chicken Biryani", "pizza under 200"
    private Double latitude; // User's current latitude
    private Double longitude; // User's current longitude
    private Double radiusKm = 5.0; // Search radius in km (default 5km)
    private String dietaryType; // Optional: VEG, NON_VEG, EGG, VEGAN
    private BigDecimal maxPrice; // Optional: price ceiling filter
    private int page = 0; // Pagination: page number
    private int size = 20; // Pagination: items per page
    private String sortBy = "relevance"; // relevance, distance, price

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getRadiusKm() {
        return radiusKm;
    }

    public void setRadiusKm(Double radiusKm) {
        this.radiusKm = radiusKm;
    }

    public String getDietaryType() {
        return dietaryType;
    }

    public void setDietaryType(String dietaryType) {
        this.dietaryType = dietaryType;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }
}
