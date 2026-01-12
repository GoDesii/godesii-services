package com.godesii.godesii_services.service.order;

import com.godesii.godesii_services.dto.CartItemRequest;
import com.godesii.godesii_services.dto.CartRequest;
import com.godesii.godesii_services.entity.order.Cart;
import com.godesii.godesii_services.entity.order.CartItem;
import com.godesii.godesii_services.repository.order.CartRepository;
import com.godesii.godesii_services.repository.restaurant.RestaurantRepo;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final RestaurantRepo restaurantRepo;

    public CartService(CartRepository cartRepository, RestaurantRepo restaurantRepo) {
        this.cartRepository = cartRepository;
        this.restaurantRepo = restaurantRepo;
    }

    // CREATE / ADD ITEMS
    public Cart addItemsToCart(CartRequest request) {
        Cart cart = cartRepository.findByUserId(request.getUserId())
                .orElse(new Cart());

        cart.setUserId(request.getUserId());
        cart.setRestaurantId(request.getRestaurantId());
        cart.setUpdatedAt(Instant.now());

        for (CartItemRequest itemReq : request.getCartItemRequests()) {
            CartItem item = new CartItem();
            item.setProductId(itemReq.getProductId());
            item.setQuantity(itemReq.getQuantity());
            item.setPrice(itemReq.getPrice());
            item.setSpecialInstruction(itemReq.getSpecialInstruction());
            item.setCarts(cart);
            cart.getCartItems().add(item);
        }

        calculateTotal(cart);
        return cartRepository.save(cart);
    }

    // READ
    public Cart getCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
    }

    // DELETE SINGLE ITEM
    public void removeItem(String cartId, Long cartItemId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        cart.getCartItems()
                .removeIf(item -> item.getCartItemId().equals(cartItemId));

        calculateTotal(cart);
        cartRepository.save(cart);
    }

    // DELETE MULTIPLE ITEMS
    public void removeMultipleItems(String cartId, List<Long> itemIds) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        cart.getCartItems()
                .removeIf(item -> itemIds.contains(item.getCartItemId()));

        calculateTotal(cart);
        cartRepository.save(cart);
    }

    // CLEAR CART
    public void clearCart(String cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        cart.getCartItems().clear();
        cart.setTotalPrice(0L);
        cartRepository.save(cart);
    }

    private void calculateTotal(Cart cart) {
        long total = cart.getCartItems().stream()
                .mapToLong(i -> i.getPrice() * i.getQuantity())
                .sum();
        cart.setTotalPrice(total);
    }
}
