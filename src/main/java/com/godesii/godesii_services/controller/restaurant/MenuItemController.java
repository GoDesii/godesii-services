package com.godesii.godesii_services.controller.restaurant;

import com.godesii.godesii_services.constant.GoDesiiConstant;
import com.godesii.godesii_services.entity.restaurant.MenuItem;
import com.godesii.godesii_services.service.restaurant.MenuItemService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(MenuItemController.ENDPOINT)
public class MenuItemController {

    public static final String ENDPOINT = GoDesiiConstant.API_VERSION + "/menu-items";

    private final MenuItemService service;

    public MenuItemController(MenuItemService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<MenuItem> create(@RequestBody MenuItem menuItem) {
        MenuItem created = service.create(menuItem);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<MenuItem> getByMenuId(@PathVariable Long id) {
        MenuItem item = service.getByMenuId(id);
        return item != null ? ResponseEntity.ok(item) : ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<Page<MenuItem>> getAll(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<MenuItem> items = service.getAll(pageable);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{restaurantId}")
    public ResponseEntity<Page<MenuItem>> getByRestaurantId(@PathVariable Long restaurantId,
                                                            @RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "2") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<MenuItem> items = service.getByRestaurantId(restaurantId, pageable);
        return ResponseEntity.ok(items);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MenuItem> update(@PathVariable Long id, @RequestBody MenuItem menuItem) {
        MenuItem updated = service.update(id, menuItem);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<MenuItem>> searchMenus(
            @RequestParam(required = false) String restaurantName,
            @RequestParam(required = false) String menuName) {

        List<MenuItem> menus = service.getMenus(restaurantName, menuName);

        if (menus.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(menus);
    }
}
