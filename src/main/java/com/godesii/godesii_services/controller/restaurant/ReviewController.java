package com.godesii.godesii_services.controller.restaurant;


import com.godesii.godesii_services.entity.restaurant.Review;
import com.godesii.godesii_services.service.FoodCertificateService;
import com.godesii.godesii_services.service.ReviewService;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/reviews")
public class ReviewController {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ReviewController.class);

    private final ReviewService service;

    public ReviewController(ReviewService service) {
        this.service = service;
    }

    @PostMapping
    public Review create(@RequestBody Review review) {
        log.info("Review req {}",review);
        return service.create(review);
    }

    @GetMapping
    public List<Review> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Review getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public Review update(@PathVariable Long id, @RequestBody Review review) {
        return service.update(id, review);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
