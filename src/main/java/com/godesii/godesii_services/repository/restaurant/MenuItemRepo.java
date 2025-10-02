package com.godesii.godesii_services.repository.restaurant;

import com.godesii.godesii_services.entity.restaurant.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuItemRepo extends JpaRepository<MenuItem, Long> {}
