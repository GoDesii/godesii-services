package com.godesii.godesii_services.controller.restaurant;

import com.godesii.godesii_services.common.APIResponse;
import com.godesii.godesii_services.constant.GoDesiiConstant;
import com.godesii.godesii_services.dto.RestaurantRequest;
import com.godesii.godesii_services.entity.restaurant.Restaurant;
import com.godesii.godesii_services.service.restaurant.RestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(RestaurantController.ENDPOINT)
@Tag(name = "Restaurant API", description = "Manage restaurants with full CRUD operations")
public class RestaurantController {

    public static final String ENDPOINT = GoDesiiConstant.API_VERSION + "/restaurants";

    private final RestaurantService service;

    public RestaurantController(RestaurantService service) {
        this.service = service;
    }

    /**
     * Create a new restaurant
     * 
     * @param request Validated restaurant creation request
     * @return Created restaurant with 201 status
     */
    @PostMapping(value = "/create")
    @Operation(summary = "Create a new restaurant", description = "Creates a new restaurant with validated data")
    public ResponseEntity<APIResponse<Restaurant>> create(@Valid @RequestBody RestaurantRequest request) {
        Restaurant created = service.create(request);

        APIResponse<Restaurant> apiResponse = new APIResponse<>(
                HttpStatus.CREATED,
                created,
                GoDesiiConstant.SUCCESSFULLY_CREATED);

        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
    }

    /**
     * Get all restaurants with pagination
     * 
     * @param page      Page number (default: 0)
     * @param size      Page size (default: 10)
     * @param sortBy    Field to sort by (default: id)
     * @param direction Sort direction (default: asc)
     * @return Paginated list of restaurants
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all restaurants", description = "Retrieves paginated list of restaurants")
    public ResponseEntity<APIResponse<Page<Restaurant>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<Restaurant> restaurants = service.getAll(pageable);

        APIResponse<Page<Restaurant>> apiResponse = new APIResponse<>(
                HttpStatus.OK,
                restaurants,
                GoDesiiConstant.SUCCESSFULLY_FETCHED);

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Get restaurant by ID
     * 
     * @param id Restaurant ID
     * @return Restaurant entity or 404 if not found
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get restaurant by ID", description = "Retrieves a single restaurant by its ID")
    public ResponseEntity<APIResponse<Restaurant>> getById(@PathVariable @NonNull Long id) {
        Restaurant restaurant = service.getById(id);

        APIResponse<Restaurant> apiResponse = new APIResponse<>(
                HttpStatus.OK,
                restaurant,
                GoDesiiConstant.SUCCESSFULLY_FETCHED);

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Update restaurant details
     * 
     * @param id      Restaurant ID
     * @param request Validated restaurant update request
     * @return Updated restaurant or 404 if not found
     */
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update restaurant", description = "Updates an existing restaurant's details")
    public ResponseEntity<APIResponse<Restaurant>> update(
            @PathVariable @NonNull Long id,
            @Valid @RequestBody RestaurantRequest request) {

        Restaurant updated = service.update(id, request);

        APIResponse<Restaurant> apiResponse = new APIResponse<>(
                HttpStatus.OK,
                updated,
                GoDesiiConstant.SUCCESSFULLY_UPDATED);

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Delete restaurant by ID
     * 
     * @param id Restaurant ID
     * @return 204 No Content on success
     */
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Delete restaurant", description = "Deletes a restaurant by its ID")
    public ResponseEntity<APIResponse<Void>> delete(@PathVariable @NonNull Long id) {
        service.delete(id);

        APIResponse<Void> apiResponse = new APIResponse<>(
                HttpStatus.NO_CONTENT,
                null,
                GoDesiiConstant.SUCCESSFULLY_DELETED);

        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
    }
}
