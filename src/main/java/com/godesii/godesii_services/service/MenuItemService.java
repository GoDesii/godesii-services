package com.godesii.godesii_services.service;

import com.godesii.godesii_services.entity.restaurant.MenuItem;
import com.godesii.godesii_services.repository.restaurant.MenuItemRepo;
import org.springframework.stereotype.Service;

import java.util.List;

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
        existing.setMenuName(item.getMenuName());
        existing.setCuisine(item.getCuisine());
        existing.setPrice(item.getPrice());
        existing.setImageUrl(item.getImageUrl());
        existing.setAvailable(item.isAvailable());
        existing.setDescription(item.getDescription());
        existing.setIngredients(item.getIngredients());
        existing.setMenuType(item.getMenuType());
        return repo.save(existing);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
