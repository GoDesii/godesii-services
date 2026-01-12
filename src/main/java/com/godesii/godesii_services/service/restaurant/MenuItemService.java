//package com.godesii.godesii_services.service.restaurant;
//
//import com.godesii.godesii_services.entity.restaurant.Category;
//import com.godesii.godesii_services.entity.restaurant.MenuItem;
//import com.godesii.godesii_services.entity.restaurant.Restaurant;
//import com.godesii.godesii_services.exception.ResourceNotFoundException;
//import com.godesii.godesii_services.repository.restaurant.CategoryRepository;
//import com.godesii.godesii_services.repository.restaurant.MenuItemRepo;
//import com.godesii.godesii_services.repository.restaurant.RestaurantRepo;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Optional;
//
//@Service
//@Slf4j
//public class MenuItemService {
//
//    private final MenuItemRepo repo;
//    private final RestaurantRepo restaurantRepo;
//    private final CategoryRepository categoryRepo;
//
//    public MenuItemService(MenuItemRepo repo, RestaurantRepo restaurantRepo,
//                           CategoryRepository categoryRepo) {
//        this.repo = repo;
//        this.restaurantRepo = restaurantRepo;
//        this.categoryRepo = categoryRepo;
//    }
//
//    public MenuItem create(MenuItem item) {
//
//        if (item.getRestaurant() == null || item.getRestaurant().getId() == null) {
//            throw new IllegalArgumentException("Restaurant ID is required");
//        }
//
//        Restaurant restaurant = restaurantRepo.findById(item.getRestaurant().getId())
//                .orElseThrow(() -> new ResourceNotFoundException(
//                        "Restaurant not found with ID: " + item.getRestaurant().getId()
//                ));
//        item.setRestaurant(restaurant);
//
//        if (item.getCategory() != null && item.getCategory().getId() != null) {
//            Category category = categoryRepo.findById(item.getCategory().getId())
//                    .orElseThrow(() -> new ResourceNotFoundException(
//                            "Category not found with ID: " + item.getCategory().getId()
//                    ));
//            item.setCategory(category);
//        }
//
//        MenuItem saved = repo.save(item);
////        log.info("Created MenuItem: {}", saved);
//        return saved;
//    }
//
//    public MenuItem getByMenuId(Long id) {
//        return repo.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("MenuItem not found with ID: " + id));
//    }
//
//    public Page<MenuItem> getAll(Pageable pageable) {
//        return repo.findAll(pageable);
//    }
//
//    public Page<MenuItem> getByRestaurantId(Long restaurantId, Pageable pageable) {
//        return repo.findByRestaurantId(restaurantId, pageable);
//    }
//
//    public MenuItem update(Long id, MenuItem item) {
//        MenuItem existing = getByMenuId(id);
//
//        Optional.ofNullable(item.getMenuName()).ifPresent(existing::setMenuName);
//        Optional.ofNullable(item.getCuisine()).ifPresent(existing::setCuisine);
//        Optional.ofNullable(item.getPrice()).ifPresent(existing::setPrice);
//        Optional.ofNullable(item.getImageUrl()).ifPresent(existing::setImageUrl);
//        Optional.ofNullable(item.getDescription()).ifPresent(existing::setDescription);
//        Optional.ofNullable(item.getIngredients()).ifPresent(existing::setIngredients);
//        Optional.ofNullable(item.getMenuType()).ifPresent(existing::setMenuType);
//
//        // Update Restaurant if provided
//        if (item.getRestaurant() != null && item.getRestaurant().getId() != null) {
//            Restaurant restaurant = restaurantRepo.findById(item.getRestaurant().getId())
//                    .orElseThrow(() -> new ResourceNotFoundException(
//                            "Restaurant not found with ID: " + item.getRestaurant().getId()
//                    ));
//            existing.setRestaurant(restaurant);
//        }
//
//        // Update Category if provided
//        if (item.getCategory() != null && item.getCategory().getId() != null) {
//            Category category = categoryRepo.findById(item.getCategory().getId())
//                    .orElseThrow(() -> new ResourceNotFoundException(
//                            "Category not found with ID: " + item.getCategory().getId()
//                    ));
//            existing.setCategory(category);
//        }
//
//        // Boolean fields
//        existing.setAvailable(item.isAvailable());
//
//        MenuItem updated = repo.save(existing);
////        log.info("Updated MenuItem: {}", updated);
//        return updated;
//    }
//
//    // DELETE
//    public void delete(Long id) {
//        MenuItem existing = getByMenuId(id);
//        repo.delete(existing);
////        log.info("Deleted MenuItem with ID {}", id);
//    }
//
//    // SEARCH
//    public List<MenuItem> getMenus(String restaurantName, String menuName) {
//        if (restaurantName != null && menuName != null) {
//            return repo.findByRestaurant_RestaurantNameAndMenuName(restaurantName, menuName);
//        } else if (restaurantName != null) {
//            return repo.findByRestaurant_RestaurantName(restaurantName);
//        } else if (menuName != null) {
//            return repo.findByMenuName(menuName);
//        } else {
//            return repo.findAll();
//        }
//    }
//}
