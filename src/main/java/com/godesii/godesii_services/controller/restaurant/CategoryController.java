package com.godesii.godesii_services.controller.restaurant;

import com.godesii.godesii_services.common.APIResponse;
import com.godesii.godesii_services.constant.GoDesiiConstant;
import com.godesii.godesii_services.entity.restaurant.Category;
import com.godesii.godesii_services.service.restaurant.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(CategoryController.ENDPOINT)
@Tag(name = "Category API", description = "Manage menu categories")
public class CategoryController {

    public static final String ENDPOINT = GoDesiiConstant.API_VERSION + "/categories";

    private final CategoryService service;

    public CategoryController(CategoryService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Create a new category")
    public ResponseEntity<APIResponse<Category>> create(@RequestBody Category category) {
        Category created = service.create(category);

        APIResponse<Category> apiResponse = new APIResponse<>(
                HttpStatus.CREATED,
                created,
                GoDesiiConstant.SUCCESSFULLY_CREATED
        );

        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
    }

    @GetMapping
    @Operation(summary = "Get all categories")
    public ResponseEntity<APIResponse<List<Category>>> getAll() {
        List<Category> categories = service.getAll();

        APIResponse<List<Category>> apiResponse = new APIResponse<>(
                HttpStatus.OK,
                categories,
                GoDesiiConstant.SUCCESSFULLY_FETCHED
        );

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID")
    public ResponseEntity<APIResponse<Category>> getById(@PathVariable Long id) {
        Category category = service.getById(id);

        if (category == null) {
            APIResponse<Category> apiResponse = new APIResponse<>(
                    HttpStatus.NOT_FOUND,
                    null,
                    GoDesiiConstant.NOT_FOUND
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
        }

        APIResponse<Category> apiResponse = new APIResponse<>(
                HttpStatus.OK,
                category,
                GoDesiiConstant.SUCCESSFULLY_FETCHED
        );

        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update category")
    public ResponseEntity<APIResponse<Category>> update(@PathVariable Long id, @RequestBody Category category) {
        Category updated = service.update(id, category);

        if (updated == null) {
            APIResponse<Category> apiResponse = new APIResponse<>(
                    HttpStatus.NOT_FOUND,
                    null,
                    GoDesiiConstant.NOT_FOUND
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
        }

        APIResponse<Category> apiResponse = new APIResponse<>(
                HttpStatus.OK,
                updated,
                GoDesiiConstant.SUCCESSFULLY_UPDATED
        );

        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete category")
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
