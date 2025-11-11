package com.godesii.godesii_services.service.restaurant;

import com.godesii.godesii_services.entity.restaurant.MenuItem;
import com.godesii.godesii_services.repository.restaurant.MenuItemRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@Slf4j
public class MenuItemService {

    private final MenuItemRepo repo;

    public MenuItemService(MenuItemRepo repo) {
        this.repo = repo;
    }

    public MenuItem create(MenuItem item) {
        return repo.save(item);
    }

    public MenuItem getByMenuId(Long id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("MenuItem not found by this id"));
    }

    public Page<MenuItem> getAll(Pageable pageable) {
        return repo.findAll(pageable);
    }

    public Page<MenuItem> getByRestaurantId(Long restaurantId, Pageable pageable) {
        return repo.findByRestaurantId(restaurantId, pageable);
    }

    public MenuItem update(Long id, MenuItem item) {
        MenuItem existing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("MenuItem not found"));

        Optional.ofNullable(item.getMenuName()).ifPresent(existing::setMenuName);
        Optional.ofNullable(item.getCuisine()).ifPresent(existing::setCuisine);
        Optional.ofNullable(item.getPrice()).ifPresent(existing::setPrice);
        Optional.ofNullable(item.getImageUrl()).ifPresent(existing::setImageUrl);
        Optional.ofNullable(item.getDescription()).ifPresent(existing::setDescription);
        Optional.ofNullable(item.getIngredients()).ifPresent(existing::setIngredients);
        Optional.ofNullable(item.getMenuType()).ifPresent(existing::setMenuType);

        // boolean must be handled separately (since primitive can't be null)
        existing.setAvailable(item.isAvailable());
        log.info("updated MenuItem {}", existing);
        return repo.save(existing);
    }


    public void delete(Long id) {
        repo.deleteById(id);
    }
}
