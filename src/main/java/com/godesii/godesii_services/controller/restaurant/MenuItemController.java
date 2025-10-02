package com.godesii.godesii_services.controller.restaurant;

import com.godesii.godesii_services.entity.restaurant.MenuItem;
import com.godesii.godesii_services.service.MenuItemService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/menu-items")
public class MenuItemController {

    private final MenuItemService service;

    public MenuItemController(MenuItemService service) {
        this.service = service;
    }

    @PostMapping
    public MenuItem create(@RequestBody MenuItem menuItem) {
        return service.create(menuItem);
    }

    @GetMapping
    public List<MenuItem> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public MenuItem getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public MenuItem update(@PathVariable Long id, @RequestBody MenuItem menuItem) {
        return service.update(id, menuItem);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
