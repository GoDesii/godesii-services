package com.godesii.godesii_services.dto.search;

import java.util.List;

public class SearchResponse {

    private String query;
    private List<RestaurantSearchResult> restaurants;
    private List<MenuItemSearchResult> menuItems;
    private int totalRestaurants;
    private int totalMenuItems;
    private long searchTimeMs;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<RestaurantSearchResult> getRestaurants() {
        return restaurants;
    }

    public void setRestaurants(List<RestaurantSearchResult> restaurants) {
        this.restaurants = restaurants;
    }

    public List<MenuItemSearchResult> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(List<MenuItemSearchResult> menuItems) {
        this.menuItems = menuItems;
    }

    public int getTotalRestaurants() {
        return totalRestaurants;
    }

    public void setTotalRestaurants(int totalRestaurants) {
        this.totalRestaurants = totalRestaurants;
    }

    public int getTotalMenuItems() {
        return totalMenuItems;
    }

    public void setTotalMenuItems(int totalMenuItems) {
        this.totalMenuItems = totalMenuItems;
    }

    public long getSearchTimeMs() {
        return searchTimeMs;
    }

    public void setSearchTimeMs(long searchTimeMs) {
        this.searchTimeMs = searchTimeMs;
    }
}
