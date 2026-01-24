package com.godesii.godesii_services.service.delivery;

import com.godesii.godesii_services.entity.delivery.AssignmentStatus;
import com.godesii.godesii_services.entity.delivery.DeliveryAssignment;
import com.godesii.godesii_services.entity.delivery.DeliveryPartner;
import com.godesii.godesii_services.entity.order.Order;
import com.godesii.godesii_services.exception.NoDeliveryPartnerAvailableException;
import com.godesii.godesii_services.exception.ResourceNotFoundException;
import com.godesii.godesii_services.repository.delivery.DeliveryAssignmentRepository;
import com.godesii.godesii_services.repository.delivery.DeliveryPartnerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing delivery partner assignments and tracking
 */
@Service
public class DeliveryService {

    private static final Logger log = LoggerFactory.getLogger(DeliveryService.class);
    private static final double INITIAL_SEARCH_RADIUS_KM = 5.0;
    private static final double MAX_SEARCH_RADIUS_KM = 15.0;
    private static final double RADIUS_INCREMENT_KM = 5.0;

    private final DeliveryPartnerRepository partnerRepo;
    private final DeliveryAssignmentRepository assignmentRepo;

    public DeliveryService(DeliveryPartnerRepository partnerRepo,
            DeliveryAssignmentRepository assignmentRepo) {
        this.partnerRepo = partnerRepo;
        this.assignmentRepo = assignmentRepo;
    }

    /**
     * Assign nearest available delivery partner to order
     * Uses expanding radius search: 5km → 10km → 15km
     */
    @Transactional
    public DeliveryAssignment assignDeliveryPartner(Order order) {
        log.info("Attempting to assign delivery partner for order: {}", order.getOrderId());

        // Get restaurant location (pickup point)
        // In production, fetch from restaurant entity
        BigDecimal pickupLat = new BigDecimal("12.9716"); // Mock: Bangalore
        BigDecimal pickupLng = new BigDecimal("77.5946");

        DeliveryPartner partner = null;
        double currentRadius = INITIAL_SEARCH_RADIUS_KM;
        int attempts = 0;

        // Try expanding radius search
        while (partner == null && currentRadius <= MAX_SEARCH_RADIUS_KM) {
            attempts++;
            log.info("Searching for partners within {}km radius (attempt {})", currentRadius, attempts);

            List<DeliveryPartner> nearbyPartners = partnerRepo.findAvailablePartnersNearLocation(
                    pickupLat, pickupLng, currentRadius);

            if (!nearbyPartners.isEmpty()) {
                partner = nearbyPartners.get(0); // Closest partner (sorted by distance, rating)
                log.info("Found delivery partner: {} at distance within {}km",
                        partner.getName(), currentRadius);
            } else {
                currentRadius += RADIUS_INCREMENT_KM;
            }
        }

        if (partner == null) {
            log.error("No delivery partners available for order: {}", order.getOrderId());
            throw new NoDeliveryPartnerAvailableException(
                    "No delivery partners available within " + MAX_SEARCH_RADIUS_KM + "km",
                    order.getOrderId(),
                    attempts);
        }

        // Create assignment
        DeliveryAssignment assignment = new DeliveryAssignment();
        assignment.setOrderId(order.getOrderId());
        assignment.setPartnerId(partner.getPartnerId());
        assignment.setStatus(AssignmentStatus.ASSIGNED);
        assignment.setAssignedAt(Instant.now());
        assignment.setPickupLat(pickupLat);
        assignment.setPickupLng(pickupLng);

        // Set drop location from order address (would come from
        // order.getOrderAddress())
        assignment.setDropLat(new BigDecimal("12.9352")); // Mock drop location
        assignment.setDropLng(new BigDecimal("77.6245"));

        DeliveryAssignment saved = assignmentRepo.save(assignment);
        log.info("Created delivery assignment: {} for partner: {}",
                saved.getAssignmentId(), partner.getName());

        return saved;
    }

