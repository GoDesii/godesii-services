package com.godesii.godesii_services.controller.search;

import com.godesii.godesii_services.common.APIResponse;
import com.godesii.godesii_services.constant.GoDesiiConstant;
import com.godesii.godesii_services.dto.search.SearchRequest;
import com.godesii.godesii_services.dto.search.SearchResponse;
import com.godesii.godesii_services.service.search.GlobalSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping(SearchController.ENDPOINT)
@Tag(name = "Search API", description = "Global search across restaurants and menu items with fuzzy matching, synonyms, and geospatial filtering")
public class SearchController {

    public static final String ENDPOINT = GoDesiiConstant.API_VERSION + "/search";

    private final GlobalSearchService searchService;

    public SearchController(GlobalSearchService searchService) {
        this.searchService = searchService;
    }

    /**
     * Global search endpoint.
     *
     * Searches across restaurants (by name, cuisine, description) and menu items
     * (by name, description) with:
     * - Fuzzy matching for typo tolerance
     * - Synonym expansion (e.g., "murg" → "chicken")
     * - Geospatial filtering by lat/lng radius
     * - Dietary type filtering (VEG, NON_VEG, VEGAN)
     * - Price ceiling filtering
     * - Time-based relevance (open restaurants boosted)
     * - Multi-factor ranking (relevance + distance + availability)
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Global search", description = "Search restaurants and menu items with fuzzy matching, synonym support, location filtering, and intelligent ranking")
    public ResponseEntity<APIResponse<SearchResponse>> search(
            @Parameter(description = "Search query (e.g., 'chicken biryani', 'pizza under 200')") @RequestParam(name = "q", defaultValue = "") String query,

            @Parameter(description = "User latitude for distance calculation and geo-filtering") @RequestParam(name = "lat", required = false) Double latitude,

            @Parameter(description = "User longitude for distance calculation and geo-filtering") @RequestParam(name = "lng", required = false) Double longitude,

            @Parameter(description = "Search radius in kilometers (default: 5.0)") @RequestParam(name = "radius", required = false, defaultValue = "5.0") Double radiusKm,

            @Parameter(description = "Dietary filter: VEG, NON_VEG, EGG, VEGAN") @RequestParam(name = "dietaryType", required = false) String dietaryType,

            @Parameter(description = "Maximum price filter") @RequestParam(name = "maxPrice", required = false) BigDecimal maxPrice,

            @Parameter(description = "Page number (0-indexed)") @RequestParam(name = "page", required = false, defaultValue = "0") int page,

            @Parameter(description = "Results per page") @RequestParam(name = "size", required = false, defaultValue = "20") int size,

            @Parameter(description = "Sort by: relevance, distance, price") @RequestParam(name = "sortBy", required = false, defaultValue = "relevance") String sortBy) {
        // Build search request
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setQuery(query);
        searchRequest.setLatitude(latitude);
        searchRequest.setLongitude(longitude);
        searchRequest.setRadiusKm(radiusKm);
        searchRequest.setDietaryType(dietaryType);
        searchRequest.setMaxPrice(maxPrice);
        searchRequest.setPage(page);
        searchRequest.setSize(size);
        searchRequest.setSortBy(sortBy);

        SearchResponse searchResponse = searchService.search(searchRequest);

        APIResponse<SearchResponse> apiResponse = new APIResponse<>(
                HttpStatus.OK,
                searchResponse,
                GoDesiiConstant.SUCCESSFULLY_FETCHED);

        return ResponseEntity.ok(apiResponse);
    }
}
