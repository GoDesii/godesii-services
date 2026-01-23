package com.godesii.godesii_services.repository.delivery;

import com.godesii.godesii_services.entity.delivery.DeliveryPartner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface DeliveryPartnerRepository extends JpaRepository<DeliveryPartner, String> {

    /**
     * Find all available delivery partners
     */
    List<DeliveryPartner> findByIsAvailableTrue();

    /**
     * Find available partners near a location using Haversine formula
     * Formula: distance = 6371 * acos(cos(radians(lat1)) * cos(radians(lat2)) *
     * cos(radians(lng2) - radians(lng1)) + sin(radians(lat1)) * sin(radians(lat2)))
     * 
     * @param lat      Latitude
     * @param lng      Longitude
     * @param radiusKm Search radius in kilometers
     * @return List of partners within radius, sorted by distance
     */
    @Query(value = "SELECT *, " +
            "(6371 * acos(cos(radians(:lat)) * cos(radians(current_lat)) * " +
            "cos(radians(current_lng) - radians(:lng)) + sin(radians(:lat)) * " +
            "sin(radians(current_lat)))) AS distance " +
            "FROM delivery_partners " +
            "WHERE is_available = true " +
            "HAVING distance < :radiusKm " +
            "ORDER BY distance, rating DESC, total_deliveries ASC", nativeQuery = true)
    List<DeliveryPartner> findAvailablePartnersNearLocation(
            @Param("lat") BigDecimal lat,
            @Param("lng") BigDecimal lng,
            @Param("radiusKm") double radiusKm);
}
