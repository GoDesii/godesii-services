package com.godesii.godesii_services.service;

import com.godesii.godesii_services.dto.MenuRequest;
import com.godesii.godesii_services.entity.restaurant.Menu;
import com.godesii.godesii_services.entity.restaurant.Restaurant;
import com.godesii.godesii_services.exception.ResourceNotFoundException;
import com.godesii.godesii_services.repository.restaurant.MenuRepository;
import com.godesii.godesii_services.repository.restaurant.RestaurantRepo;
import com.godesii.godesii_services.service.restaurant.MenuService;
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

class MenuServiceTest {

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private RestaurantRepo restaurantRepository;

    @InjectMocks
    private MenuService menuService;

    private Restaurant restaurant;
    private Menu menu;
    private MenuRequest menuRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup test restaurant
        restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setName("Test Restaurant");

        // Setup test menu
        menu = new Menu();
        menu.setId(1L);
        menu.setName("Lunch Menu");
        menu.setDescription("Delicious lunch options");
        menu.setMenuType("LUNCH");
        menu.setSortOrder(1);
        menu.setActive(true);
        menu.setRestaurant(restaurant);

        // Setup test request
        menuRequest = new MenuRequest();
        menuRequest.setName("Lunch Menu");
        menuRequest.setDescription("Delicious lunch options");
        menuRequest.setMenuType("LUNCH");
        menuRequest.setSortOrder(1);
        menuRequest.setIsActive(true);
        menuRequest.setRestaurantId(1L);
    }

    @Test
    void testCreateMenu() {
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(menuRepository.save(any(Menu.class))).thenReturn(menu);

        Menu created = menuService.create(menuRequest);

        assertNotNull(created);
        assertEquals("Lunch Menu", created.getName());
        assertEquals("Delicious lunch options", created.getDescription());
        assertEquals("LUNCH", created.getMenuType());
        verify(restaurantRepository, times(1)).findById(1L);
        verify(menuRepository, times(1)).save(any(Menu.class));
    }

    @Test
    void testCreateMenuWithInvalidRestaurant() {
        when(restaurantRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            menuService.create(menuRequest);
        });

        verify(restaurantRepository, times(1)).findById(1L);
        verify(menuRepository, never()).save(any(Menu.class));
    }

    @Test
    void testGetAllMenus() {
        when(menuRepository.findAll()).thenReturn(List.of(menu));

        List<Menu> menus = menuService.getAll();

        assertEquals(1, menus.size());
        assertEquals("Lunch Menu", menus.get(0).getName());
        verify(menuRepository, times(1)).findAll();
    }

    @Test
    void testGetById() {
        when(menuRepository.findById(1L)).thenReturn(Optional.of(menu));

        Menu found = menuService.getById(1L);

        assertNotNull(found);
        assertEquals("Lunch Menu", found.getName());
        verify(menuRepository, times(1)).findById(1L);
    }

    @Test
    void testGetByIdNotFound() {
        when(menuRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            menuService.getById(999L);
        });

        verify(menuRepository, times(1)).findById(999L);
    }

    @Test
    void testGetByRestaurantId() {
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(menuRepository.findAll()).thenReturn(List.of(menu));

        List<Menu> menus = menuService.getByRestaurantId(1L);

        assertEquals(1, menus.size());
        assertEquals("Lunch Menu", menus.get(0).getName());
        verify(restaurantRepository, times(1)).findById(1L);
        verify(menuRepository, times(1)).findAll();
    }

    @Test
    void testGetByRestaurantIdWithInvalidRestaurant() {
        when(restaurantRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            menuService.getByRestaurantId(999L);
        });

        verify(restaurantRepository, times(1)).findById(999L);
        verify(menuRepository, never()).findAll();
    }

    @Test
    void testUpdateMenu() {
        MenuRequest updateRequest = new MenuRequest();
        updateRequest.setName("Updated Lunch Menu");
        updateRequest.setDescription("Updated description");
        updateRequest.setMenuType("DINNER");
        updateRequest.setRestaurantId(1L);

        when(menuRepository.findById(1L)).thenReturn(Optional.of(menu));
        when(menuRepository.save(any(Menu.class))).thenReturn(menu);

        Menu updated = menuService.update(1L, updateRequest);

        assertNotNull(updated);
        verify(menuRepository, times(1)).findById(1L);
        verify(menuRepository, times(1)).save(any(Menu.class));
    }

    @Test
    void testUpdateMenuNotFound() {
        when(menuRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            menuService.update(999L, menuRequest);
        });

        verify(menuRepository, times(1)).findById(999L);
        verify(menuRepository, never()).save(any(Menu.class));
    }

    @Test
    void testDeleteMenu() {
        when(menuRepository.findById(1L)).thenReturn(Optional.of(menu));
        doNothing().when(menuRepository).delete(menu);

        menuService.delete(1L);

        verify(menuRepository, times(1)).findById(1L);
        verify(menuRepository, times(1)).delete(menu);
    }

    @Test
    void testDeleteMenuNotFound() {
        when(menuRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            menuService.delete(999L);
        });

        verify(menuRepository, times(1)).findById(999L);
        verify(menuRepository, never()).delete(any(Menu.class));
    }
}
