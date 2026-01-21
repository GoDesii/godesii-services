package com.godesii.godesii_services.service.restaurant;

import com.godesii.godesii_services.dto.NutritionalInfoRequest;
import com.godesii.godesii_services.entity.restaurant.MenuItem;
import com.godesii.godesii_services.entity.restaurant.NutritionalInfo;
import com.godesii.godesii_services.exception.ResourceNotFoundException;
import com.godesii.godesii_services.repository.restaurant.MenuItemRepo;
import com.godesii.godesii_services.repository.restaurant.NutritionalInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class NutritionalInfoService {

    private final NutritionalInfoRepository nutritionalInfoRepository;
    private final MenuItemRepo menuItemRepository;

    public NutritionalInfoService(NutritionalInfoRepository nutritionalInfoRepository, 
                                  MenuItemRepo menuItemRepository) {
        this.nutritionalInfoRepository = nutritionalInfoRepository;
        this.menuItemRepository = menuItemRepository;
    }

    /**
     * Create new nutritional info
     */
    public NutritionalInfo create(NutritionalInfoRequest request) {
        // Validate menu item exists
        MenuItem menuItem = menuItemRepository.findById(request.getMenuItemId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Menu item not found with ID: " + request.getMenuItemId()
                ));

        // Check if nutritional info already exists for this menu item
        Optional<NutritionalInfo> existing = nutritionalInfoRepository.findByMenuItem_ItemId(request.getMenuItemId());
        if (existing.isPresent()) {
            throw new IllegalArgumentException(
                    "Nutritional info already exists for menu item ID: " + request.getMenuItemId()
            );
        }

        // Map and save
        NutritionalInfo info = NutritionalInfoRequest.mapToEntity(request, menuItem);
        NutritionalInfo saved = nutritionalInfoRepository.save(info);
        log.info("Created nutritional info for menu item ID: {}", menuItem.getItemId());
        return saved;
    }

    /**
     * Get all nutritional info
     */
    public List<NutritionalInfo> getAll() {
        return nutritionalInfoRepository.findAll();
    }

    /**
     * Get nutritional info by ID
     */
    public NutritionalInfo getById(Long id) {
        return nutritionalInfoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Nutritional info not found with ID: " + id
                ));
    }

    /**
     * Get nutritional info by menu item ID
     */
    public NutritionalInfo getByMenuItemId(String menuItemId) {
        // Validate menu item exists
        menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Menu item not found with ID: " + menuItemId
                ));
        
        return nutritionalInfoRepository.findByMenuItem_ItemId(menuItemId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Nutritional info not found for menu item ID: " + menuItemId
                ));
    }

    /**
     * Update existing nutritional info
     */
    public NutritionalInfo update(Long id, NutritionalInfoRequest request) {
        // Find existing nutritional info
        NutritionalInfo existing = getById(id);

        // Validate menu item exists if menu item ID is being changed
        MenuItem menuItem = null;
        if (request.getMenuItemId() != null && 
            !request.getMenuItemId().equals(existing.getMenuItem().getItemId())) {
            menuItem = menuItemRepository.findById(request.getMenuItemId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Menu item not found with ID: " + request.getMenuItemId()
                    ));

            // Check if nutritional info already exists for the new menu item
            Optional<NutritionalInfo> duplicate = nutritionalInfoRepository.findByMenuItem_ItemId(request.getMenuItemId());
            if (duplicate.isPresent() && !duplicate.get().getInfoId().equals(id)) {
                throw new IllegalArgumentException(
                        "Nutritional info already exists for menu item ID: " + request.getMenuItemId()
                );
            }
        }

        // Update entity
        NutritionalInfoRequest.updateEntity(existing, request, menuItem);
        NutritionalInfo updated = nutritionalInfoRepository.save(existing);
        log.info("Updated nutritional info ID: {}", id);
        return updated;
    }

    /**
     * Delete nutritional info
     */
    public void delete(Long id) {
        NutritionalInfo existing = getById(id);
        nutritionalInfoRepository.delete(existing);
        log.info("Deleted nutritional info ID: {}", id);
    }
}
