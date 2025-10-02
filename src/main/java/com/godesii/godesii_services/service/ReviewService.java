package com.godesii.godesii_services.service;

import com.godesii.godesii_services.entity.restaurant.Review;
import com.godesii.godesii_services.repository.restaurant.ReviewRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {

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
        existing.setRating(review.getRating());
        existing.setComment(review.getComment());
        return repo.save(existing);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
