package com.godesii.godesii_services.service;

import com.godesii.godesii_services.entity.restaurant.Review;
import com.godesii.godesii_services.repository.restaurant.ReviewRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ReviewService.class);

    private final ReviewRepo repo;

    public ReviewService(ReviewRepo repo) {
        this.repo = repo;
    }

    public Review create(Review review) {
        return repo.save(review);
    }

    public List<Review> getAll() {
        return repo.findAll();
    }

    public Review getById(Long id) {
        return repo.findById(id).orElseThrow();
    }

    public Review update(Long id, Review review) {
        Review existing = getById(id);

        Optional.ofNullable(review.getRating()).ifPresent(existing::setRating);
        Optional.ofNullable(review.getComment()).ifPresent(existing::setComment);

        log.info("updated Review {}",existing);
        return repo.save(existing);
    }


    public void delete(Long id) {
        repo.deleteById(id);
    }
}
