package com.godesii.godesii_services.repository.auth;

import com.godesii.godesii_services.entity.auth.ShippingAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShippingAddressRepository extends JpaRepository<ShippingAddress, Long> {

    Optional<List<ShippingAddress>> findAllByCreatedBy(String username);
}
