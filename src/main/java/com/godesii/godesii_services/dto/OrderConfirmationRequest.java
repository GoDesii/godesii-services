package com.godesii.godesii_services.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Restaurant confirmation request (Accept/Reject order)
 */
public class OrderConfirmationRequest {

    @Pattern(regexp = "^(ACCEPT|REJECT)$", message = "Action must be either ACCEPT or REJECT")
    @NotBlank(message = "Action is required")
    private String action;

    @Size(max = 500, message = "Rejection reason cannot exceed 500 characters")
    private String rejectionReason; // Required if action = REJECT

    @Min(value = 1, message = "Estimated preparation time must be at least 1 minute")
    private Integer estimatedPreparationTime; // Required if action = ACCEPT (in minutes)

    // Getters and Setters
    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public Integer getEstimatedPreparationTime() {
        return estimatedPreparationTime;
    }

    public void setEstimatedPreparationTime(Integer estimatedPreparationTime) {
        this.estimatedPreparationTime = estimatedPreparationTime;
    }
}
