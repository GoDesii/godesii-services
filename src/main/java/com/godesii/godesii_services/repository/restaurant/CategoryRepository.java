package com.godesii.godesii_services.repository.restaurant;

import com.godesii.godesii_services.entity.restaurant.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    /**
     * Find all categories for a specific menu
     */
    List<Category> findByMenuId(Long menuId);
    
    /**
     * Find category by name and menu ID (for duplicate checking)
     */
    Optional<Category> findByNameAndMenuId(String name, Long menuId);
}
