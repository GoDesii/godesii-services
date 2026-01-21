package com.godesii.godesii_services.controller.order;

import com.godesii.godesii_services.common.APIResponse;
import com.godesii.godesii_services.constant.GoDesiiConstant;
import com.godesii.godesii_services.dto.OrderItemRequest;
import com.godesii.godesii_services.entity.order.OrderItem;
import com.godesii.godesii_services.service.order.OrderItemService;
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
@RequestMapping(OrderItemController.ENDPOINT)
@Tag(name = "Order Item API", description = "Manage order items with full CRUD operations")
public class OrderItemController {

    public static final String ENDPOINT = GoDesiiConstant.API_VERSION + "/order-items";

    private final OrderItemService service;

    public OrderItemController(OrderItemService service) {
        this.service = service;
    }

    /**
     * Create a new order item
     * 
     * @param request Validated order item creation request
     * @return Created order item with 201 status
     */
    @PostMapping(value = "/create")
    @Operation(summary = "Create a new order item", description = "Creates a new order item with validated data")
    public ResponseEntity<APIResponse<OrderItem>> create(@Valid @RequestBody OrderItemRequest request) {
        OrderItem created = service.create(request);

        APIResponse<OrderItem> apiResponse = new APIResponse<>(
                HttpStatus.CREATED,
                created,
                GoDesiiConstant.SUCCESSFULLY_CREATED);

        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
    }

    /**
     * Get all order items with pagination
     * 
     * @param page      Page number (default: 0)
     * @param size      Page size (default: 10)
     * @param sortBy    Field to sort by (default: orderItemId)
     * @param direction Sort direction (default: asc)
     * @return Paginated list of order items
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all order items", description = "Retrieves paginated list of order items")
    public ResponseEntity<APIResponse<Page<OrderItem>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "orderItemId") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<OrderItem> orderItems = service.getAll(pageable);

        APIResponse<Page<OrderItem>> apiResponse = new APIResponse<>(
                HttpStatus.OK,
                orderItems,
                GoDesiiConstant.SUCCESSFULLY_FETCHED);

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Get order item by ID
     * 
     * @param id Order item ID
     * @return OrderItem entity or 404 if not found
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get order item by ID", description = "Retrieves a single order item by its ID")
    public ResponseEntity<APIResponse<OrderItem>> getById(@PathVariable @NonNull String id) {
        OrderItem orderItem = service.getById(id);

        APIResponse<OrderItem> apiResponse = new APIResponse<>(
                HttpStatus.OK,
                orderItem,
                GoDesiiConstant.SUCCESSFULLY_FETCHED);

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Update order item details
     * 
     * @param id      Order item ID
     * @param request Validated order item update request
     * @return Updated order item or 404 if not found
     */
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update order item", description = "Updates an existing order item's details")
    public ResponseEntity<APIResponse<OrderItem>> update(
            @PathVariable @NonNull String id,
            @Valid @RequestBody OrderItemRequest request) {

        OrderItem updated = service.update(id, request);

        APIResponse<OrderItem> apiResponse = new APIResponse<>(
                HttpStatus.OK,
                updated,
                GoDesiiConstant.SUCCESSFULLY_UPDATED);

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Delete order item by ID
     * 
     * @param id Order item ID
     * @return 204 No Content on success
     */
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Delete order item", description = "Deletes an order item by its ID")
    public ResponseEntity<APIResponse<Void>> delete(@PathVariable @NonNull String id) {
        service.delete(id);

        APIResponse<Void> apiResponse = new APIResponse<>(
                HttpStatus.NO_CONTENT,
                null,
                GoDesiiConstant.SUCCESSFULLY_DELETED);

        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
    }
}
