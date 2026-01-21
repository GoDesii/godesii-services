package com.godesii.godesii_services.service.order;

import com.godesii.godesii_services.dto.CartItemRequest;
import com.godesii.godesii_services.entity.order.CartItem;
import com.godesii.godesii_services.exception.ResourceNotFoundException;
import com.godesii.godesii_services.repository.order.CartItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartItemService {

    private final CartItemRepository repo;
    public static final Logger log = LoggerFactory.getLogger(CartItemService.class);

    public CartItemService(CartItemRepository repo) {
        this.repo = repo;
    }

    /**
     * Create a new cart item
     * 
     * @param request CartItemRequest DTO with validated data
     * @return Created cart item entity
     */
    @Transactional
    public CartItem create(CartItemRequest request) {
        CartItem cartItem = CartItemRequest.mapToEntity(request);
        CartItem saved = repo.save(cartItem);
        return saved;
    }

    /**
     * Get all cart items with pagination
     * 
     * @param pageable Pagination information
     * @return Page of cart items
     */
    public Page<CartItem> getAll(Pageable pageable) {
        Page<CartItem> cartItems = repo.findAll(pageable);
        return cartItems;
    }

    /**
     * Get cart item by ID
     * 
     * @param id Cart item ID
     * @return CartItem entity
     * @throws ResourceNotFoundException if cart item not found
     */
    public CartItem getById(@NonNull String id) {
        return repo.findById(id)
                .orElseThrow(() -> {
                    return new ResourceNotFoundException("Cart item not found with ID: " + id);
                });
    }

    /**
     * Update existing cart item
     * 
     * @param id      Cart item ID
     * @param request CartItemRequest with update data
     * @return Updated cart item entity
     * @throws ResourceNotFoundException if cart item not found
     */
    @Transactional
    public CartItem update(@NonNull String id, CartItemRequest request) {
        CartItem existing = getById(id);

        // Update only non-null fields
        CartItemRequest.updateEntity(existing, request);

        CartItem updated = repo.save(existing);
        return updated;
    }

    /**
     * Delete cart item by ID
     * 
     * @param id Cart item ID
     * @throws ResourceNotFoundException if cart item not found
     */
    @Transactional
    public void delete(@NonNull String id) {
        CartItem existing = getById(id);
        repo.delete(existing);
    }
}
