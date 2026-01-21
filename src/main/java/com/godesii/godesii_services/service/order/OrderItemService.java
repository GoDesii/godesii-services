package com.godesii.godesii_services.service.order;

import com.godesii.godesii_services.dto.OrderItemRequest;
import com.godesii.godesii_services.entity.order.OrderItem;
import com.godesii.godesii_services.exception.ResourceNotFoundException;
import com.godesii.godesii_services.repository.order.OrderItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderItemService {

    private final OrderItemRepository repo;
    public static final Logger log = LoggerFactory.getLogger(OrderItemService.class);

    public OrderItemService(OrderItemRepository repo) {
        this.repo = repo;
    }

    /**
     * Create a new order item
     * 
     * @param request OrderItemRequest DTO with validated data
     * @return Created order item entity
     */
    @Transactional
    public OrderItem create(OrderItemRequest request) {
        OrderItem orderItem = OrderItemRequest.mapToEntity(request);
        OrderItem saved = repo.save(orderItem);
        return saved;
    }

    /**
     * Get all order items with pagination
     * 
     * @param pageable Pagination information
     * @return Page of order items
     */
    public Page<OrderItem> getAll(Pageable pageable) {
        Page<OrderItem> orderItems = repo.findAll(pageable);
        return orderItems;
    }

    /**
     * Get order item by ID
     * 
     * @param id Order item ID
     * @return OrderItem entity
     * @throws ResourceNotFoundException if order item not found
     */
    public OrderItem getById(@NonNull String id) {
        return repo.findById(id)
                .orElseThrow(() -> {
                    return new ResourceNotFoundException("Order item not found with ID: " + id);
                });
    }

    /**
     * Update existing order item
     * 
     * @param id      Order item ID
     * @param request OrderItemRequest with update data
     * @return Updated order item entity
     * @throws ResourceNotFoundException if order item not found
     */
    @Transactional
    public OrderItem update(@NonNull String id, OrderItemRequest request) {
        OrderItem existing = getById(id);

        // Update only non-null fields
        OrderItemRequest.updateEntity(existing, request);

        OrderItem updated = repo.save(existing);
        return updated;
    }

    /**
     * Delete order item by ID
     * 
     * @param id Order item ID
     * @throws ResourceNotFoundException if order item not found
     */
    @Transactional
    public void delete(@NonNull String id) {
        OrderItem existing = getById(id);
        repo.delete(existing);
    }
}
