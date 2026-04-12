package com.godesii.godesii_services.controller.order;

import com.godesii.godesii_services.common.APIResponse;
import com.godesii.godesii_services.common.DatabaseHelper;
import com.godesii.godesii_services.constant.GoDesiiConstant;
import com.godesii.godesii_services.dto.AddToCartRequest;
import com.godesii.godesii_services.dto.CartRequest;
import com.godesii.godesii_services.dto.CartResponse;
import com.godesii.godesii_services.dto.UpdateCartItemRequest;
import com.godesii.godesii_services.entity.order.Cart;
import com.godesii.godesii_services.service.order.CartService;
import io.jsonwebtoken.lang.Strings;
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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        public ResponseEntity<APIResponse<List<Cart>>> getAll(
                @RequestParam(name = "username") String username,
                @RequestParam(name = "currentPage", defaultValue = "0", required = false) int currentPage,
                @RequestParam(name = "itemsPerPage",  defaultValue = "0", required = false) int itemsPerPage,
                @RequestParam(name = "sortOrder", defaultValue = "asc", required = false) String sortOrder,
                @RequestParam(name = "sortBy", defaultValue = "", required = false) String sortBy) {

            DatabaseHelper databaseHelper = new DatabaseHelper(Strings.EMPTY, currentPage, itemsPerPage, sortBy, sortOrder);
                Page<Cart> carts = service.getAll(username, databaseHelper);

            APIResponse<List<Cart>> apiResponse = new APIResponse<>(
                    HttpStatus.OK,
                    carts.getContent(),
                    GoDesiiConstant.SUCCESSFULLY_FETCHED,
                    databaseHelper.getCurrentPage(),
                    (int)carts.getTotalElements());


            return ResponseEntity.ok(apiResponse);
        }

        @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
        @Operation(summary = "Get cart by ID", description = "Retrieves a single cart by its ID")
        public ResponseEntity<APIResponse<Cart>> getById(@PathVariable @NonNull Long id) {
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

        @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
        @Operation(summary = "Update cart", description = "Updates an existing cart's details")
        public ResponseEntity<APIResponse<Cart>> update(
                        @PathVariable @NonNull Long id,
                        @Valid @RequestBody CartRequest request) {

                Cart updated = service.update(id, request);

                APIResponse<Cart> apiResponse = new APIResponse<>(
                                HttpStatus.OK,
                                updated,
                                GoDesiiConstant.SUCCESSFULLY_UPDATED);

                return ResponseEntity.ok(apiResponse);
        }

        /**
         * Add items to cart
         * 
         * @param request Validated add to cart request with list of items
         * @return Cart response with updated cart
         */
        @PostMapping(value = "/add-item", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
        @Operation(summary = "Add items to cart", description = "Add one or more menu items to user's cart with validations")
        public ResponseEntity<APIResponse<CartResponse>> addItemToCart(@Valid @RequestBody AddToCartRequest request) {
                CartResponse cartResponse = service.addItemToCart(request);

                APIResponse<CartResponse> apiResponse = new APIResponse<>(
                                HttpStatus.OK,
                                cartResponse,
                                "Item(s) added to cart successfully");

                return ResponseEntity.ok(apiResponse);
        }

        @PutMapping(value = "/{cartId}/items/{cartItemId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
        @Operation(summary = "Update cart item", description = "Update quantity of cart item (0 removes item)")
        public ResponseEntity<APIResponse<CartResponse>> updateCartItem(
                        @PathVariable @NonNull Long cartId,
                        @PathVariable @NonNull Long cartItemId,
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

        @DeleteMapping(value = "/{cartId}/items/{cartItemId}", produces = MediaType.APPLICATION_JSON_VALUE)
        @Operation(summary = "Remove cart item", description = "Remove a specific item from cart")
        public ResponseEntity<APIResponse<CartResponse>> removeCartItem(
                        @PathVariable @NonNull Long cartId,
                        @PathVariable @NonNull Long cartItemId) {

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

        @DeleteMapping(value = "/{cartId}/clear", produces = MediaType.APPLICATION_JSON_VALUE)
        @Operation(summary = "Clear cart", description = "Remove all items and delete the cart")
        public ResponseEntity<APIResponse<Void>> clearCart(@PathVariable @NonNull Long cartId) {
                service.clearCart(cartId);

                APIResponse<Void> apiResponse = new APIResponse<>(
                                HttpStatus.NO_CONTENT,
                                null,
                                "Cart cleared successfully");

                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(apiResponse);
        }

        @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
        @Operation(summary = "Delete cart", description = "Deletes a cart by its ID")
        public ResponseEntity<APIResponse<Void>> delete(@PathVariable @NonNull Long id) {
                service.delete(id);

                APIResponse<Void> apiResponse = new APIResponse<>(
                                HttpStatus.NO_CONTENT,
                                null,
                                GoDesiiConstant.SUCCESSFULLY_DELETED);

                return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
        }
}
