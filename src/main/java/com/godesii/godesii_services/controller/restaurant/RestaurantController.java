package com.godesii.godesii_services.controller.restaurant;

import com.godesii.godesii_services.entity.restaurant.Restaurant;
import com.godesii.godesii_services.service.FoodCertificateService;
import com.godesii.godesii_services.service.RestaurantService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/restaurants")
public class RestaurantController {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(RestaurantController.class);

    private final RestaurantService service;

    public RestaurantController(RestaurantService service) {
        this.service = service;
    }

    @PostMapping("/create")
    public Restaurant create(@RequestBody Restaurant restaurant) {
        log.info("Restaurant req {}",restaurant);
        return service.create(restaurant);
    }

    @GetMapping
    public Page<Restaurant> getAll(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "2") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        return service.getAll(pageable);
    }

    // GET restaurant by ID (without menu list)
    @GetMapping("/{id}")
    public Restaurant getById(@PathVariable Long id) {
        return service.getById(id);
    }
    @PutMapping("/{id}")
    public Restaurant update(@PathVariable Long id, @RequestBody Restaurant restaurant) {
        return service.update(id, restaurant);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
