package com.godesii.godesii_services.controller.restaurant;

import com.godesii.godesii_services.common.APIResponse;
import com.godesii.godesii_services.constant.GoDesiiConstant;
import com.godesii.godesii_services.entity.restaurant.MenuItem;
import com.godesii.godesii_services.service.restaurant.MenuItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(MenuItemController.ENDPOINT)
@Tag(name = "Menu Item API", description = "Manage restaurant menu items")
public class MenuItemController {

    public static final String ENDPOINT = GoDesiiConstant.API_VERSION + "/menu-items";

    private final MenuItemService service;

    public MenuItemController(MenuItemService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Create a new menu item")
    public ResponseEntity<APIResponse<MenuItem>> create(@RequestBody MenuItem menuItem) {
        MenuItem created = service.create(menuItem);

        APIResponse<MenuItem> apiResponse = new APIResponse<>(
                HttpStatus.CREATED,
                created,
                GoDesiiConstant.SUCCESSFULLY_CREATED
        );

        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
    }

    @GetMapping("/get/{id}")
    @Operation(summary = "Get menu item by ID")
    public ResponseEntity<APIResponse<MenuItem>> getByMenuId(@PathVariable Long id) {
        MenuItem item = service.getByMenuId(id);

        if (item == null) {
            APIResponse<MenuItem> apiResponse = new APIResponse<>(
                    HttpStatus.NOT_FOUND,
                    null,
                    GoDesiiConstant.NOT_FOUND
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
        }

        APIResponse<MenuItem> apiResponse = new APIResponse<>(
                HttpStatus.OK,
                item,
                GoDesiiConstant.SUCCESSFULLY_FETCHED
        );

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping
    @Operation(summary = "Get all menu items with pagination")
    public ResponseEntity<APIResponse<Page<MenuItem>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<MenuItem> items = service.getAll(pageable);

        APIResponse<Page<MenuItem>> apiResponse = new APIResponse<>(
                HttpStatus.OK,
                items,
                GoDesiiConstant.SUCCESSFULLY_FETCHED
        );

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{restaurantId}")
    @Operation(summary = "Get menu items by restaurant ID with pagination")
    public ResponseEntity<APIResponse<Page<MenuItem>>> getByRestaurantId(
            @PathVariable Long restaurantId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "2") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<MenuItem> items = service.getByRestaurantId(restaurantId, pageable);

        APIResponse<Page<MenuItem>> apiResponse = new APIResponse<>(
                HttpStatus.OK,
                items,
                GoDesiiConstant.SUCCESSFULLY_FETCHED
        );

        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a menu item")
    public ResponseEntity<APIResponse<MenuItem>> update(@PathVariable Long id, @RequestBody MenuItem menuItem) {
        MenuItem updated = service.update(id, menuItem);

        if (updated == null) {
            APIResponse<MenuItem> apiResponse = new APIResponse<>(
                    HttpStatus.NOT_FOUND,
                    null,
                    GoDesiiConstant.NOT_FOUND
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
        }

        APIResponse<MenuItem> apiResponse = new APIResponse<>(
                HttpStatus.OK,
                updated,
                GoDesiiConstant.SUCCESSFULLY_UPDATED
        );

        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a menu item")
    public ResponseEntity<APIResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);

        APIResponse<Void> apiResponse = new APIResponse<>(
                HttpStatus.NO_CONTENT,
                null,
                GoDesiiConstant.SUCCESSFULLY_DELETED
        );

        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
    }

    @GetMapping("/search")
    @Operation(summary = "Search menu items by restaurant name or menu name")
    public ResponseEntity<APIResponse<List<MenuItem>>> searchMenus(
            @RequestParam(required = false) String restaurantName,
            @RequestParam(required = false) String menuName) {

        List<MenuItem> menus = service.getMenus(restaurantName, menuName);

        if (menus.isEmpty()) {
            APIResponse<List<MenuItem>> apiResponse = new APIResponse<>(
                    HttpStatus.NOT_FOUND,
                    null,
                    GoDesiiConstant.NOT_FOUND
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
        }

        APIResponse<List<MenuItem>> apiResponse = new APIResponse<>(
                HttpStatus.OK,
                menus,
                GoDesiiConstant.SUCCESSFULLY_FETCHED
        );

        return ResponseEntity.ok(apiResponse);
    }
}
