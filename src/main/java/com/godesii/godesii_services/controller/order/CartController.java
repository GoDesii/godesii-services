package com.godesii.godesii_services.controller.order;

import com.godesii.godesii_services.common.APIResponse;
import com.godesii.godesii_services.constant.GoDesiiConstant;
import com.godesii.godesii_services.dto.AddToCartRequest;
import com.godesii.godesii_services.dto.CartRequest;
import com.godesii.godesii_services.dto.CartResponse;
import com.godesii.godesii_services.dto.UpdateCartItemRequest;
import com.godesii.godesii_services.entity.order.Cart;
import com.godesii.godesii_services.service.order.CartService;
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
@RequestMapping(CartController.ENDPOINT)
@Tag(name = "Cart API", description = "Manage shopping carts with full CRUD operations")
public class CartController {

        public static final String ENDPOINT = GoDesiiConstant.API_VERSION + "/carts";

        private final CartService service;

        public CartController(CartService service) {
                this.service = service;
        }

        /**
         * Create a new cart
         * 
         * @param request Validated cart creation request
         * @return Created cart with 201 status
         */
        @PostMapping(value = "/create")
        @Operation(summary = "Create a new cart", description = "Creates a new shopping cart with validated data")
        public ResponseEntity<APIResponse<Cart>> create(@Valid @RequestBody CartRequest request) {
                Cart created = service.create(request);

                APIResponse<Cart> apiResponse = new APIResponse<>(
                                HttpStatus.CREATED,
                                created,
                                GoDesiiConstant.SUCCESSFULLY_CREATED);

                return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
        }

        /**
         * Get all carts with pagination
         * 
         * @param page      Page number (default: 0)
         * @param size      Page size (default: 10)
         * @param sortBy    Field to sort by (default: createAt)
         * @param direction Sort direction (default: desc)
         * @return Paginated list of carts
         */
        @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
        @Operation(summary = "Get all carts", description = "Retrieves paginated list of carts")
        public ResponseEntity<APIResponse<Page<Cart>>> getAll(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        @RequestParam(defaultValue = "createAt") String sortBy,
                        @RequestParam(defaultValue = "desc") String direction) {

                Sort.Direction sortDirection = direction.equalsIgnoreCase("desc")
                                ? Sort.Direction.DESC
                                : Sort.Direction.ASC;

                Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
                Page<Cart> carts = service.getAll(pageable);

                APIResponse<Page<Cart>> apiResponse = new APIResponse<>(
                                HttpStatus.OK,
                                carts,
                                GoDesiiConstant.SUCCESSFULLY_FETCHED);

                return ResponseEntity.ok(apiResponse);
        }

        /**
         * Get cart by ID
         * 
         * @param id Cart ID
         * @return Cart entity or 404 if not found
         */
        @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
        @Operation(summary = "Get cart by ID", description = "Retrieves a single cart by its ID")
        public ResponseEntity<APIResponse<Cart>> getById(@PathVariable @NonNull String id) {
                Cart cart = service.getById(id);

                APIResponse<Cart> apiResponse = new APIResponse<>(
                                HttpStatus.OK,
                                cart,
                                GoDesiiConstant.SUCCESSFULLY_FETCHED);

                return ResponseEntity.ok(apiResponse);
        }

