package com.godesii.godesii_services.controller.restaurant;

import com.godesii.godesii_services.common.APIResponse;
import com.godesii.godesii_services.constant.GoDesiiConstant;
import com.godesii.godesii_services.dto.MenuItemRequest;
import com.godesii.godesii_services.dto.MenuItemResponse;
import com.godesii.godesii_services.entity.restaurant.MenuItem;
import com.godesii.godesii_services.service.restaurant.MenuItemService;
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
@RequestMapping(MenuItemController.ENDPOINT)
@Tag(name = "Menu Item API", description = "Manage menu items with full CRUD operations")
public class MenuItemController {

    public static final String ENDPOINT = GoDesiiConstant.API_VERSION + "/menu-items";

    private final MenuItemService service;

    public MenuItemController(MenuItemService service) {
        this.service = service;
    }

    /**
     * Create a new menu item
     * 
     * @param request Validated menu item creation request
     * @return Created menu item with 201 status
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create a new menu item", description = "Creates a new menu item with validated data")
    public ResponseEntity<APIResponse<MenuItemResponse>> create(@Valid @RequestBody MenuItemRequest request) {
        MenuItemResponse created = service.create(request);

        APIResponse<MenuItemResponse> apiResponse = new APIResponse<>(
                HttpStatus.CREATED,
                created,
                GoDesiiConstant.SUCCESSFULLY_CREATED
        );

        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
    }

    /**
     * Get all menu items
     * 
     * @return List of all menu items
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all menu items", description = "Retrieves all menu items")
    public ResponseEntity<APIResponse<List<MenuItemResponse>>> getAll() {
        List<MenuItemResponse> menuItems = service.getAll();

        APIResponse<List<MenuItemResponse>> apiResponse = new APIResponse<>(
                HttpStatus.OK,
                menuItems,
                GoDesiiConstant.SUCCESSFULLY_FETCHED
        );

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Get menu item by ID
     * 
     * @param id Menu item ID (UUID)
     * @return Menu item entity or 404 if not found
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get menu item by ID", description = "Retrieves a single menu item by its ID")
    public ResponseEntity<APIResponse<MenuItemResponse>> getById(@PathVariable @NonNull String id) {
        MenuItemResponse menuItem = service.getById(id);

        APIResponse<MenuItemResponse> apiResponse = new APIResponse<>(
                HttpStatus.OK,
                menuItem,
                GoDesiiConstant.SUCCESSFULLY_FETCHED
        );

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Get menu items by category ID
     * 
     * @param categoryId Category ID
     * @return List of menu items for the specified category
     */
    @GetMapping(value = "/category/{categoryId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get menu items by category ID", description = "Retrieves all menu items for a specific category")
    public ResponseEntity<APIResponse<List<MenuItemResponse>>> getByCategoryId(@PathVariable @NonNull Long categoryId) {
        List<MenuItemResponse> menuItems = service.getByCategoryId(categoryId);

        APIResponse<List<MenuItemResponse>> apiResponse = new APIResponse<>(
                HttpStatus.OK,
                menuItems,
                GoDesiiConstant.SUCCESSFULLY_FETCHED
        );

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Update menu item details
     * 
     * @param id      Menu item ID (UUID)
     * @param request Validated menu item update request
     * @return Updated menu item or 404 if not found
     */
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update menu item", description = "Updates an existing menu item's details")
    public ResponseEntity<APIResponse<MenuItemResponse>> update(
            @PathVariable @NonNull String id,
            @Valid @RequestBody MenuItemRequest request) {

        MenuItemResponse updated = service.update(id, request);

        APIResponse<MenuItemResponse> apiResponse = new APIResponse<>(
                HttpStatus.OK,
                updated,
                GoDesiiConstant.SUCCESSFULLY_UPDATED
        );

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Delete menu item by ID
     * 
     * @param id Menu item ID (UUID)
     * @return 204 No Content on success
     */
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Delete menu item", description = "Deletes a menu item by its ID")
    public ResponseEntity<APIResponse<Void>> delete(@PathVariable @NonNull String id) {
        service.delete(id);

        APIResponse<Void> apiResponse = new APIResponse<>(
                HttpStatus.NO_CONTENT,
                null,
                GoDesiiConstant.SUCCESSFULLY_DELETED
        );

        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
    }
}
