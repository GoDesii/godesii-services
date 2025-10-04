package com.godesii.godesii_services.service;

import static org.junit.jupiter.api.Assertions.*;

import com.godesii.godesii_services.entity.restaurant.Restaurant;
import com.godesii.godesii_services.repository.restaurant.RestaurantRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;
import static org.mockito.Mockito.*;

class RestaurantServiceTest {

    @Mock
    private RestaurantRepo repository;

    @InjectMocks
    private RestaurantService service;

    private Restaurant restaurant;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        restaurant = new Restaurant();
        restaurant.setId(2L);
        restaurant.setRestaurantName("Pizza Palace");
        restaurant.setAddress("123 Main St");
        restaurant.setVerified(true);
    }

    @Test
    void testCreateRestaurant() {
        when(repository.save(any(Restaurant.class))).thenReturn(restaurant);
        Restaurant saved = service.create(restaurant);
        assertNotNull(saved);
        assertEquals("Pizza Palace", saved.getRestaurantName());
    }

    @Test
    void testGetAllWithPagination() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<Restaurant> page = new PageImpl<>(List.of(restaurant), pageable, 1);

        when(repository.findAll(pageable)).thenReturn(page);

        Page<Restaurant> result = service.getAll(pageable);
        assertEquals(1, result.getContent().size());
        assertEquals("Pizza Palace", result.getContent().get(0).getRestaurantName());
    }

    @Test
    void testGetById() {
        when(repository.findById(2L)).thenReturn(Optional.of(restaurant));
        Restaurant found = service.getById(2L);
        assertEquals("Pizza Palace", found.getRestaurantName());
    }

    @Test
    void testUpdateRestaurant() {
        when(repository.findById(2L)).thenReturn(Optional.of(restaurant));
        when(repository.save(any(Restaurant.class))).thenReturn(restaurant);

        Restaurant update = new Restaurant();
        update.setRestaurantName("Updated Name");
        Restaurant updated = service.update(2L, update);

        assertEquals("Updated Name", updated.getRestaurantName());
    }

    @Test
    void testDeleteRestaurant() {
        doNothing().when(repository).deleteById(2L);
        service.delete(2L);
        verify(repository, times(1)).deleteById(2L);
    }
}
