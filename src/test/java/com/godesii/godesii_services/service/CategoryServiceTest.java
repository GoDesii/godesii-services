package com.godesii.godesii_services.service;

import com.godesii.godesii_services.dto.CategoryRequest;
import com.godesii.godesii_services.entity.restaurant.Category;
import com.godesii.godesii_services.entity.restaurant.Menu;
import com.godesii.godesii_services.exception.ResourceNotFoundException;
import com.godesii.godesii_services.repository.restaurant.CategoryRepository;
import com.godesii.godesii_services.repository.restaurant.MenuRepository;
import com.godesii.godesii_services.service.restaurant.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private MenuRepository menuRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Menu menu;
    private Category category;
    private CategoryRequest categoryRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup test menu
        menu = new Menu();
        menu.setId(1L);
        menu.setName("Main Menu");

        // Setup test category
        category = new Category();
        category.setId(1L);
        category.setName("Appetizers");
        category.setDescription("Starter dishes");
        category.setDisplayOrder(1);
        category.setMenu(menu);

        // Setup test request
        categoryRequest = new CategoryRequest();
        categoryRequest.setName("Appetizers");
        categoryRequest.setDescription("Starter dishes");
        categoryRequest.setDisplayOrder(1);
        categoryRequest.setMenuId(1L);
    }

    @Test
    void testCreateCategory() {
        when(menuRepository.findById(1L)).thenReturn(Optional.of(menu));
        when(categoryRepository.findByNameAndMenuId("Appetizers", 1L)).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        Category created = categoryService.create(categoryRequest);

        assertNotNull(created);
        assertEquals("Appetizers", created.getName());
        assertEquals("Starter dishes", created.getDescription());
        verify(menuRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void testCreateCategoryWithInvalidMenu() {
        when(menuRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            categoryService.create(categoryRequest);
        });

        verify(menuRepository, times(1)).findById(1L);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void testCreateCategoryWithDuplicateName() {
        when(menuRepository.findById(1L)).thenReturn(Optional.of(menu));
        when(categoryRepository.findByNameAndMenuId("Appetizers", 1L))
                .thenReturn(Optional.of(category));

        assertThrows(IllegalArgumentException.class, () -> {
            categoryService.create(categoryRequest);
        });

        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void testGetAllCategories() {
        when(categoryRepository.findAll()).thenReturn(List.of(category));

        List<Category> categories = categoryService.getAll();

        assertEquals(1, categories.size());
        assertEquals("Appetizers", categories.get(0).getName());
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    void testGetById() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        Category found = categoryService.getById(1L);

        assertNotNull(found);
        assertEquals("Appetizers", found.getName());
        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    void testGetByIdNotFound() {
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            categoryService.getById(999L);
        });

        verify(categoryRepository, times(1)).findById(999L);
    }

    @Test
    void testGetByMenuId() {
        when(menuRepository.findById(1L)).thenReturn(Optional.of(menu));
        when(categoryRepository.findByMenuId(1L)).thenReturn(List.of(category));

        List<Category> categories = categoryService.getByMenuId(1L);

        assertEquals(1, categories.size());
        assertEquals("Appetizers", categories.get(0).getName());
        verify(menuRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).findByMenuId(1L);
    }

    @Test
    void testGetByMenuIdWithInvalidMenu() {
        when(menuRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            categoryService.getByMenuId(999L);
        });

        verify(menuRepository, times(1)).findById(999L);
        verify(categoryRepository, never()).findByMenuId(anyLong());
    }

    @Test
    void testUpdateCategory() {
        CategoryRequest updateRequest = new CategoryRequest();
        updateRequest.setName("Updated Appetizers");
        updateRequest.setDescription("Updated description");
        updateRequest.setMenuId(1L);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.findByNameAndMenuId("Updated Appetizers", 1L))
                .thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        Category updated = categoryService.update(1L, updateRequest);

        assertNotNull(updated);
        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void testUpdateCategoryNotFound() {
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            categoryService.update(999L, categoryRequest);
        });

        verify(categoryRepository, times(1)).findById(999L);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void testDeleteCategory() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        doNothing().when(categoryRepository).delete(category);

        categoryService.delete(1L);

        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).delete(category);
    }

    @Test
    void testDeleteCategoryNotFound() {
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            categoryService.delete(999L);
        });

        verify(categoryRepository, times(1)).findById(999L);
        verify(categoryRepository, never()).delete(any(Category.class));
    }
}
