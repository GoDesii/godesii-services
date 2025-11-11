package com.godesii.godesii_services.service;

import static org.junit.jupiter.api.Assertions.*;

import com.godesii.godesii_services.entity.restaurant.Review;
import com.godesii.godesii_services.repository.restaurant.ReviewRepo;
import com.godesii.godesii_services.service.restaurant.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

class ReviewServiceTest {

    @Mock
    private ReviewRepo repository;

    @InjectMocks
    private ReviewService service;

    private Review review;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        review = new Review();
        review.setId(2L);
        review.setRating("5");
        review.setComment("Great!");
    }

    @Test
    void testCreateReview() {
        when(repository.save(any(Review.class))).thenReturn(review);
        Review saved = service.create(review);
        assertEquals("5", saved.getRating());
    }

    @Test
    void testGetAllReviews() {
        when(repository.findAll()).thenReturn(List.of(review));
        List<Review> list = service.getAll();
        assertEquals(1, list.size());
    }

    @Test
    void testGetById() {
        when(repository.findById(2L)).thenReturn(Optional.of(review));
        Review found = service.getById(2L);
        assertEquals("Great!", found.getComment());
    }

    @Test
    void testUpdateReview() {
        when(repository.findById(2L)).thenReturn(Optional.of(review));
        when(repository.save(any(Review.class))).thenReturn(review);

        Review update = new Review();
        update.setComment("Good but could improve");
        Review updated = service.update(2L, update);

        assertEquals("Good but could improve", updated.getComment());
    }

    @Test
    void testDeleteReview() {
        doNothing().when(repository).deleteById(2L);
        service.delete(2L);
        verify(repository, times(1)).deleteById(2L);
    }
}
