package com.godesii.godesii_services.controller.order;

import com.godesii.godesii_services.common.APIResponse;
import com.godesii.godesii_services.constant.GoDesiiConstant;
import com.godesii.godesii_services.dto.CartItemRequest;
import com.godesii.godesii_services.entity.order.CartItem;
import com.godesii.godesii_services.service.order.CartItemService;
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
@RequestMapping(CartItemController.ENDPOINT)
@Tag(name = "Cart Item API", description = "Manage cart items with full CRUD operations")
public class CartItemController {

    public static final String ENDPOINT = GoDesiiConstant.API_VERSION + "/cart-items";

    private final CartItemService service;

    public CartItemController(CartItemService service) {
        this.service = service;
    }

    /**
     * Create a new cart item
     * 
     * @param request Validated cart item creation request
     * @return Created cart item with 201 status
     */
    @PostMapping(value = "/create")
    @Operation(summary = "Create a new cart item", description = "Creates a new cart item with validated data")
    public ResponseEntity<APIResponse<CartItem>> create(@Valid @RequestBody CartItemRequest request) {
        CartItem created = service.create(request);

        APIResponse<CartItem> apiResponse = new APIResponse<>(
                HttpStatus.CREATED,
                created,
                GoDesiiConstant.SUCCESSFULLY_CREATED);

        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
    }

    /**
     * Get all cart items with pagination
     * 
     * @param page      Page number (default: 0)
     * @param size      Page size (default: 10)
     * @param sortBy    Field to sort by (default: cartItemId)
     * @param direction Sort direction (default: asc)
     * @return Paginated list of cart items
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all cart items", description = "Retrieves paginated list of cart items")
    public ResponseEntity<APIResponse<Page<CartItem>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "cartItemId") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<CartItem> cartItems = service.getAll(pageable);

        APIResponse<Page<CartItem>> apiResponse = new APIResponse<>(
                HttpStatus.OK,
                cartItems,
                GoDesiiConstant.SUCCESSFULLY_FETCHED);

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Get cart item by ID
     * 
     * @param id Cart item ID
     * @return CartItem entity or 404 if not found
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get cart item by ID", description = "Retrieves a single cart item by its ID")
    public ResponseEntity<APIResponse<CartItem>> getById(@PathVariable @NonNull String id) {
        CartItem cartItem = service.getById(id);

        APIResponse<CartItem> apiResponse = new APIResponse<>(
                HttpStatus.OK,
                cartItem,
                GoDesiiConstant.SUCCESSFULLY_FETCHED);

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Update cart item details
     * 
     * @param id      Cart item ID
     * @param request Validated cart item update request
     * @return Updated cart item or 404 if not found
     */
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update cart item", description = "Updates an existing cart item's details")
    public ResponseEntity<APIResponse<CartItem>> update(
            @PathVariable @NonNull String id,
            @Valid @RequestBody CartItemRequest request) {

        CartItem updated = service.update(id, request);

        APIResponse<CartItem> apiResponse = new APIResponse<>(
                HttpStatus.OK,
                updated,
                GoDesiiConstant.SUCCESSFULLY_UPDATED);

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Delete cart item by ID
     * 
     * @param id Cart item ID
     * @return 204 No Content on success
     */
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Delete cart item", description = "Deletes a cart item by its ID")
    public ResponseEntity<APIResponse<Void>> delete(@PathVariable @NonNull String id) {
        service.delete(id);

        APIResponse<Void> apiResponse = new APIResponse<>(
                HttpStatus.NO_CONTENT,
                null,
                GoDesiiConstant.SUCCESSFULLY_DELETED);

        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
    }
}
