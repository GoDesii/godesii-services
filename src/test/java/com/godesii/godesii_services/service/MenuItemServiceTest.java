package com.godesii.godesii_services.service;

import static org.junit.jupiter.api.Assertions.*;

import com.godesii.godesii_services.entity.restaurant.MenuItem;
import com.godesii.godesii_services.repository.restaurant.MenuItemRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

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
    void testGetAllMenuItems() {
        when(repository.findAll()).thenReturn(List.of(menuItem));
        List<MenuItem> list = service.getAll();
        assertEquals(1, list.size());
    }

    @Test
    void testGetById() {
        when(repository.findById(2L)).thenReturn(Optional.of(menuItem));
        MenuItem found = service.getById(2L);
        assertEquals("Margherita Pizza", found.getMenuName());
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
