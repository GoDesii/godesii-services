package com.godesii.godesii_services.service.order;

import com.godesii.godesii_services.config.CartConfig;
import com.godesii.godesii_services.dto.*;
import com.godesii.godesii_services.entity.order.Cart;
import com.godesii.godesii_services.entity.order.CartItem;
import com.godesii.godesii_services.entity.restaurant.DayOfWeek;
import com.godesii.godesii_services.entity.restaurant.MenuItem;
import com.godesii.godesii_services.entity.restaurant.Restaurant;
import com.godesii.godesii_services.exception.*;
import com.godesii.godesii_services.repository.order.CartRepository;
import com.godesii.godesii_services.repository.restaurant.MenuItemRepository;
import com.godesii.godesii_services.repository.restaurant.RestaurantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    private final CartRepository cartRepo;
    private final MenuItemRepository menuItemRepo;
    private final RestaurantRepository restaurantRepo;
    public static final Logger log = LoggerFactory.getLogger(CartService.class);

    public CartService(CartRepository cartRepo, MenuItemRepository menuItemRepo, RestaurantRepository restaurantRepo) {
        this.cartRepo = cartRepo;
        this.menuItemRepo = menuItemRepo;
        this.restaurantRepo = restaurantRepo;
    }

    /**
     * Add item to cart with comprehensive validations
     */
    @Transactional
    public CartResponse addItemToCart(AddToCartRequest request) {
        log.info("Adding item to cart for user: {}, restaurant: {}, item: {}",
                request.getUsername(), request.getRestaurantId(), request.getMenuItemId());

        // Get or create cart
        Instant now = Instant.now();
        Optional<Cart> existingCartOpt = cartRepo.findByUsernameAndExpiresAtAfter(request.getUsername(), now);

        Cart cart;
        if (existingCartOpt.isPresent()) {
            cart = existingCartOpt.get();

            // Validate same restaurant constraint
            if (!cart.getRestaurantId().equals(request.getRestaurantId())) {
                throw new CartValidationException(
                        "Cannot add items from different restaurant. Please clear your current cart first. " +
                                "Current restaurant ID: " + cart.getRestaurantId() +
                                ", Requested restaurant ID: " + request.getRestaurantId());
            }
        } else {
            // Create new cart
            cart = new Cart();
            cart.setUsername(request.getUsername());
            cart.setRestaurantId(request.getRestaurantId());
            cart.setCreateAt(now);
            cart.setCartItems(new ArrayList<>());
        }

        // Validate restaurant is active and open
        validateRestaurantOpen(request.getRestaurantId());

        // Validate menu item availability
        MenuItem menuItem = validateItemAvailability(request.getMenuItemId());

        // Convert BigDecimal price to Long (assuming price is in rupees, store as
        // paise)
        Long itemPriceInPaise = menuItem.getBasePrice().multiply(BigDecimal.valueOf(100))
                .longValue();

        // Check if item already exists in cart
        Optional<CartItem> existingItem = cart.getCartItems().stream()
                .filter(ci -> ci.getProductId().equals(request.getMenuItemId()))
                .findFirst();

        if (existingItem.isPresent()) {
            // Update quantity
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());

            // Check price hasn't changed
            if (!item.getPrice().equals(itemPriceInPaise)) {
                log.warn("Price changed for item: {}. Old: {}, New: {}",
                        request.getMenuItemId(), item.getPrice(), itemPriceInPaise);
                // Update to new price
                item.setPrice(itemPriceInPaise);
            }
        } else {
            // Add new item to cart
            CartItem newItem = new CartItem();
            newItem.setProductId(request.getMenuItemId());
            newItem.setQuantity(request.getQuantity());
            newItem.setPrice(itemPriceInPaise);
            newItem.setSpecialInstruction(request.getSpecialInstruction());
            cart.getCartItems().add(newItem);
        }

        // Update cart metadata
        cart.setUpdatedAt(now);
        cart.setExpiresAt(now.plusSeconds(CartConfig.CART_EXPIRY_MINUTES * 60));

        // Calculate and set total price
        PriceBreakdown priceBreakdown = calculatePriceBreakdown(cart);
        cart.setTotalPrice(priceBreakdown.getTotalAmount());

        // Save cart
        Cart savedCart = cartRepo.save(cart);

        // Get restaurant details
        Restaurant restaurant = restaurantRepo.findById(request.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));

        return buildCartResponse(savedCart, restaurant);
    }

    public boolean isExpired(Instant expiresAt) {
        return expiresAt != null && Instant.now().isAfter(expiresAt);
    }

    /**
     * Update cart item quantity
     */
    @Transactional
    public CartResponse updateCartItem(String cartId, String cartItemId, UpdateCartItemRequest request) {
        log.info("Updating cart item: {}, new quantity: {}", cartItemId, request.getQuantity());

        Cart cart = getById(cartId);

        // Check if cart is expired
        if (isExpired(cart.getExpiresAt())) {
            throw new CartValidationException("Cart has expired. Please create a new cart.");
        }

        // Find cart item
        Optional<CartItem> cartItemOpt = cart.getCartItems().stream()
                .filter(ci -> ci.getCartItemId().equals(cartItemId))
                .findFirst();

        if (cartItemOpt.isEmpty()) {
            throw new ResourceNotFoundException("Cart item not found with ID: " + cartItemId);
        }

        CartItem cartItem = cartItemOpt.get();

        if (request.getQuantity() == 0) {
            // Remove item
            cart.getCartItems().remove(cartItem);

            // If cart is empty, delete it
            if (cart.getCartItems().isEmpty()) {
                cartRepo.delete(cart);
                return null;
            }
        } else {
            // Update quantity
            cartItem.setQuantity(request.getQuantity());
        }

        // Update cart metadata
        cart.setUpdatedAt(Instant.now());

        // Recalculate pricing
        PriceBreakdown priceBreakdown = calculatePriceBreakdown(cart);
        cart.setTotalPrice(priceBreakdown.getTotalAmount());

        // Save cart
        Cart savedCart = cartRepo.save(cart);

        // Get restaurant details
        Restaurant restaurant = restaurantRepo.findById(cart.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));

        return buildCartResponse(savedCart, restaurant);
    }

    /**
     * Remove cart item
     */
    @Transactional
    public CartResponse removeCartItem(String cartId, String cartItemId) {
        log.info("Removing cart item: {} from cart: {}", cartItemId, cartId);

        Cart cart = getById(cartId);

        // Find and remove cart item
        boolean removed = cart.getCartItems().removeIf(ci -> ci.getCartItemId().equals(cartItemId));

        if (!removed) {
            throw new ResourceNotFoundException("Cart item not found with ID: " + cartItemId);
        }

        // If cart is empty, delete it
        if (cart.getCartItems().isEmpty()) {
            cartRepo.delete(cart);
            return null;
        }

        // Update cart metadata
        cart.setUpdatedAt(Instant.now());

        // Recalculate pricing
        PriceBreakdown priceBreakdown = calculatePriceBreakdown(cart);
        cart.setTotalPrice(priceBreakdown.getTotalAmount());

        // Save cart
        Cart savedCart = cartRepo.save(cart);

        // Get restaurant details
        Restaurant restaurant = restaurantRepo.findById(cart.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));

        return buildCartResponse(savedCart, restaurant);
    }

    /**
     * Get active cart for user
     */
    public CartResponse getActiveCart(String username) {
        log.info("Getting active cart for user: {}", username);

        Optional<Cart> cartOpt = cartRepo.findByUsername(username);

        if (cartOpt.isEmpty()) {
            throw new ResourceNotFoundException("No active cart found for username: " + username);
        }

        Cart cart = cartOpt.get();

        // Get restaurant details
        Restaurant restaurant = restaurantRepo.findById(cart.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));

        return buildCartResponse(cart, restaurant);
    }

    /**
     * Clear cart
     */
    @Transactional
    public void clearCart(String cartId) {
        log.info("Clearing cart: {}", cartId);
        Cart cart = getById(cartId);
        cartRepo.delete(cart);
    }

    /**
     * Clear cart by order ID (after successful order)
     */
    @Transactional
    public void clearCartByOrderId(String orderId) {
        log.info("Clearing cart for order: {}", orderId);
        Optional<Cart> cartOpt = cartRepo.findByLockedForOrderId(orderId);
        if (cartOpt.isPresent()) {
            cartRepo.delete(cartOpt.get());
            log.info("Cart cleared for order: {}", orderId);
        } else {
            log.warn("No cart found for order: {}", orderId);
        }
    }

    /**
     * Validate restaurant is active and open
     */
    private void validateRestaurantOpen(Long restaurantId) {
        // Check if restaurant is active
        Restaurant restaurant = restaurantRepo.findByIdAndIsActiveTrue(restaurantId)
                .orElseThrow(() -> new RestaurantClosedException(
                        "Restaurant is not available or inactive. Restaurant ID: " + restaurantId));

        // Check operational hours
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
        java.time.DayOfWeek currentDay = now.getDayOfWeek();
        LocalTime currentTime = now.toLocalTime();

        // Convert Java DayOfWeek to entity DayOfWeek
        DayOfWeek entityDayOfWeek = convertToDayOfWeek(currentDay);

        // Check if restaurant is open now
        boolean isOpen = restaurant.getOperatingHours().stream()
                .filter(oh -> oh.getDayOfWeek() == entityDayOfWeek)
                .filter(oh -> "DELIVERY".equalsIgnoreCase(oh.getServiceType()) ||
                        "BOTH".equalsIgnoreCase(oh.getServiceType()))
                .anyMatch(oh -> {
                    LocalTime openTime = oh.getOpenTime();
                    LocalTime closeTime = oh.getCloseTime();

                    // Handle overnight hours (e.g., 22:00 - 02:00)
                    if (closeTime.isBefore(openTime)) {
                        return currentTime.isAfter(openTime) || currentTime.isBefore(closeTime);
                    } else {
                        return !currentTime.isBefore(openTime) && !currentTime.isAfter(closeTime);
                    }
                });

        if (!isOpen) {
            throw new RestaurantClosedException(
                    "Restaurant is currently closed. Please check operational hours.");
        }
    }

    /**
     * Convert Java DayOfWeek to entity DayOfWeek
     */
    private DayOfWeek convertToDayOfWeek(java.time.DayOfWeek javaDayOfWeek) {
        return switch (javaDayOfWeek) {
            case SUNDAY -> DayOfWeek.SUNDAY;
            case MONDAY -> DayOfWeek.MONDAY;
            case TUESDAY -> DayOfWeek.TUESDAY;
            case WEDNESDAY -> DayOfWeek.WEDNESDAY;
            case THURSDAY -> DayOfWeek.THURSDAY;
            case FRIDAY -> DayOfWeek.FRIDAY;
            case SATURDAY -> DayOfWeek.SATURDAY;
        };
    }

    /**
     * Validate item availability
     */
    private MenuItem validateItemAvailability(String menuItemId) {
        Optional<MenuItem> menuItemOpt = menuItemRepo.findByItemIdAndIsAvailableTrue(menuItemId);

        if (menuItemOpt.isEmpty()) {
            throw new ItemNotAvailableException(
                    "Menu item is not available or out of stock. Item ID: " + menuItemId);
        }

        return menuItemOpt.get();
    }

    /**
     * Calculate complete price breakdown
     */
    private PriceBreakdown calculatePriceBreakdown(Cart cart) {
        PriceBreakdown breakdown = new PriceBreakdown();

        // Calculate item total (sum of all items)
        long itemTotal = cart.getCartItems().stream()
                .mapToLong(item -> item.getPrice() * item.getQuantity())
                .sum();
        breakdown.setItemTotal(itemTotal);

        // Calculate packaging charges (per item)
        long packagingCharges = cart.getCartItems().size() * CartConfig.PACKAGING_CHARGE_PER_ITEM;
        breakdown.setPackagingCharges(packagingCharges);

        // Calculate GST (5% of item total)
        long gst = Math.round(itemTotal * CartConfig.GST_RATE);
        breakdown.setGst(gst);

        // Platform fee
        breakdown.setPlatformFee(CartConfig.PLATFORM_FEE);

        // Delivery fee
        breakdown.setDeliveryFee(CartConfig.DELIVERY_FEE);

        // Discount (0 for now)
        breakdown.setDiscount(0L);

        // Calculate total
        long totalAmount = itemTotal + packagingCharges + gst +
                CartConfig.PLATFORM_FEE + CartConfig.DELIVERY_FEE - breakdown.getDiscount();
        breakdown.setTotalAmount(totalAmount);

        return breakdown;
    }

    /**
     * Build CartResponse from Cart entity
     */
    private CartResponse buildCartResponse(Cart cart, Restaurant restaurant) {
        CartResponse response = new CartResponse();
        response.setCartId(cart.getId());
        response.setUsername(cart.getUsername());
        response.setRestaurantId(cart.getRestaurantId());
        response.setRestaurantName(restaurant.getName());
        response.setCreatedAt(cart.getCreateAt());
        response.setUpdatedAt(cart.getUpdatedAt());
        response.setExpiresAt(cart.getExpiresAt());

        // Build cart items with availability check
        List<CartItemResponse> itemResponses = new ArrayList<>();
        for (CartItem cartItem : cart.getCartItems()) {
            CartItemResponse itemResponse = new CartItemResponse();
            itemResponse.setCartItemId(cartItem.getCartItemId());
            itemResponse.setMenuItemId(cartItem.getProductId());
            itemResponse.setQuantity(cartItem.getQuantity());
            itemResponse.setUnitPrice(cartItem.getPrice());
            itemResponse.setTotalPrice(cartItem.getPrice() * cartItem.getQuantity());
            itemResponse.setSpecialInstruction(cartItem.getSpecialInstruction());

            // Check current availability and price
            Optional<MenuItem> menuItemOpt = menuItemRepo.findById(cartItem.getProductId());
            if (menuItemOpt.isPresent()) {
                MenuItem menuItem = menuItemOpt.get();
                itemResponse.setMenuItemName(menuItem.getName());
                itemResponse.setImageUrl(menuItem.getImageUrl());
                itemResponse.setIsAvailable(menuItem.isAvailable());

                // Check price change
                Long currentPriceInPaise = menuItem.getBasePrice().multiply(BigDecimal.valueOf(100))
                        .longValue();
                if (!currentPriceInPaise.equals(cartItem.getPrice())) {
                    itemResponse.setPriceChanged(true);
                    itemResponse.setCurrentPrice(currentPriceInPaise);
                } else {
                    itemResponse.setPriceChanged(false);
                }
            } else {
                itemResponse.setMenuItemName("Unknown Item");
                itemResponse.setIsAvailable(false);
                itemResponse.setPriceChanged(false);
            }

            itemResponses.add(itemResponse);
        }
        response.setItems(itemResponses);

        // Calculate price breakdown
        PriceBreakdown priceBreakdown = calculatePriceBreakdown(cart);
        response.setPriceBreakdown(priceBreakdown);
        response.setTotalPrice(priceBreakdown.getTotalAmount());

        return response;
    }

    /**
     * Cleanup expired carts
     */
    @Transactional
    public int cleanupExpiredCarts() {
        List<Cart> expiredCarts = cartRepo.findByExpiresAtBefore(Instant.now());
        cartRepo.deleteAll(expiredCarts);
        log.info("Cleaned up {} expired carts", expiredCarts.size());
        return expiredCarts.size();
    }

    // ========== Cart Locking Methods ==========

    /**
     * Lock cart for checkout
     */
    @Transactional
    public void lockCart(String cartId, String orderId) {
        Cart cart = getById(cartId);

        if (Boolean.TRUE.equals(cart.getIsLocked())) {
            throw new CartLockedException("Cart is already locked for processing", cartId, cart.getLockedForOrderId());
        }

        cart.setIsLocked(true);
        cart.setLockedAt(Instant.now());
        cart.setLockedForOrderId(orderId);

        cartRepo.save(cart);
        log.info("Locked cart: {} for order: {}", cartId, orderId);
    }

    /**
     * Unlock cart
     */
    @Transactional
    public void unlockCart(String cartId) {
        Cart cart = getById(cartId);
        cart.setIsLocked(false);
        cart.setLockedAt(null);
        cart.setLockedForOrderId(null);
        cartRepo.save(cart);
        log.info("Unlocked cart: {}", cartId);
    }

    /**
     * Get final price breakdown for order placement
     * Recalculates prices and validates availability
     */
    @Transactional
    public PriceBreakdown getFinalPriceBreakdown(String cartId) {
        Cart cart = getById(cartId);

        // Validate expiry
        if (isExpired(cart.getExpiresAt())) {
            throw new CartValidationException("Cart has expired. Please create a new cart.");
        }

        // Validate items and recalculate prices
        for (CartItem item : cart.getCartItems()) {
            MenuItem menuItem = validateItemAvailability(item.getProductId());

            // Update price if changed
            Long currentPrice = menuItem.getBasePrice().multiply(BigDecimal.valueOf(100)).longValue();
            if (!currentPrice.equals(item.getPrice())) {
                log.info("Price update during checkout for item: {}, old: {}, new: {}",
                        item.getProductId(), item.getPrice(), currentPrice);
                item.setPrice(currentPrice);
            }
        }

        // Recalculate totals
        PriceBreakdown breakdown = calculatePriceBreakdown(cart);
        cart.setTotalPrice(breakdown.getTotalAmount());
        cart.setUpdatedAt(Instant.now());
        cartRepo.save(cart);

        return breakdown;
    }

    // ========== Original CRUD Methods ==========

    /**
     * Create a new cart
     */
    @Transactional
    public Cart create(CartRequest request) {
        Cart cart = CartRequest.mapToEntity(request);
        Cart saved = cartRepo.save(cart);
        return saved;
    }

    /**
     * Get all carts with pagination
     */
    public Page<Cart> getAll(Pageable pageable) {
        Page<Cart> carts = cartRepo.findAll(pageable);
        return carts;
    }

    /**
     * Get cart by ID
     */
    public Cart getById(@NonNull String id) {
        return cartRepo.findById(id)
                .orElseThrow(() -> {
                    return new ResourceNotFoundException("Cart not found with ID: " + id);
                });
    }

    /**
     * Get cart by username
     */
    public Cart getByUsername(@NonNull String username) {
        return cartRepo.findByUsername(username)
                .orElseThrow(() -> {
                    return new ResourceNotFoundException("Cart not found for username: " + username);
                });
    }

    /**
     * Update existing cart
     */
    @Transactional
    public Cart update(@NonNull String id, CartRequest request) {
        Cart existing = getById(id);

        // Update only non-null fields
        CartRequest.updateEntity(existing, request);

        Cart updated = cartRepo.save(existing);
        return updated;
    }

    /**
     * Delete cart by ID
     */
    @Transactional
    public void delete(@NonNull String id) {
        Cart existing = getById(id);
        cartRepo.delete(existing);
    }
}
