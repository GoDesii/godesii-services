package com.godesii.godesii_services.service.restaurant;

import com.godesii.godesii_services.entity.restaurant.Category;
import com.godesii.godesii_services.exception.ResourceNotFoundException;
import com.godesii.godesii_services.repository.restaurant.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository repository;

    public CategoryService(CategoryRepository repository) {
        this.repository = repository;
    }

    public Category create(Category category) {
        return repository.save(category);
    }

    public List<Category> getAll() {
        return repository.findAll();
    }

    public Category getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    public Category update(Long id, Category category) {
        Category existing = getById(id);

        existing.setName(category.getName());
        existing.setDescription(category.getDescription());

        return repository.save(existing);
    }

    public void delete(Long id) {
        Category existing = getById(id);
        repository.delete(existing);
    }
}
