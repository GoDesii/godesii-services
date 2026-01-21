package com.godesii.godesii_services.controller.restaurant;

import com.godesii.godesii_services.common.APIResponse;
import com.godesii.godesii_services.constant.GoDesiiConstant;
import com.godesii.godesii_services.dto.CategoryRequest;
import com.godesii.godesii_services.entity.restaurant.Category;
import com.godesii.godesii_services.service.restaurant.CategoryService;
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
@RequestMapping(CategoryController.ENDPOINT)
@Tag(name = "Category API", description = "Manage menu categories with full CRUD operations")
public class CategoryController {

    public static final String ENDPOINT = GoDesiiConstant.API_VERSION + "/categories";

    private final CategoryService service;

    public CategoryController(CategoryService service) {
        this.service = service;
    }

    /**
     * Create a new category
     * 
     * @param request Validated category creation request
     * @return Created category with 201 status
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create a new category", description = "Creates a new category with validated data")
    public ResponseEntity<APIResponse<Category>> create(@Valid @RequestBody CategoryRequest request) {
        Category created = service.create(request);

        APIResponse<Category> apiResponse = new APIResponse<>(
                HttpStatus.CREATED,
                created,
                GoDesiiConstant.SUCCESSFULLY_CREATED
        );

        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
    }

    /**
     * Get all categories
     * 
     * @return List of all categories
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all categories", description = "Retrieves all categories")
    public ResponseEntity<APIResponse<List<Category>>> getAll() {
        List<Category> categories = service.getAll();

        APIResponse<List<Category>> apiResponse = new APIResponse<>(
                HttpStatus.OK,
                categories,
                GoDesiiConstant.SUCCESSFULLY_FETCHED
        );

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Get category by ID
     * 
     * @param id Category ID
     * @return Category entity or 404 if not found
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get category by ID", description = "Retrieves a single category by its ID")
    public ResponseEntity<APIResponse<Category>> getById(@PathVariable @NonNull Long id) {
        Category category = service.getById(id);

        APIResponse<Category> apiResponse = new APIResponse<>(
                HttpStatus.OK,
                category,
                GoDesiiConstant.SUCCESSFULLY_FETCHED
        );

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Get categories by menu ID
     * 
     * @param menuId Menu ID
     * @return List of categories for the specified menu
     */
    @GetMapping(value = "/menu/{menuId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get categories by menu ID", description = "Retrieves all categories for a specific menu")
    public ResponseEntity<APIResponse<List<Category>>> getByMenuId(@PathVariable @NonNull Long menuId) {
        List<Category> categories = service.getByMenuId(menuId);

        APIResponse<List<Category>> apiResponse = new APIResponse<>(
                HttpStatus.OK,
                categories,
                GoDesiiConstant.SUCCESSFULLY_FETCHED
        );

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Update category details
     * 
     * @param id      Category ID
     * @param request Validated category update request
     * @return Updated category or 404 if not found
     */
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update category", description = "Updates an existing category's details")
    public ResponseEntity<APIResponse<Category>> update(
            @PathVariable @NonNull Long id,
            @Valid @RequestBody CategoryRequest request) {

        Category updated = service.update(id, request);

        APIResponse<Category> apiResponse = new APIResponse<>(
                HttpStatus.OK,
                updated,
                GoDesiiConstant.SUCCESSFULLY_UPDATED
        );

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Delete category by ID
     * 
     * @param id Category ID
     * @return 204 No Content on success
     */
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Delete category", description = "Deletes a category by its ID")
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