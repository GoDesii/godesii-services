package com.godesii.godesii_services.controller.delivery;

import com.godesii.godesii_services.common.APIResponse;
import com.godesii.godesii_services.entity.delivery.DeliveryAssignment;
import com.godesii.godesii_services.entity.delivery.DeliveryPartner;
import com.godesii.godesii_services.service.delivery.DeliveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * REST Controller for Delivery Partner operations
 */
@RestController
@RequestMapping("/api/v1/delivery")
@Tag(name = "Delivery Management", description = "Delivery partner and assignment management APIs")
public class DeliveryController {

    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    /**
     * Partner accepts delivery assignment
     */
    @PostMapping(value = "/assignments/{assignmentId}/accept", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Accept delivery", description = "Delivery partner accepts an assigned order")
    public ResponseEntity<APIResponse<DeliveryAssignment>> acceptDelivery(
            @PathVariable @NonNull String assignmentId) {

        DeliveryAssignment assignment = deliveryService.partnerAcceptsDelivery(assignmentId);

        APIResponse<DeliveryAssignment> apiResponse = new APIResponse<>(
                HttpStatus.OK,
                assignment,
                "Delivery assignment accepted successfully");

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Partner rejects delivery assignment
     */
    @PostMapping(value = "/assignments/{assignmentId}/reject", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Reject delivery", description = "Delivery partner rejects an assigned order")
    public ResponseEntity<APIResponse<String>> rejectDelivery(
            @PathVariable @NonNull String assignmentId,
            @RequestParam String reason) {

        deliveryService.partnerRejectsDelivery(assignmentId, reason);

        APIResponse<String> apiResponse = new APIResponse<>(
                HttpStatus.OK,
                "Assignment rejected - will be reassigned to another partner",
                "Delivery assignment rejected");

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Partner marks order as picked up from restaurant
     */
    @PostMapping(value = "/assignments/{assignmentId}/pickup", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Mark as picked up", description = "Delivery partner confirms order pickup from restaurant")
    public ResponseEntity<APIResponse<DeliveryAssignment>> markAsPickedUp(
            @PathVariable @NonNull String assignmentId) {

        DeliveryAssignment assignment = deliveryService.markAsPickedUp(assignmentId);

        APIResponse<DeliveryAssignment> apiResponse = new APIResponse<>(
                HttpStatus.OK,
                assignment,
                "Order marked as picked up");

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Update delivery partner's current location for real-time tracking
     */
    @PostMapping(value = "/partners/{partnerId}/location", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update location", description = "Update delivery partner's real-time location")
    public ResponseEntity<APIResponse<String>> updateLocation(
            @PathVariable @NonNull String partnerId,
            @RequestParam BigDecimal latitude,
            @RequestParam BigDecimal longitude) {

        deliveryService.updateDeliveryLocation(partnerId, latitude, longitude);

        APIResponse<String> apiResponse = new APIResponse<>(
                HttpStatus.OK,
                String.format("Location updated: %.6f, %.6f", latitude, longitude),
                "Location updated successfully");

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Cancel delivery assignment (when order is cancelled)
     */
    @PostMapping(value = "/assignments/order/{orderId}/cancel", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Cancel assignment", description = "Cancel delivery assignment for an order")
    public ResponseEntity<APIResponse<String>> cancelAssignment(
            @PathVariable @NonNull String orderId) {

        deliveryService.cancelAssignment(orderId);

        APIResponse<String> apiResponse = new APIResponse<>(
                HttpStatus.OK,
                "Delivery assignment cancelled",
                "Assignment cancelled successfully");

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Reassign delivery to another partner
     */
    @PostMapping(value = "/assignments/order/{orderId}/reassign", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Reassign delivery", description = "Reassign delivery to another available partner")
    public ResponseEntity<APIResponse<String>> reassignDelivery(
            @PathVariable @NonNull String orderId) {

        try {
            deliveryService.reassignDelivery(orderId);

            APIResponse<String> apiResponse = new APIResponse<>(
                    HttpStatus.OK,
                    "Delivery reassignment initiated",
                    "Reassignment in progress");

            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            APIResponse<String> apiResponse = new APIResponse<>(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    null,
                    "Reassignment failed: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
        }
    }
}
