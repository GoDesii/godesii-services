package com.godesii.godesii_services.service.search;

import com.godesii.godesii_services.dto.search.*;
import com.godesii.godesii_services.entity.restaurant.MenuItem;
import com.godesii.godesii_services.entity.restaurant.OperationalHour;
import com.godesii.godesii_services.entity.restaurant.Restaurant;
import com.godesii.godesii_services.entity.restaurant.RestaurantAddress;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class GlobalSearchService {

    private static final Logger log = LoggerFactory.getLogger(GlobalSearchService.class);
    private static final double EARTH_RADIUS_KM = 6371.0;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Perform a global search across restaurants and menu items.
     *
     * Implements: fuzzy matching, synonym expansion (via analyzer), geospatial
     * filtering,
     * dietary filtering, price filtering, time-based relevance, and multi-factor
     * ranking.
     */
    public SearchResponse search(SearchRequest request) {
        long startTime = System.currentTimeMillis();
        SearchSession searchSession = Search.session(entityManager);

        String query = request.getQuery() != null ? request.getQuery().trim() : "";

        // --- 1. SEARCH RESTAURANTS ---
        List<RestaurantSearchResult> restaurantResults = searchRestaurants(searchSession, query, request);

        // --- 2. SEARCH MENU ITEMS ---
        List<MenuItemSearchResult> menuItemResults = searchMenuItems(searchSession, query, request);

        // --- 3. BUILD RESPONSE ---
        SearchResponse response = new SearchResponse();
        response.setQuery(query);
        response.setRestaurants(restaurantResults);
        response.setMenuItems(menuItemResults);
        response.setTotalRestaurants(restaurantResults.size());
        response.setTotalMenuItems(menuItemResults.size());
        response.setSearchTimeMs(System.currentTimeMillis() - startTime);

        log.info("Search '{}': {} restaurants, {} menu items in {}ms",
                query, restaurantResults.size(), menuItemResults.size(), response.getSearchTimeMs());

        return response;
    }

    // ========================================================================
    // RESTAURANT SEARCH
    // ========================================================================

    private List<RestaurantSearchResult> searchRestaurants(SearchSession searchSession, String query,
            SearchRequest request) {
        if (query.isEmpty()) {
            return Collections.emptyList();
        }

        int fetchSize = Math.max(request.getSize() * 3, 60); // Fetch more to allow post-filtering

        SearchResult<Restaurant> result = searchSession.search(Restaurant.class)
                .where(f -> f.bool()
                        // Must match the query in at least one text field
                        .must(f.bool()
                                .should(f.match().field("name").matching(query).fuzzy(2).boost(3.0f))
                                .should(f.match().field("cuisineType").matching(query).fuzzy(1).boost(2.0f))
                                .should(f.match().field("description").matching(query).fuzzy(2).boost(1.0f))
                                .should(f.match().field("address.city").matching(query).fuzzy(1).boost(1.5f)))
                        // Only active restaurants
                        .must(f.match().field("isActive").matching(true)))
                .fetch(fetchSize);

        List<Restaurant> restaurants = result.hits();

        // Post-process: compute distance, check open status, apply geo filter, rank
        return restaurants.stream()
                .map(r -> toRestaurantResult(r, request))
                .filter(Objects::nonNull) // Filtered out by geo radius
                .sorted(getRestaurantComparator(request.getSortBy()))
                .limit(request.getSize())
                .collect(Collectors.toList());
    }

    private RestaurantSearchResult toRestaurantResult(Restaurant restaurant, SearchRequest request) {
        RestaurantSearchResult result = new RestaurantSearchResult();
        result.setId(restaurant.getId());
        result.setName(restaurant.getName());
        result.setCuisineType(restaurant.getCuisineType());
        result.setDescription(restaurant.getDescription());
        result.setActive(restaurant.isActive());

        // City
        if (restaurant.getAddress() != null) {
            result.setCity(restaurant.getAddress().getCity());
        }

        // Distance calculation
        Double distance = calculateDistance(restaurant.getAddress(), request.getLatitude(), request.getLongitude());
        result.setDistanceKm(distance);

        // Geo filter: exclude restaurants outside radius
        if (distance != null && request.getRadiusKm() != null && distance > request.getRadiusKm()) {
            return null;
        }

        // Check if currently open
        result.setOpen(isRestaurantOpen(restaurant));

        // Compute composite relevance score
        result.setRelevanceScore(computeRestaurantScore(result));

        return result;
    }

    // ========================================================================
    // MENU ITEM SEARCH
    // ========================================================================

    private List<MenuItemSearchResult> searchMenuItems(SearchSession searchSession, String query,
            SearchRequest request) {
        if (query.isEmpty()) {
            return Collections.emptyList();
        }

        int fetchSize = Math.max(request.getSize() * 3, 60);

        SearchResult<MenuItem> result = searchSession.search(MenuItem.class)
                .where(f -> {
                    var bool = f.bool()
                            // Must match the query in menu item text fields
                            .must(f.bool()
                                    .should(f.match().field("name").matching(query).fuzzy(2).boost(3.0f))
                                    .should(f.match().field("description").matching(query).fuzzy(2).boost(1.5f)))
                            // Only available items
                            .must(f.match().field("isAvailable").matching(true));

                    // Dietary type filter
                    if (request.getDietaryType() != null && !request.getDietaryType().isEmpty()) {
                        bool = bool.must(f.match().field("dietaryType").matching(request.getDietaryType()));
                    }

                    // Price filter
                    if (request.getMaxPrice() != null) {
                        bool = bool.must(f.range().field("basePrice").atMost(request.getMaxPrice()));
                    }

                    return bool;
                })
                .fetch(fetchSize);

        List<MenuItem> menuItems = result.hits();

        return menuItems.stream()
                .map(item -> toMenuItemResult(item, request))
                .filter(Objects::nonNull) // Filtered out by geo radius
                .sorted(getMenuItemComparator(request.getSortBy()))
                .limit(request.getSize())
                .collect(Collectors.toList());
    }

    private MenuItemSearchResult toMenuItemResult(MenuItem item, SearchRequest request) {
        MenuItemSearchResult result = new MenuItemSearchResult();
        result.setItemId(item.getItemId());
        result.setName(item.getName());
        result.setDescription(item.getDescription());
        result.setBasePrice(item.getBasePrice());
        result.setDietaryType(item.getDietaryType());
        result.setImageUrl(item.getImageUrl());
        result.setAvailable(item.isAvailable());

        // Navigate up: MenuItem → Category → Menu → Restaurant
        Restaurant restaurant = null;
        if (item.getCategory() != null && item.getCategory().getMenu() != null) {
            restaurant = item.getCategory().getMenu().getRestaurant();
            result.setCategoryName(item.getCategory().getName());
        }

        if (restaurant != null) {
            result.setRestaurantId(restaurant.getId());
            result.setRestaurantName(restaurant.getName());

            // Skip inactive restaurants
            if (!restaurant.isActive()) {
                return null;
            }

            // Distance calculation
            Double distance = calculateDistance(restaurant.getAddress(), request.getLatitude(), request.getLongitude());
            result.setDistanceKm(distance);

            // Geo filter
            if (distance != null && request.getRadiusKm() != null && distance > request.getRadiusKm()) {
                return null;
            }
        }

        // Composite relevance score
        result.setRelevanceScore(computeMenuItemScore(result));

        return result;
    }

    // ========================================================================
    // SCORING & RANKING
    // ========================================================================

    /**
     * Composite score for restaurants:
     * - Base score: 10.0
     * - Open bonus: +5.0 (time-based relevance)
     * - Distance penalty: closer = higher (max -3.0 penalty)
     */
    private double computeRestaurantScore(RestaurantSearchResult result) {
        double score = 10.0;

        // Open now bonus (time-based relevance)
        if (result.isOpen()) {
            score += 5.0;
        }

        // Distance penalty (closer is better)
        if (result.getDistanceKm() != null) {
            score -= Math.min(result.getDistanceKm() * 0.6, 3.0);
        }

        return Math.max(score, 0.0);
    }

    /**
     * Composite score for menu items:
     * - Base score: 10.0
     * - Lower price = slight bonus
     * - Distance penalty
     */
    private double computeMenuItemScore(MenuItemSearchResult result) {
        double score = 10.0;

        // Price factor: cheaper items get a slight boost
        if (result.getBasePrice() != null) {
            double priceVal = result.getBasePrice().doubleValue();
            if (priceVal < 100)
                score += 2.0;
            else if (priceVal < 200)
                score += 1.0;
            else if (priceVal < 500)
                score += 0.5;
        }

        // Distance penalty
        if (result.getDistanceKm() != null) {
            score -= Math.min(result.getDistanceKm() * 0.6, 3.0);
        }

        return Math.max(score, 0.0);
    }

    private Comparator<RestaurantSearchResult> getRestaurantComparator(String sortBy) {
        if ("distance".equalsIgnoreCase(sortBy)) {
            return Comparator.comparing(RestaurantSearchResult::getDistanceKm,
                    Comparator.nullsLast(Comparator.naturalOrder()));
        }
        // Default: relevance (composite score descending)
        return Comparator.comparingDouble(RestaurantSearchResult::getRelevanceScore).reversed();
    }

    private Comparator<MenuItemSearchResult> getMenuItemComparator(String sortBy) {
        if ("distance".equalsIgnoreCase(sortBy)) {
            return Comparator.comparing(MenuItemSearchResult::getDistanceKm,
                    Comparator.nullsLast(Comparator.naturalOrder()));
        }
        if ("price".equalsIgnoreCase(sortBy)) {
            return Comparator.comparing(MenuItemSearchResult::getBasePrice,
                    Comparator.nullsLast(Comparator.naturalOrder()));
        }
        // Default: relevance
        return Comparator.comparingDouble(MenuItemSearchResult::getRelevanceScore).reversed();
    }

    // ========================================================================
    // GEOSPATIAL UTILITIES (Haversine formula)
    // ========================================================================

    private Double calculateDistance(RestaurantAddress address, Double userLat, Double userLng) {
        if (address == null || address.getLatitude() == null || address.getLongitude() == null
                || userLat == null || userLng == null) {
            return null;
        }

        double lat1 = Math.toRadians(userLat);
        double lat2 = Math.toRadians(address.getLatitude().doubleValue());
        double dLat = Math.toRadians(address.getLatitude().doubleValue() - userLat);
        double dLng = Math.toRadians(address.getLongitude().doubleValue() - userLng);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distance = EARTH_RADIUS_KM * c;
        return Math.round(distance * 100.0) / 100.0; // Round to 2 decimal places
    }

    // ========================================================================
    // TIME-BASED RELEVANCE
    // ========================================================================

    /**
     * Check if a restaurant is currently open based on its operational hours.
     */
    private boolean isRestaurantOpen(Restaurant restaurant) {
        if (restaurant.getOperatingHours() == null || restaurant.getOperatingHours().isEmpty()) {
            return true; // If no hours defined, assume open
        }

        LocalTime now = LocalTime.now();
        int todayDayIndex = java.time.LocalDate.now().getDayOfWeek().getValue() % 7; // 0=Sunday

        for (OperationalHour oh : restaurant.getOperatingHours()) {
            if (oh.getDayOfWeek() != null && oh.getDayOfWeek().ordinal() == todayDayIndex) {
                if (oh.getOpenTime() != null && oh.getCloseTime() != null) {
                    // Handle overnight hours (e.g., 22:00 - 02:00)
                    if (oh.getCloseTime().isBefore(oh.getOpenTime())) {
                        if (now.isAfter(oh.getOpenTime()) || now.isBefore(oh.getCloseTime())) {
                            return true;
                        }
                    } else {
                        if (!now.isBefore(oh.getOpenTime()) && now.isBefore(oh.getCloseTime())) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }
}
