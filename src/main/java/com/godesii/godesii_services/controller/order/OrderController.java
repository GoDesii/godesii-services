package com.godesii.godesii_services.controller.order;

import com.godesii.godesii_services.common.APIResponse;
import com.godesii.godesii_services.constant.GoDesiiConstant;
import com.godesii.godesii_services.dto.*;
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
         * Place a new order
         */
        @PostMapping(value = "/place", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
        @Operation(summary = "Place a new order", description = "Place order from active cart")
        public ResponseEntity<APIResponse<PlaceOrderResponse>> placeOrder(
                        @Valid @RequestBody PlaceOrderRequest request) {
                PlaceOrderResponse response = service.placeOrder(request);

                APIResponse<PlaceOrderResponse> apiResponse = new APIResponse<>(
                                HttpStatus.CREATED,
                                response,
                                "Order placed successfully");

                return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
        }

        /**
         * Handle payment success callback
         */
        @PostMapping(value = "/{id}/payment/success", consumes = MediaType.APPLICATION_JSON_VALUE)
        @Operation(summary = "Handle payment success", description = "Callback for successful payment")
        public ResponseEntity<APIResponse<Order>> paymentSuccess(
                        @PathVariable @NonNull String id,
                        @RequestBody PaymentCallbackRequest callback) {

                callback.setOrderId(id); // Ensure ID matches path
                Order updated = service.handlePaymentSuccess(callback);

                APIResponse<Order> apiResponse = new APIResponse<>(
                                HttpStatus.OK,
                                updated,
                                "Payment processed successfully");

                return ResponseEntity.ok(apiResponse);
        }

        /**
         * Handle payment failure callback
         */
        @PostMapping(value = "/{id}/payment/failure", consumes = MediaType.APPLICATION_JSON_VALUE)
        @Operation(summary = "Handle payment failure", description = "Callback for failed payment")
        public ResponseEntity<APIResponse<Order>> paymentFailure(
                        @PathVariable @NonNull String id,
                        @RequestBody PaymentCallbackRequest callback) {

                callback.setOrderId(id);
                Order updated = service.handlePaymentFailure(callback);

                APIResponse<Order> apiResponse = new APIResponse<>(
                                HttpStatus.OK,
                                updated,
                                "Payment failure recorded");

                return ResponseEntity.ok(apiResponse);
        }

        /**
         * Confirm/Reject order by restaurant
         */
        @PostMapping(value = "/{id}/confirm", consumes = MediaType.APPLICATION_JSON_VALUE)
        @Operation(summary = "Confirm/Reject order", description = "Restaurant confirmation action")
        public ResponseEntity<APIResponse<Order>> confirmOrder(
                        @PathVariable @NonNull String id,
                        @Valid @RequestBody OrderConfirmationRequest request) {

                Order updated = service.confirmOrderByRestaurant(id, request);

                String message = "ACCEPT".equalsIgnoreCase(request.getAction()) ? "Order accepted successfully"
                                : "Order rejected successfully";

                APIResponse<Order> apiResponse = new APIResponse<>(
                                HttpStatus.OK,
                                updated,
                                message);

                return ResponseEntity.ok(apiResponse);
        }

        /**
         * Cancel order by user
         */
        @PostMapping(value = "/{id}/cancel")
        @Operation(summary = "Cancel order", description = "Cancel an existing order")
        public ResponseEntity<APIResponse<Order>> cancelOrder(
                        @PathVariable @NonNull String id,
                        @RequestParam(required = true) String reason) {

                Order updated = service.cancelOrder(id, reason);

                APIResponse<Order> apiResponse = new APIResponse<>(
                                HttpStatus.OK,
                                updated,
                                "Order cancelled successfully");

                return ResponseEntity.ok(apiResponse);
        }

        /**
         * Get all orders with pagination
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
         * Update order status (Legacy/Manual)
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

        /**
         * Retry payment for failed order
         */
        @PostMapping(value = "/{id}/retry-payment", consumes = MediaType.APPLICATION_JSON_VALUE)
        @Operation(summary = "Retry payment", description = "Retry payment for failed order")
        public ResponseEntity<APIResponse<PlaceOrderResponse>> retryPayment(
                        @PathVariable @NonNull String id,
                        @RequestParam @NonNull String paymentMethod) {

                PlaceOrderResponse response = service.retryPayment(id, paymentMethod);

                APIResponse<PlaceOrderResponse> apiResponse = new APIResponse<>(
                                HttpStatus.OK,
                                response,
                                "Payment retry initiated");

                return ResponseEntity.ok(apiResponse);
        }

        /**
         * Mark order as preparing
         */
        @PostMapping(value = "/{id}/mark-preparing")
        @Operation(summary = "Mark as preparing", description = "Restaurant marks order as being prepared")
        public ResponseEntity<APIResponse<Order>> markAsPreparing(@PathVariable @NonNull String id) {

                Order updated = service.markAsPreparing(id);

                APIResponse<Order> apiResponse = new APIResponse<>(
                                HttpStatus.OK,
                                updated,
                                "Order marked as preparing");

                return ResponseEntity.ok(apiResponse);
        }

        /**
         * Mark order as ready for pickup
         */
        @PostMapping(value = "/{id}/mark-ready")
        @Operation(summary = "Mark as ready", description = "Restaurant marks order as ready for pickup")
        public ResponseEntity<APIResponse<Order>> markAsReady(@PathVariable @NonNull String id) {

                Order updated = service.markAsReadyForPickup(id);

                APIResponse<Order> apiResponse = new APIResponse<>(
                                HttpStatus.OK,
                                updated,
                                "Order marked as ready for pickup");

                return ResponseEntity.ok(apiResponse);
        }

        /**
         * Mark order as out for delivery
         */
        @PostMapping(value = "/{id}/mark-out-for-delivery")
        @Operation(summary = "Mark as out for delivery", description = "Mark order out for delivery")
        public ResponseEntity<APIResponse<Order>> markAsOutForDelivery(
                        @PathVariable @NonNull String id) {

                Order updated = service.markAsOutForDelivery(id);

                APIResponse<Order> apiResponse = new APIResponse<>(
                                HttpStatus.OK,
                                updated,
                                "Order marked as out for delivery");

                return ResponseEntity.ok(apiResponse);
        }

        /**
         * Mark order as delivered
         */
        @PostMapping(value = "/{id}/mark-delivered")
        @Operation(summary = "Mark as delivered", description = "Mark order as delivered")
        public ResponseEntity<APIResponse<Order>> markAsDelivered(@PathVariable @NonNull String id) {

                Order updated = service.markAsDelivered(id);

                APIResponse<Order> apiResponse = new APIResponse<>(
                                HttpStatus.OK,
                                updated,
                                "Order delivered successfully");

                return ResponseEntity.ok(apiResponse);
        }

        /**
         * User cancels order
         */
        @PostMapping(value = "/{id}/cancel/user")
        @Operation(summary = "User cancel order", description = "User cancels order before preparation")
        public ResponseEntity<APIResponse<Order>> cancelOrderByUser(
                        @PathVariable @NonNull String id,
                        @RequestParam String reason) {

                Order updated = service.cancelOrderByUser(id, reason);

                APIResponse<Order> apiResponse = new APIResponse<>(
                                HttpStatus.OK,
                                updated,
                                "Order cancelled successfully");

                return ResponseEntity.ok(apiResponse);
        }

        /**
         * Restaurant cancels order
         */
        @PostMapping(value = "/{id}/cancel/restaurant", consumes = MediaType.APPLICATION_JSON_VALUE)
        @Operation(summary = "Restaurant cancel order", description = "Restaurant cancels order due to stock/other issues")
        public ResponseEntity<APIResponse<Order>> cancelOrderByRestaurant(
                        @PathVariable @NonNull String id,
                        @RequestParam String reason,
                        @RequestParam String details) {

                // Convert string to enum
                com.godesii.godesii_services.entity.order.CancellationReason cancellationReason;
                try {
                        cancellationReason = com.godesii.godesii_services.entity.order.CancellationReason
                                        .valueOf(reason);
                } catch (IllegalArgumentException e) {
                        cancellationReason = com.godesii.godesii_services.entity.order.CancellationReason.OTHER;
                }

                Order updated = service.cancelOrderByRestaurant(id, cancellationReason, details);

                APIResponse<Order> apiResponse = new APIResponse<>(
                                HttpStatus.OK,
                                updated,
                                "Order cancelled by restaurant");

                return ResponseEntity.ok(apiResponse);
        }
}
