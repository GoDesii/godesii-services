//package com.godesii.godesii_services.service.restaurant;
//
//import com.godesii.godesii_services.entity.restaurant.Review;
//import com.godesii.godesii_services.exception.ResourceNotFoundException;
//import com.godesii.godesii_services.repository.restaurant.ReviewRepo;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Optional;
//
//@Service
//@Slf4j
//public class ReviewService {
//
//    private final ReviewRepo repo;
//
//    public ReviewService(ReviewRepo repo) {
//        this.repo = repo;
//    }
//
//    // CREATE
//    public Review create(Review review) {
//        Review saved = repo.save(review);
////        log.info("Created Review: {}", saved);
//        return saved;
//    }
//
//    // GET ALL
//    public List<Review> getAll() {
//        List<Review> reviews = repo.findAll();
////        log.info("Fetched {} reviews", reviews.size());
//        return reviews;
//    }
//
//    // GET BY ID (Exception Safe)
//    public Review getById(Long id) {
//        return repo.findById(id)
//                .orElseThrow(() -> {
////                    log.warn("Review not found with ID {}", id);
//                    return new ResourceNotFoundException("Review not found with ID: " + id);
//                });
//    }
//
//    // UPDATE
//    public Review update(Long id, Review review) {
//        Review existing = getById(id); // throws ResourceNotFoundException
//
//        Optional.ofNullable(review.getRating()).ifPresent(existing::setRating);
//        Optional.ofNullable(review.getComment()).ifPresent(existing::setComment);
//
//        Review updated = repo.save(existing);
////        log.info("Updated Review with ID {}: {}", id, updated);
//        return updated;
//    }
//
//    // DELETE
//    public void delete(Long id) {
//        Review existing = getById(id); // throws ResourceNotFoundException
//        repo.delete(existing);
////        log.info("Deleted Review with ID {}", id);
//    }
//}
