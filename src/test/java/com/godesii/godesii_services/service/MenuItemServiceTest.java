package com.godesii.godesii_services.service;

import static org.junit.jupiter.api.Assertions.*;

import com.godesii.godesii_services.entity.restaurant.MenuItem;
import com.godesii.godesii_services.repository.restaurant.MenuItemRepo;
import com.godesii.godesii_services.service.restaurant.MenuItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import static org.mockito.Mockito.*;

class MenuItemServiceTest {

    @Mock
    private MenuItemRepo repository;

    @InjectMocks
    private MenuItemService service;

    private MenuItem menuItem;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        menuItem = new MenuItem();
        menuItem.setId(2L);
        menuItem.setMenuName("Margherita Pizza");
        menuItem.setPrice("250");
    }

    @Test
    void testCreateMenuItem() {
        when(repository.save(any(MenuItem.class))).thenReturn(menuItem);
        MenuItem saved = service.create(menuItem);
        assertEquals("Margherita Pizza", saved.getMenuName());
    }

    @Test
    void testGetAllWithPagination() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<MenuItem> page = new PageImpl<>(List.of(menuItem), pageable, 1);

        when(repository.findAll(pageable)).thenReturn(page);

        Page<MenuItem> result = service.getAll(pageable);
        assertEquals(1, result.getContent().size());
        assertEquals("Margherita Pizza", result.getContent().get(0).getMenuName());
    }

    @Test
    void testGetByRestaurantIdWithPagination() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<MenuItem> page = new PageImpl<>(List.of(menuItem), pageable, 1);

        when(repository.findByRestaurantId(1L, pageable)).thenReturn(page);

        Page<MenuItem> result = service.getByRestaurantId(1L, pageable);
        assertEquals(1, result.getContent().size());
        assertEquals("Margherita Pizza", result.getContent().get(0).getMenuName());
    }


    @Test
    void testUpdateMenuItem() {
        when(repository.findById(2L)).thenReturn(Optional.of(menuItem));
        when(repository.save(any(MenuItem.class))).thenReturn(menuItem);

        MenuItem update = new MenuItem();
        update.setMenuName("Updated Pizza");
        MenuItem updated = service.update(2L, update);

        assertEquals("Updated Pizza", updated.getMenuName());
    }

    @Test
    void testDeleteMenuItem() {
        doNothing().when(repository).deleteById(2L);
        service.delete(2L);
        verify(repository, times(1)).deleteById(2L);
    }
}
