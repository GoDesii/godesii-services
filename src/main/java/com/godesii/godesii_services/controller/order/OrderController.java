package com.godesii.godesii_services.controller.order;

import com.godesii.godesii_services.common.APIResponse;
import com.godesii.godesii_services.constant.GoDesiiConstant;
import com.godesii.godesii_services.dto.OrderRequest;
import com.godesii.godesii_services.entity.order.Order;
import com.godesii.godesii_services.service.order.OrderService;
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
@RequestMapping(OrderController.ENDPOINT)
@Tag(name = "Order API", description = "Manage orders with full CRUD operations")
public class OrderController {

    public static final String ENDPOINT = GoDesiiConstant.API_VERSION + "/orders";

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    /**
     * Create a new order
     * 
     * @param request Validated order creation request
     * @return Created order with 201 status
     */
    @PostMapping(value = "/create")
    @Operation(summary = "Create a new order", description = "Creates a new order with validated data")
    public ResponseEntity<APIResponse<Order>> create(@Valid @RequestBody OrderRequest request) {
        Order created = service.create(request);

        APIResponse<Order> apiResponse = new APIResponse<>(
                HttpStatus.CREATED,
                created,
                GoDesiiConstant.SUCCESSFULLY_CREATED);

        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
    }

    /**
     * Get all orders with pagination
     * 
     * @param page      Page number (default: 0)
     * @param size      Page size (default: 10)
     * @param sortBy    Field to sort by (default: orderDate)
     * @param direction Sort direction (default: desc)
     * @return Paginated list of orders
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all orders", description = "Retrieves paginated list of orders")
    public ResponseEntity<APIResponse<Page<Order>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "orderDate") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<Order> orders = service.getAll(pageable);

        APIResponse<Page<Order>> apiResponse = new APIResponse<>(
                HttpStatus.OK,
                orders,
                GoDesiiConstant.SUCCESSFULLY_FETCHED);

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Get order by ID
     * 
     * @param id Order ID
     * @return Order entity or 404 if not found
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get order by ID", description = "Retrieves a single order by its ID")
    public ResponseEntity<APIResponse<Order>> getById(@PathVariable @NonNull String id) {
        Order order = service.getById(id);

        APIResponse<Order> apiResponse = new APIResponse<>(
                HttpStatus.OK,
                order,
                GoDesiiConstant.SUCCESSFULLY_FETCHED);

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Update order details
     * 
     * @param id      Order ID
     * @param request Validated order update request
     * @return Updated order or 404 if not found
     */
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update order", description = "Updates an existing order's details")
    public ResponseEntity<APIResponse<Order>> update(
            @PathVariable @NonNull String id,
            @Valid @RequestBody OrderRequest request) {

        Order updated = service.update(id, request);

        APIResponse<Order> apiResponse = new APIResponse<>(
                HttpStatus.OK,
                updated,
                GoDesiiConstant.SUCCESSFULLY_UPDATED);

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Update order status
     * 
     * @param id     Order ID
     * @param status New order status
     * @return Updated order or 404 if not found
     */
    @PatchMapping(value = "/{id}/status", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update order status", description = "Updates only the order status")
    public ResponseEntity<APIResponse<Order>> updateStatus(
            @PathVariable @NonNull String id,
            @RequestParam @NonNull String status) {

        Order updated = service.updateStatus(id, status);

        APIResponse<Order> apiResponse = new APIResponse<>(
                HttpStatus.OK,
                updated,
                GoDesiiConstant.SUCCESSFULLY_UPDATED);

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Delete order by ID
     * 
     * @param id Order ID
     * @return 204 No Content on success
     */
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Delete order", description = "Deletes an order by its ID")
    public ResponseEntity<APIResponse<Void>> delete(@PathVariable @NonNull String id) {
        service.delete(id);

        APIResponse<Void> apiResponse = new APIResponse<>(
                HttpStatus.NO_CONTENT,
                null,
                GoDesiiConstant.SUCCESSFULLY_DELETED);

        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
    }
}
