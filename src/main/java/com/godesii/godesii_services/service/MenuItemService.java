package com.godesii.godesii_services.service;

import com.godesii.godesii_services.entity.restaurant.MenuItem;
import com.godesii.godesii_services.repository.restaurant.MenuItemRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MenuItemService {

    private final MenuItemRepo repo;

    public MenuItemService(MenuItemRepo repo) {
        this.repo = repo;
    }

    public MenuItem create(MenuItem item) {
        return repo.save(item);
    }

    public List<MenuItem> getAll() {
        return repo.findAll();
    }

    public MenuItem getById(Long id) {
        return repo.findById(id).orElseThrow();
    }

    public MenuItem update(Long id, MenuItem item) {
        MenuItem existing = getById(id);

        Optional.ofNullable(item.getMenuName()).ifPresent(existing::setMenuName);
        Optional.ofNullable(item.getCuisine()).ifPresent(existing::setCuisine);
        Optional.ofNullable(item.getPrice()).ifPresent(existing::setPrice);
        Optional.ofNullable(item.getImageUrl()).ifPresent(existing::setImageUrl);
        Optional.ofNullable(item.getDescription()).ifPresent(existing::setDescription);
        Optional.ofNullable(item.getIngredients()).ifPresent(existing::setIngredients);
        Optional.ofNullable(item.getMenuType()).ifPresent(existing::setMenuType);

        // boolean must be handled separately (since primitive can't be null)
        existing.setAvailable(item.isAvailable());

        return repo.save(existing);
    }


    public void delete(Long id) {
        repo.deleteById(id);
    }
}
