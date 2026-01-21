package com.godesii.godesii_services.repository.restaurant;

import com.godesii.godesii_services.entity.restaurant.NutritionalInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NutritionalInfoRepository extends JpaRepository<NutritionalInfo, Long> {
    
    /**
     * Find nutritional info by menu item ID
     */
    Optional<NutritionalInfo> findByMenuItem_ItemId(String itemId);
}
