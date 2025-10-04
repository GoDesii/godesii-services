package com.godesii.godesii_services.controller.restaurant;

import com.godesii.godesii_services.entity.restaurant.MenuItem;
import com.godesii.godesii_services.service.FoodCertificateService;
import com.godesii.godesii_services.service.MenuItemService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/menu-items")
public class MenuItemController {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MenuItemController.class);

    private final MenuItemService service;

    public MenuItemController(MenuItemService service) {
        this.service = service;
    }

    @PostMapping
    public MenuItem create(@RequestBody MenuItem menuItem) {
        log.info("MenuItem req {}",menuItem);
        return service.create(menuItem);
    }

    @GetMapping("/get/{id}")
    public MenuItem getByMenuId(@PathVariable Long id){
        return service.getByMenuId(id);
    }
    @GetMapping
    public Page<MenuItem> getAll(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        return service.getAll(pageable);
    }

    // Menu items by restaurant ID
    @GetMapping("/{restaurantId}")
    public Page<MenuItem> getByRestaurantId(@PathVariable Long restaurantId,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "2") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return service.getByRestaurantId(restaurantId, pageable);
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
