package com.godesii.godesii_services.controller.restaurant;

import com.godesii.godesii_services.common.APIResponse;
import com.godesii.godesii_services.constant.GoDesiiConstant;
import com.godesii.godesii_services.entity.restaurant.Restaurant;
import com.godesii.godesii_services.service.restaurant.RestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(RestaurantController.ENDPOINT)
@Tag(name = "Restaurant API", description = "Manage restaurants")
public class RestaurantController {

    public static final String ENDPOINT = GoDesiiConstant.API_VERSION + "/restaurants";

    private final RestaurantService service;

    public RestaurantController(RestaurantService service) {
        this.service = service;
    }

    @PostMapping("/create")
    @Operation(summary = "Create a new restaurant")
    public ResponseEntity<APIResponse<Restaurant>> create(@RequestBody Restaurant restaurant) {
        Restaurant created = service.create(restaurant);

        APIResponse<Restaurant> apiResponse = new APIResponse<>(
                HttpStatus.CREATED,
                created,
                GoDesiiConstant.SUCCESSFULLY_CREATED
        );

        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
    }

    @GetMapping
    @Operation(summary = "Get all restaurants with pagination")
    public ResponseEntity<APIResponse<Page<Restaurant>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "2") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<Restaurant> restaurants = service.getAll(pageable);

        APIResponse<Page<Restaurant>> apiResponse = new APIResponse<>(
                HttpStatus.OK,
                restaurants,
                GoDesiiConstant.SUCCESSFULLY_FETCHED
        );

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get restaurant by ID")
    public ResponseEntity<APIResponse<Restaurant>> getById(@PathVariable Long id) {
        Restaurant restaurant = service.getById(id);

        if (restaurant == null) {
            APIResponse<Restaurant> apiResponse = new APIResponse<>(
                    HttpStatus.NOT_FOUND,
                    null,
                    GoDesiiConstant.NOT_FOUND
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
        }

        APIResponse<Restaurant> apiResponse = new APIResponse<>(
                HttpStatus.OK,
                restaurant,
                GoDesiiConstant.SUCCESSFULLY_FETCHED
        );

        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update restaurant details")
    public ResponseEntity<APIResponse<Restaurant>> update(
            @PathVariable Long id,
            @RequestBody Restaurant restaurant) {

        Restaurant updated = service.update(id, restaurant);

        if (updated == null) {
            APIResponse<Restaurant> apiResponse = new APIResponse<>(
                    HttpStatus.NOT_FOUND,
                    null,
                    GoDesiiConstant.NOT_FOUND
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
        }

        APIResponse<Restaurant> apiResponse = new APIResponse<>(
                HttpStatus.OK,
                updated,
                GoDesiiConstant.SUCCESSFULLY_UPDATED
        );

        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete restaurant by ID")
    public ResponseEntity<APIResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);

        APIResponse<Void> apiResponse = new APIResponse<>(
                HttpStatus.NO_CONTENT,
                null,
                GoDesiiConstant.SUCCESSFULLY_DELETED
        );

        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
    }
}
