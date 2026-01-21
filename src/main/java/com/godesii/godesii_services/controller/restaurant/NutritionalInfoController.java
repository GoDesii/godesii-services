package com.godesii.godesii_services.controller.restaurant;

import com.godesii.godesii_services.common.APIResponse;
import com.godesii.godesii_services.constant.GoDesiiConstant;
import com.godesii.godesii_services.dto.NutritionalInfoRequest;
import com.godesii.godesii_services.entity.restaurant.NutritionalInfo;
import com.godesii.godesii_services.service.restaurant.NutritionalInfoService;
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
@RequestMapping(NutritionalInfoController.ENDPOINT)
@Tag(name = "Nutritional Info API", description = "Manage nutritional information with full CRUD operations")
public class NutritionalInfoController {

    public static final String ENDPOINT = GoDesiiConstant.API_VERSION + "/nutritional-info";

    private final NutritionalInfoService service;

    public NutritionalInfoController(NutritionalInfoService service) {
        this.service = service;
    }

    /**
     * Create new nutritional info
     * 
     * @param request Validated nutritional info creation request
     * @return Created nutritional info with 201 status
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create nutritional info", description = "Creates nutritional info for a menu item")
    public ResponseEntity<APIResponse<NutritionalInfo>> create(@Valid @RequestBody NutritionalInfoRequest request) {
        NutritionalInfo created = service.create(request);

        APIResponse<NutritionalInfo> apiResponse = new APIResponse<>(
                HttpStatus.CREATED,
                created,
                GoDesiiConstant.SUCCESSFULLY_CREATED
        );

        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
    }

    /**
     * Get all nutritional info
     * 
     * @return List of all nutritional info
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all nutritional info", description = "Retrieves all nutritional information")
    public ResponseEntity<APIResponse<List<NutritionalInfo>>> getAll() {
        List<NutritionalInfo> nutritionalInfoList = service.getAll();

        APIResponse<List<NutritionalInfo>> apiResponse = new APIResponse<>(
                HttpStatus.OK,
                nutritionalInfoList,
                GoDesiiConstant.SUCCESSFULLY_FETCHED
        );

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Get nutritional info by ID
     * 
     * @param id Nutritional info ID
     * @return Nutritional info entity or 404 if not found
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get nutritional info by ID", description = "Retrieves nutritional info by its ID")
    public ResponseEntity<APIResponse<NutritionalInfo>> getById(@PathVariable @NonNull Long id) {
        NutritionalInfo nutritionalInfo = service.getById(id);

        APIResponse<NutritionalInfo> apiResponse = new APIResponse<>(
                HttpStatus.OK,
                nutritionalInfo,
                GoDesiiConstant.SUCCESSFULLY_FETCHED
        );

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Get nutritional info by menu item ID
     * 
     * @param itemId Menu item ID (UUID)
     * @return Nutritional info for the specified menu item
     */
    @GetMapping(value = "/menu-item/{itemId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get nutritional info by menu item ID", description = "Retrieves nutritional info for a specific menu item")
    public ResponseEntity<APIResponse<NutritionalInfo>> getByMenuItemId(@PathVariable @NonNull String itemId) {
        NutritionalInfo nutritionalInfo = service.getByMenuItemId(itemId);

        APIResponse<NutritionalInfo> apiResponse = new APIResponse<>(
                HttpStatus.OK,
                nutritionalInfo,
                GoDesiiConstant.SUCCESSFULLY_FETCHED
        );

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Update nutritional info
     * 
     * @param id      Nutritional info ID
     * @param request Validated nutritional info update request
     * @return Updated nutritional info or 404 if not found
     */
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update nutritional info", description = "Updates existing nutritional information")
    public ResponseEntity<APIResponse<NutritionalInfo>> update(
            @PathVariable @NonNull Long id,
            @Valid @RequestBody NutritionalInfoRequest request) {

        NutritionalInfo updated = service.update(id, request);

        APIResponse<NutritionalInfo> apiResponse = new APIResponse<>(
                HttpStatus.OK,
                updated,
                GoDesiiConstant.SUCCESSFULLY_UPDATED
        );

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Delete nutritional info by ID
     * 
     * @param id Nutritional info ID
     * @return 204 No Content on success
     */
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Delete nutritional info", description = "Deletes nutritional info by its ID")
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
