package com.godesii.godesii_services.service.restaurant;

import com.godesii.godesii_services.dto.MenuItemRequest;
import com.godesii.godesii_services.entity.restaurant.Category;
import com.godesii.godesii_services.entity.restaurant.MenuItem;
import com.godesii.godesii_services.exception.ResourceNotFoundException;
import com.godesii.godesii_services.repository.restaurant.CategoryRepository;
import com.godesii.godesii_services.repository.restaurant.MenuItemRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class MenuItemService {

    private final MenuItemRepo menuItemRepository;
    private final CategoryRepository categoryRepository;

    public MenuItemService(MenuItemRepo menuItemRepository, CategoryRepository categoryRepository) {
        this.menuItemRepository = menuItemRepository;
        this.categoryRepository = categoryRepository;
    }

    /**
     * Create a new menu item
     */
    public MenuItem create(MenuItemRequest request) {
        // Validate category exists
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with ID: " + request.getCategoryId()
                ));

        // Map and save
        MenuItem menuItem = MenuItemRequest.mapToEntity(request, category);
        MenuItem saved = menuItemRepository.save(menuItem);
        log.info("Created menu item: {} for category ID: {}", saved.getName(), category.getId());
        return saved;
    }

    /**
     * Get all menu items
     */
    public List<MenuItem> getAll() {
        return menuItemRepository.findAll();
    }

    /**
     * Get menu item by ID
     */
    public MenuItem getById(String id) {
        return menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Menu item not found with ID: " + id
                ));
    }

    /**
     * Get menu items by category ID
     */
    public List<MenuItem> getByCategoryId(Long categoryId) {
        // Validate category exists
        categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with ID: " + categoryId
                ));
        
        return menuItemRepository.findByCategoryId(categoryId);
    }

    /**
     * Update an existing menu item
     */
    public MenuItem update(String id, MenuItemRequest request) {
        // Find existing menu item
        MenuItem existing = getById(id);

        // Validate category exists if category ID is being changed
        Category category = null;
        if (request.getCategoryId() != null && 
            !request.getCategoryId().equals(existing.getCategory().getId())) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Category not found with ID: " + request.getCategoryId()
                    ));
        }

        // Update entity
        MenuItemRequest.updateEntity(existing, request, category);
        MenuItem updated = menuItemRepository.save(existing);
        log.info("Updated menu item ID: {}", id);
        return updated;
    }

    /**
     * Delete a menu item
     */
    public void delete(String id) {
        MenuItem existing = getById(id);
        menuItemRepository.delete(existing);
        log.info("Deleted menu item ID: {}", id);
    }
}
