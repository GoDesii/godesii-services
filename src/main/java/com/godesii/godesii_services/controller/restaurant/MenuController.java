package com.godesii.godesii_services.controller.restaurant;

import com.godesii.godesii_services.common.APIResponse;
import com.godesii.godesii_services.constant.GoDesiiConstant;
import com.godesii.godesii_services.dto.MenuRequest;
import com.godesii.godesii_services.entity.restaurant.Menu;
import com.godesii.godesii_services.service.restaurant.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(MenuController.ENDPOINT)
@Tag(name = "Menu API", description = "Manage restaurant menus with full CRUD operations")
public class MenuController {

    public static final String ENDPOINT = GoDesiiConstant.API_VERSION + "/menus";

    private final MenuService service;

    public MenuController(MenuService service) {
        this.service = service;
    }

    /**
     * Create a new menu
     * 
     * @param request Validated menu creation request
     * @return Created menu with 201 status
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create a new menu", description = "Creates a new menu with validated data")
    public ResponseEntity<APIResponse<Menu>> create(@Valid @RequestBody MenuRequest request) {
        Menu created = service.create(request);

        APIResponse<Menu> apiResponse = new APIResponse<>(
                HttpStatus.CREATED,
                created,
                GoDesiiConstant.SUCCESSFULLY_CREATED
        );

        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
    }

    /**
     * Get all menus
     * 
     * @return List of all menus
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all menus", description = "Retrieves all menus")
    public ResponseEntity<APIResponse<List<Menu>>> getAll() {
        List<Menu> menus = service.getAll();

        APIResponse<List<Menu>> apiResponse = new APIResponse<>(
                HttpStatus.OK,
                menus,
                GoDesiiConstant.SUCCESSFULLY_FETCHED
        );

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Get menu by ID
     * 
     * @param id Menu ID
     * @return Menu entity or 404 if not found
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get menu by ID", description = "Retrieves a single menu by its ID")
    public ResponseEntity<APIResponse<Menu>> getById(@PathVariable @NonNull Long id) {
        Menu menu = service.getById(id);

        APIResponse<Menu> apiResponse = new APIResponse<>(
                HttpStatus.OK,
                menu,
                GoDesiiConstant.SUCCESSFULLY_FETCHED
        );

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Get menus by restaurant ID
     * 
     * @param restaurantId Restaurant ID
     * @return List of menus for the specified restaurant
     */
    @GetMapping(value = "/restaurant/{restaurantId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get menus by restaurant ID", description = "Retrieves all menus for a specific restaurant")
    public ResponseEntity<APIResponse<List<Menu>>> getByRestaurantId(@PathVariable @NonNull Long restaurantId) {
        List<Menu> menus = service.getByRestaurantId(restaurantId);

        APIResponse<List<Menu>> apiResponse = new APIResponse<>(
                HttpStatus.OK,
                menus,
                GoDesiiConstant.SUCCESSFULLY_FETCHED
        );

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Update menu details
     * 
     * @param id      Menu ID
     * @param request Validated menu update request
     * @return Updated menu or 404 if not found
     */
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update menu", description = "Updates an existing menu's details")
    public ResponseEntity<APIResponse<Menu>> update(
            @PathVariable @NonNull Long id,
            @Valid @RequestBody MenuRequest request) {

        Menu updated = service.update(id, request);

        APIResponse<Menu> apiResponse = new APIResponse<>(
                HttpStatus.OK,
                updated,
                GoDesiiConstant.SUCCESSFULLY_UPDATED
        );

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Delete menu by ID
     * 
     * @param id Menu ID
     * @return 204 No Content on success
     */
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Delete menu", description = "Deletes a menu by its ID")
    public ResponseEntity<APIResponse<Void>> delete(@PathVariable @NonNull Long id) {
        service.delete(id);

        APIResponse<Void> apiResponse = new APIResponse<>(
                HttpStatus.NO_CONTENT,
                null,
                GoDesiiConstant.SUCCESSFULLY_DELETED
        );

        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
    }
}
