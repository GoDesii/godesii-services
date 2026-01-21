package com.godesii.godesii_services.service.order;

import com.godesii.godesii_services.dto.OrderRequest;
import com.godesii.godesii_services.entity.order.Order;
import com.godesii.godesii_services.exception.ResourceNotFoundException;
import com.godesii.godesii_services.repository.order.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final OrderRepository repo;
    public static final Logger log = LoggerFactory.getLogger(OrderService.class);

    public OrderService(OrderRepository repo) {
        this.repo = repo;
    }

    /**
     * Create a new order
     * 
     * @param request OrderRequest DTO with validated data
     * @return Created order entity
     */
    @Transactional
    public Order create(OrderRequest request) {
        Order order = OrderRequest.mapToEntity(request);
        Order saved = repo.save(order);
        return saved;
    }

    /**
     * Get all orders with pagination
     * 
     * @param pageable Pagination information
     * @return Page of orders
     */
    public Page<Order> getAll(Pageable pageable) {
        Page<Order> orders = repo.findAll(pageable);
        return orders;
    }

    /**
     * Get order by ID
     * 
     * @param id Order ID
     * @return Order entity
     * @throws ResourceNotFoundException if order not found
     */
    public Order getById(@NonNull String id) {
        return repo.findById(id)
                .orElseThrow(() -> {
                    return new ResourceNotFoundException("Order not found with ID: " + id);
                });
    }

    /**
     * Update existing order
     * 
     * @param id      Order ID
     * @param request OrderRequest with update data
     * @return Updated order entity
     * @throws ResourceNotFoundException if order not found
     */
    @Transactional
    public Order update(@NonNull String id, OrderRequest request) {
        Order existing = getById(id);

        // Update only non-null fields
        OrderRequest.updateEntity(existing, request);

        Order updated = repo.save(existing);
        return updated;
    }

    /**
     * Update order status
     * 
     * @param id     Order ID
     * @param status New order status
     * @return Updated order entity
     * @throws ResourceNotFoundException if order not found
     */
    @Transactional
    public Order updateStatus(@NonNull String id, @NonNull String status) {
        Order existing = getById(id);
        existing.setOrderStatus(status);
        Order updated = repo.save(existing);
        return updated;
    }

    /**
     * Delete order by ID
     * 
     * @param id Order ID
     * @throws ResourceNotFoundException if order not found
     */
    @Transactional
    public void delete(@NonNull String id) {
        Order existing = getById(id);
        repo.delete(existing);
    }
}
