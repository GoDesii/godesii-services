package com.godesii.godesii_services.service.order;

import com.godesii.godesii_services.dto.CartRequest;
import com.godesii.godesii_services.entity.order.Cart;
import com.godesii.godesii_services.exception.ResourceNotFoundException;
import com.godesii.godesii_services.repository.order.CartRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {

    private final CartRepository repo;
    public static final Logger log = LoggerFactory.getLogger(CartService.class);

    public CartService(CartRepository repo) {
        this.repo = repo;
    }

    /**
     * Create a new cart
     * 
     * @param request CartRequest DTO with validated data
     * @return Created cart entity
     */
    @Transactional
    public Cart create(CartRequest request) {
        Cart cart = CartRequest.mapToEntity(request);
        Cart saved = repo.save(cart);
        return saved;
    }

    /**
     * Get all carts with pagination
     * 
     * @param pageable Pagination information
     * @return Page of carts
     */
    public Page<Cart> getAll(Pageable pageable) {
        Page<Cart> carts = repo.findAll(pageable);
        return carts;
    }

    /**
     * Get cart by ID
     * 
     * @param id Cart ID
     * @return Cart entity
     * @throws ResourceNotFoundException if cart not found
     */
    public Cart getById(@NonNull String id) {
        return repo.findById(id)
                .orElseThrow(() -> {
                    return new ResourceNotFoundException("Cart not found with ID: " + id);
                });
    }

    /**
     * Get cart by user ID
     * 
     * @param userId User ID
     * @return Cart entity
     * @throws ResourceNotFoundException if cart not found
     */
    public Cart getByUserId(@NonNull Long userId) {
        return repo.findByUserId(userId)
                .orElseThrow(() -> {
                    return new ResourceNotFoundException("Cart not found for user ID: " + userId);
                });
    }

    /**
     * Update existing cart
     * 
     * @param id      Cart ID
     * @param request CartRequest with update data
     * @return Updated cart entity
     * @throws ResourceNotFoundException if cart not found
     */
    @Transactional
    public Cart update(@NonNull String id, CartRequest request) {
        Cart existing = getById(id);

        // Update only non-null fields
        CartRequest.updateEntity(existing, request);

        Cart updated = repo.save(existing);
        return updated;
    }

    /**
     * Delete cart by ID
     * 
     * @param id Cart ID
     * @throws ResourceNotFoundException if cart not found
     */
    @Transactional
    public void delete(@NonNull String id) {
        Cart existing = getById(id);
        repo.delete(existing);
    }

    /**
     * Calculate total price for a cart
     */
    private void calculateTotal(Cart cart) {
        long total = cart.getCartItems().stream()
                .mapToLong(i -> i.getPrice() * i.getQuantity())
                .sum();
        cart.setTotalPrice(total);
    }
}