        /**
         * Get cart by username
         * 
         * @param username Username
         * @return Cart entity or 404 if not found
         */
        @GetMapping(value = "/user/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
        @Operation(summary = "Get cart by username", description = "Retrieves a user's shopping cart")
        public ResponseEntity<APIResponse<Cart>> getByUsername(@PathVariable @NonNull String username) {
                Cart cart = service.getByUsername(username);

                APIResponse<Cart> apiResponse = new APIResponse<>(
                                HttpStatus.OK,
                                cart,
                                GoDesiiConstant.SUCCESSFULLY_FETCHED);

                return ResponseEntity.ok(apiResponse);
        }

        /**
         * Update cart details
         * 
         * @param id      Cart ID
         * @param request Validated cart update request
         * @return Updated cart or 404 if not found
         */
        @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
        @Operation(summary = "Update cart", description = "Updates an existing cart's details")
        public ResponseEntity<APIResponse<Cart>> update(
                        @PathVariable @NonNull String id,
                        @Valid @RequestBody CartRequest request) {

                Cart updated = service.update(id, request);

                APIResponse<Cart> apiResponse = new APIResponse<>(
                                HttpStatus.OK,
                                updated,
                                GoDesiiConstant.SUCCESSFULLY_UPDATED);

                return ResponseEntity.ok(apiResponse);
        }

        /**
         * Add item to cart
         * 
         * @param request Validated add to cart request
         * @return Cart response with updated cart
         */
        @PostMapping(value = "/add-item", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
        @Operation(summary = "Add item to cart", description = "Add a menu item to user's cart with validations")
        public ResponseEntity<APIResponse<CartResponse>> addItemToCart(@Valid @RequestBody AddToCartRequest request) {
                CartResponse cartResponse = service.addItemToCart(request);

                APIResponse<CartResponse> apiResponse = new APIResponse<>(
                                HttpStatus.OK,
                                cartResponse,
                                "Item added to cart successfully");

                return ResponseEntity.ok(apiResponse);
        }

        /**
         * Update cart item quantity
         * 
         * @param cartId     Cart ID
         * @param cartItemId Cart item ID
         * @param request    Update request with new quantity
         * @return Updated cart response or 204 if cart deleted
         */
        @PutMapping(value = "/{cartId}/items/{cartItemId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
        @Operation(summary = "Update cart item", description = "Update quantity of cart item (0 removes item)")
        public ResponseEntity<APIResponse<CartResponse>> updateCartItem(
                        @PathVariable @NonNull String cartId,
                        @PathVariable @NonNull String cartItemId,
                        @Valid @RequestBody UpdateCartItemRequest request) {

                CartResponse cartResponse = service.updateCartItem(cartId, cartItemId, request);

                if (cartResponse == null) {
                        // Cart was deleted because it became empty
                        APIResponse<CartResponse> apiResponse = new APIResponse<>(
                                        HttpStatus.NO_CONTENT,
                                        null,
                                        "Cart is now empty and has been deleted");
                        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(apiResponse);
                }

                APIResponse<CartResponse> apiResponse = new APIResponse<>(
                                HttpStatus.OK,
                                cartResponse,
                                "Cart item updated successfully");

                return ResponseEntity.ok(apiResponse);
        }

        /**
         * Remove item from cart
         * 
         * @param cartId     Cart ID
         * @param cartItemId Cart item ID to remove
         * @return Updated cart response or 204 if cart deleted
         */
        @DeleteMapping(value = "/{cartId}/items/{cartItemId}", produces = MediaType.APPLICATION_JSON_VALUE)
        @Operation(summary = "Remove cart item", description = "Remove a specific item from cart")
        public ResponseEntity<APIResponse<CartResponse>> removeCartItem(
                        @PathVariable @NonNull String cartId,
                        @PathVariable @NonNull String cartItemId) {

                CartResponse cartResponse = service.removeCartItem(cartId, cartItemId);

                if (cartResponse == null) {
                        // Cart was deleted because it became empty
                        APIResponse<CartResponse> apiResponse = new APIResponse<>(
                                        HttpStatus.NO_CONTENT,
                                        null,
                                        "Cart is now empty and has been deleted");
                        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(apiResponse);
                }

                APIResponse<CartResponse> apiResponse = new APIResponse<>(
                                HttpStatus.OK,
                                cartResponse,
                                "Cart item removed successfully");

                return ResponseEntity.ok(apiResponse);
        }

        /**
         * Get active cart for user
         * 
         * @param username Username
         * @return Active cart with full details
         */
        @GetMapping(value = "/active/user/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
        @Operation(summary = "Get active cart", description = "Get user's active (non-expired) cart with full details")
        public ResponseEntity<APIResponse<CartResponse>> getActiveCart(@PathVariable @NonNull String username) {
                CartResponse cartResponse = service.getActiveCart(username);

                APIResponse<CartResponse> apiResponse = new APIResponse<>(
                                HttpStatus.OK,
                                cartResponse,
                                "Active cart retrieved successfully");

                return ResponseEntity.ok(apiResponse);
        }

        /**
         * Clear all items from cart
         * 
         * @param cartId Cart ID to clear
         * @return 204 No Content
         */
        @DeleteMapping(value = "/{cartId}/clear", produces = MediaType.APPLICATION_JSON_VALUE)
        @Operation(summary = "Clear cart", description = "Remove all items and delete the cart")
        public ResponseEntity<APIResponse<Void>> clearCart(@PathVariable @NonNull String cartId) {
                service.clearCart(cartId);

                APIResponse<Void> apiResponse = new APIResponse<>(
                                HttpStatus.NO_CONTENT,
                                null,
                                "Cart cleared successfully");

                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(apiResponse);
        }

        /**
         * Delete cart by ID
         * 
         * @param id Cart ID
         * @return 204 No Content on success
         */
        @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
        @Operation(summary = "Delete cart", description = "Deletes a cart by its ID")
        public ResponseEntity<APIResponse<Void>> delete(@PathVariable @NonNull String id) {
                service.delete(id);

                APIResponse<Void> apiResponse = new APIResponse<>(
                                HttpStatus.NO_CONTENT,
                                null,
                                GoDesiiConstant.SUCCESSFULLY_DELETED);

                return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
        }
}
