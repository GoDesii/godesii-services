package com.godesii.godesii_services.repository.delivery;

import com.godesii.godesii_services.entity.delivery.AssignmentStatus;
import com.godesii.godesii_services.entity.delivery.DeliveryAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryAssignmentRepository extends JpaRepository<DeliveryAssignment, String> {

    /**
     * Find assignment by order ID
     */
    Optional<DeliveryAssignment> findByOrderId(String orderId);

    /**
     * Find current active assignment for order (not rejected or cancelled)
     */
    Optional<DeliveryAssignment> findByOrderIdAndStatusNot(String orderId, AssignmentStatus status);

    /**
     * Find all assignments for a delivery partner
     */
    List<DeliveryAssignment> findByPartnerId(String partnerId);

    /**
     * Find active assignments for a delivery partner
     */
    List<DeliveryAssignment> findByPartnerIdAndStatus(String partnerId, AssignmentStatus status);

    /**
     * Find all assignments for an order (including rejected/reassigned)
     */
    List<DeliveryAssignment> findAllByOrderId(String orderId);

    /**
     * Find the currently active (ACCEPTED or PICKED_UP) assignment for a partner.
     * Used by the WebSocket location handler to map location updates to an order.
     */
    @Query("SELECT a FROM DeliveryAssignment a WHERE a.partnerId = :partnerId " +
           "AND a.status IN (com.godesii.godesii_services.entity.delivery.AssignmentStatus.ACCEPTED, " +
           "                 com.godesii.godesii_services.entity.delivery.AssignmentStatus.PICKED_UP)")
    Optional<DeliveryAssignment> findActiveAssignmentByPartnerId(@Param("partnerId") String partnerId);
}
