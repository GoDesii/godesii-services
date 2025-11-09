package com.godesii.godesii_services.repository.restaurant;

import com.godesii.godesii_services.entity.restaurant.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepo extends JpaRepository<Review, Long> {}