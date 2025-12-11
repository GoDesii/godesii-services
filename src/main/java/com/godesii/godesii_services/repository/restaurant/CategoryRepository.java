package com.godesii.godesii_services.repository.restaurant;

import com.godesii.godesii_services.entity.restaurant.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