    /**
     * Partner accepts delivery assignment
     */
    @Transactional
    public DeliveryAssignment partnerAcceptsDelivery(String assignmentId) {
        DeliveryAssignment assignment = assignmentRepo.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found: " + assignmentId));

        assignment.setStatus(AssignmentStatus.ACCEPTED);
        assignment.setAcceptedAt(Instant.now());

        // Mark partner as busy
        DeliveryPartner partner = partnerRepo.findById(assignment.getPartnerId())
                .orElseThrow(() -> new ResourceNotFoundException("Partner not found"));
        partner.setIsAvailable(false);
        partnerRepo.save(partner);

        log.info("Partner {} accepted assignment {}", assignment.getPartnerId(), assignmentId);
        return assignmentRepo.save(assignment);
    }

    /**
     * Partner rejects delivery - triggers reassignment
     */
    @Transactional
    public void partnerRejectsDelivery(String assignmentId, String reason) {
        DeliveryAssignment assignment = assignmentRepo.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found: " + assignmentId));

        assignment.setStatus(AssignmentStatus.REJECTED);
        assignment.setRejectedAt(Instant.now());
        assignment.setRejectionReason(reason);
        assignmentRepo.save(assignment);

        log.warn("Partner {} rejected assignment {} - Reason: {}",
                assignment.getPartnerId(), assignmentId, reason);
    }

    /**
     * Reassign delivery to another partner
     */
    @Transactional
    public DeliveryAssignment reassignDelivery(String orderId) {
        log.info("Reassigning delivery for order: {}", orderId);

        // Cancel existing active assignment
        Optional<DeliveryAssignment> existingAssignment = assignmentRepo.findByOrderIdAndStatusNot(orderId,
                AssignmentStatus.CANCELLED);

        existingAssignment.ifPresent(assignment -> {
            assignment.setStatus(AssignmentStatus.CANCELLED);
            assignmentRepo.save(assignment);
        });

        // This would trigger new assignment in OrderService
        throw new NoDeliveryPartnerAvailableException(
                "Reassignment needed for order: " + orderId, orderId, 1);
    }

    /**
     * Cancel delivery assignment (when order is cancelled)
     */
    @Transactional
    public void cancelAssignment(String orderId) {
        Optional<DeliveryAssignment> assignment = assignmentRepo.findByOrderId(orderId);

        assignment.ifPresent(a -> {
            if (a.getStatus() != AssignmentStatus.DELIVERED) {
                a.setStatus(AssignmentStatus.CANCELLED);
                assignmentRepo.save(a);

                // Make partner available again if they accepted
                if (a.getStatus() == AssignmentStatus.ACCEPTED) {
                    partnerRepo.findById(a.getPartnerId()).ifPresent(partner -> {
                        partner.setIsAvailable(true);
                        partnerRepo.save(partner);
                    });
                }

                log.info("Cancelled delivery assignment for order: {}", orderId);
            }
        });
    }

    /**
     * Update delivery partner location for real-time tracking
     */
    @Transactional
    public void updateDeliveryLocation(String partnerId, BigDecimal lat, BigDecimal lng) {
        DeliveryPartner partner = partnerRepo.findById(partnerId)
                .orElseThrow(() -> new ResourceNotFoundException("Partner not found: " + partnerId));

        partner.setCurrentLat(lat);
        partner.setCurrentLng(lng);
        partner.setLastActive(Instant.now());
        partnerRepo.save(partner);
    }

    /**
     * Mark order as picked up by delivery partner
     */
    @Transactional
    public DeliveryAssignment markAsPickedUp(String assignmentId) {
        DeliveryAssignment assignment = assignmentRepo.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found: " + assignmentId));

        assignment.setStatus(AssignmentStatus.PICKED_UP);
        assignment.setPickedUpAt(Instant.now());

        log.info("Order {} picked up by partner {}",
                assignment.getOrderId(), assignment.getPartnerId());

        return assignmentRepo.save(assignment);
    }
}
