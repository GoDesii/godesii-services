package com.godesii.godesii_services.service.restaurant;

import com.godesii.godesii_services.dto.CategoryRequest;
import com.godesii.godesii_services.entity.restaurant.Category;
import com.godesii.godesii_services.entity.restaurant.Menu;
import com.godesii.godesii_services.exception.ResourceNotFoundException;
import com.godesii.godesii_services.repository.restaurant.CategoryRepository;
import com.godesii.godesii_services.repository.restaurant.MenuRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final MenuRepository menuRepository;

    public CategoryService(CategoryRepository categoryRepository, MenuRepository menuRepository) {
        this.categoryRepository = categoryRepository;
        this.menuRepository = menuRepository;
    }

    /**
     * Create a new category
     */
    public Category create(CategoryRequest request) {
        // Validate menu exists
        Menu menu = menuRepository.findById(request.getMenuId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Menu not found with ID: " + request.getMenuId()
                ));

        // Check for duplicate category name within the same menu
        Optional<Category> existing = categoryRepository.findByNameAndMenuId(
                request.getName(), request.getMenuId()
        );
        if (existing.isPresent()) {
            throw new IllegalArgumentException(
                    "Category with name '" + request.getName() + "' already exists in this menu"
            );
        }

        // Map and save
        Category category = CategoryRequest.mapToEntity(request, menu);
        Category saved = categoryRepository.save(category);
        log.info("Created category: {} for menu ID: {}", saved.getName(), menu.getId());
        return saved;
    }

    /**
     * Get all categories
     */
    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    /**
     * Get category by ID
     */
    public Category getById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with ID: " + id
                ));
    }

    /**
     * Get categories by menu ID
     */
    public List<Category> getByMenuId(Long menuId) {
        // Validate menu exists
        menuRepository.findById(menuId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Menu not found with ID: " + menuId
                ));
        
        return categoryRepository.findByMenuId(menuId);
    }

    /**
     * Update an existing category
     */
    public Category update(Long id, CategoryRequest request) {
        // Find existing category
        Category existing = getById(id);

        // Validate menu exists if menu ID is being changed
        Menu menu = null;
        if (request.getMenuId() != null && !request.getMenuId().equals(existing.getMenu().getId())) {
            menu = menuRepository.findById(request.getMenuId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Menu not found with ID: " + request.getMenuId()
                    ));

            // Check for duplicate name in the new menu
            Optional<Category> duplicate = categoryRepository.findByNameAndMenuId(
                    request.getName(), request.getMenuId()
            );
            if (duplicate.isPresent() && !duplicate.get().getId().equals(id)) {
                throw new IllegalArgumentException(
                        "Category with name '" + request.getName() + "' already exists in this menu"
                );
            }
        } else if (request.getName() != null && !request.getName().equals(existing.getName())) {
            // Check for duplicate name in the same menu
            Optional<Category> duplicate = categoryRepository.findByNameAndMenuId(
                    request.getName(), existing.getMenu().getId()
            );
            if (duplicate.isPresent() && !duplicate.get().getId().equals(id)) {
                throw new IllegalArgumentException(
                        "Category with name '" + request.getName() + "' already exists in this menu"
                );
            }
        }

        // Update entity
        CategoryRequest.updateEntity(existing, request, menu);
        Category updated = categoryRepository.save(existing);
        log.info("Updated category ID: {}", id);
        return updated;
    }

    /**
     * Delete a category
     */
    public void delete(Long id) {
        Category existing = getById(id);
        categoryRepository.delete(existing);
        log.info("Deleted category ID: {}", id);
    }
}
