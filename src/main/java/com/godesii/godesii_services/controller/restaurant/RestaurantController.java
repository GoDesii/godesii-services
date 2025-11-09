package com.godesii.godesii_services.controller.restaurant;

import com.godesii.godesii_services.constant.GoDesiiConstant;
import com.godesii.godesii_services.entity.restaurant.Restaurant;
import com.godesii.godesii_services.service.FoodCertificateService;
import com.godesii.godesii_services.service.RestaurantService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(RestaurantController.ENDPOINT)
public class RestaurantController {

    public static final String ENDPOINT = GoDesiiConstant.API_VERSION + "/restaurants";

    private final RestaurantService service;

    public RestaurantController(RestaurantService service) {
        this.service = service;
    }

    @PostMapping("/create")
    public Restaurant create(@RequestBody Restaurant restaurant) {
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
