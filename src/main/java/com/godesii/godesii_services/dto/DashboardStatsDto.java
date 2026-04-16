package com.godesii.godesii_services.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class DashboardStatsDto {
    private long totalRestaurants;
    private long totalMenus;
    private long totalMenuItems;
    private long availableItems;

    public DashboardStatsDto(long totalRestaurants, long totalMenus, long totalMenuItems, long availableItems) {
        this.totalRestaurants = totalRestaurants;
        this.totalMenus = totalMenus;
        this.totalMenuItems = totalMenuItems;
        this.availableItems = availableItems;
    }

    public long getTotalRestaurants() {
        return totalRestaurants;
    }

    public void setTotalRestaurants(long totalRestaurants) {
        this.totalRestaurants = totalRestaurants;
    }

    public long getTotalMenus() {
        return totalMenus;
    }

    public void setTotalMenus(long totalMenus) {
        this.totalMenus = totalMenus;
    }

    public long getTotalMenuItems() {
        return totalMenuItems;
    }

    public void setTotalMenuItems(long totalMenuItems) {
        this.totalMenuItems = totalMenuItems;
    }

    public long getAvailableItems() {
        return availableItems;
    }

    public void setAvailableItems(long availableItems) {
        this.availableItems = availableItems;
    }
}
