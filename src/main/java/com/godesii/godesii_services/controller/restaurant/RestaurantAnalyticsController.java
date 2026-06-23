package com.godesii.godesii_services.controller.restaurant;

import com.godesii.godesii_services.common.APIResponse;
import com.godesii.godesii_services.dto.analytics.EarningsFilterRequest;
import com.godesii.godesii_services.dto.analytics.RestaurantEarningsResponse;
import com.godesii.godesii_services.entity.order.OrderStatus;
import com.godesii.godesii_services.entity.restaurant.RestaurantCommission;
import com.godesii.godesii_services.repository.restaurant.RestaurantCommissionRepository;
import com.godesii.godesii_services.security.UserPrincipal;
import com.godesii.godesii_services.service.restaurant.RestaurantAnalyticsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;

/**
 * REST Controller for Restaurant Earnings Analytics.
 *
 * Base path: /api/v1/analytics/restaurant
 *
 * Access matrix:
 * ┌─────────────────────────────────────────────────────┬──────────┬─────────┐
 * │ Endpoint                                            │ VENDOR   │ ADMIN   │
 * ├─────────────────────────────────────────────────────┼──────────┼─────────┤
 * │ GET  /earnings          (restaurantId from JWT)     │   ✅     │   ❌    │
 * │ GET  /admin/{id}/earnings (any restaurant)          │   ❌     │   ✅    │
 * │ GET  /commission        (restaurantId from JWT)     │   ✅     │   ❌    │
 * │ GET  /admin/{id}/commission (any restaurant)        │   ❌     │   ✅    │
 * │ PUT  /{id}/commission                               │   ❌     │   ✅    │
 * └─────────────────────────────────────────────────────┴──────────┴─────────┘
 */
@RestController
@RequestMapping("/api/v1/analytics/restaurant")
public class RestaurantAnalyticsController {

    private final RestaurantAnalyticsService analyticsService;
    private final RestaurantCommissionRepository commissionRepository;

    public RestaurantAnalyticsController(RestaurantAnalyticsService analyticsService,
                                         RestaurantCommissionRepository commissionRepository) {
        this.analyticsService = analyticsService;
        this.commissionRepository = commissionRepository;
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // GET /earnings  — VENDOR only (restaurantId resolved from JWT principal)
    // ─────────────────────────────────────────────────────────────────────────────

    /**
     * Fetches earnings analytics for the authenticated restaurant owner (VENDOR role).
     * The restaurantId is automatically resolved from the JWT principal — no path variable needed.
     *
     * @param principal   The authenticated user (restaurantId extracted from here)
     * @param startDate   Start of date range (ISO date: yyyy-MM-dd)
     * @param endDate     End of date range (ISO date: yyyy-MM-dd)
     * @param minAmount   Minimum order total filter (in rupees)
     * @param maxAmount   Maximum order total filter (in rupees)
     * @param orderStatus Filter by order status (default: DELIVERED)
     * @param groupBy     Chart grouping: DAY | WEEK | MONTH (default: DAY)
     * @param page        Page number (default: 0)
     * @param size        Page size (default: 20, max: 100)
     */
    @GetMapping("/earnings")
    @PreAuthorize("hasAuthority('VENDOR')")
    public ResponseEntity<APIResponse<RestaurantEarningsResponse>> getMyEarnings(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long minAmount,
            @RequestParam(required = false) Long maxAmount,
            @RequestParam(required = false) OrderStatus orderStatus,
            @RequestParam(defaultValue = "DAY") String groupBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Long restaurantId = principal.getRestaurantId();
        if (restaurantId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new APIResponse<>(HttpStatus.FORBIDDEN, null, "No restaurant associated with this account"));
        }

        EarningsFilterRequest filter = buildFilter(restaurantId.toString(), startDate, endDate,
                minAmount, maxAmount, orderStatus, groupBy, page, size);

        RestaurantEarningsResponse response = analyticsService.getRestaurantEarnings(filter);

        return ResponseEntity.ok(new APIResponse<>(HttpStatus.OK, response, "Earnings analytics fetched successfully"));
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // GET /admin/{restaurantId}/earnings  — ADMIN only (any restaurant by ID)
    // ─────────────────────────────────────────────────────────────────────────────

    /**
     * Admin endpoint to fetch earnings analytics for any restaurant by its ID.
     *
     * @param restaurantId Target restaurant ID
     * @param startDate    Start of date range (ISO date: yyyy-MM-dd)
     * @param endDate      End of date range (ISO date: yyyy-MM-dd)
     * @param minAmount    Minimum order total filter (in rupees)
     * @param maxAmount    Maximum order total filter (in rupees)
     * @param orderStatus  Filter by order status (default: DELIVERED)
     * @param groupBy      Chart grouping: DAY | WEEK | MONTH (default: DAY)
     * @param page         Page number (default: 0)
     * @param size         Page size (default: 20, max: 100)
     */
    @GetMapping("/{restaurantId}/earnings")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('RESTAURANT_PARTNER')")
    public ResponseEntity<APIResponse<RestaurantEarningsResponse>> getEarningsForAdmin(
            @PathVariable String restaurantId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long minAmount,
            @RequestParam(required = false) Long maxAmount,
            @RequestParam(required = false) OrderStatus orderStatus,
            @RequestParam(defaultValue = "DAY") String groupBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        EarningsFilterRequest filter = buildFilter(restaurantId, startDate, endDate,
                minAmount, maxAmount, orderStatus, groupBy, page, size);

        RestaurantEarningsResponse response = analyticsService.getRestaurantEarnings(filter);

        return ResponseEntity.ok(new APIResponse<>(HttpStatus.OK, response, "Earnings analytics fetched successfully"));
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // GET /commission  — VENDOR only (restaurantId resolved from JWT principal)
    // ─────────────────────────────────────────────────────────────────────────────

    /**
     * Returns the active commission configuration for the authenticated restaurant owner.
     * The restaurantId is resolved automatically from the JWT principal.
     * If no DB record exists, the platform default (20%) applies.
     */
    @GetMapping("/commission")
    @PreAuthorize("hasAuthority('VENDOR')")
    public ResponseEntity<APIResponse<RestaurantCommission>> getMyCommission(
            @AuthenticationPrincipal UserPrincipal principal) {

        Long restaurantId = principal.getRestaurantId();
        if (restaurantId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new APIResponse<>(HttpStatus.FORBIDDEN, null, "No restaurant associated with this account"));
        }

        return commissionRepository.findByRestaurantIdAndIsActiveTrue(restaurantId)
                .map(commission -> ResponseEntity.ok(
                        new APIResponse<>(HttpStatus.OK, commission, "Commission config found")))
                .orElse(ResponseEntity.ok(
                        new APIResponse<>(HttpStatus.OK, null,
                                "No custom commission found — platform default (20%) applies")));
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // GET /admin/{restaurantId}/commission  — ADMIN only (any restaurant)
    // ─────────────────────────────────────────────────────────────────────────────

    /**
     * Admin endpoint to view the commission configuration for any restaurant by ID.
     * If no DB record exists, the platform default (20%) applies.
     *
     * @param restaurantId Target restaurant ID
     */
    @GetMapping("/admin/{restaurantId}/commission")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<APIResponse<RestaurantCommission>> getCommissionForAdmin(
            @PathVariable Long restaurantId) {

        return commissionRepository.findByRestaurantIdAndIsActiveTrue(restaurantId)
                .map(commission -> ResponseEntity.ok(
                        new APIResponse<>(HttpStatus.OK, commission, "Commission config found")))
                .orElse(ResponseEntity.ok(
                        new APIResponse<>(HttpStatus.OK, null,
                                "No custom commission found — platform default (20%) applies")));
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // PUT /{restaurantId}/commission  — ADMIN only
    // ─────────────────────────────────────────────────────────────────────────────

    /**
     * Creates or updates the commission % for a restaurant.
     * Deactivates any existing active record before creating a new one.
     *
     * <p><b>Restricted to ADMIN only.</b> Restaurant owners cannot modify their commission rate.
     *
     * @param restaurantId         The restaurant's ID
     * @param newCommissionPercent The new commission percentage (e.g. 22.5)
     */
    @PutMapping("/{restaurantId}/commission")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<APIResponse<RestaurantCommission>> updateCommission(
            @PathVariable Long restaurantId,
            @RequestParam java.math.BigDecimal newCommissionPercent) {

        // Deactivate existing active record
        commissionRepository.findByRestaurantIdAndIsActiveTrue(restaurantId)
                .ifPresent(existing -> {
                    existing.setActive(false);
                    commissionRepository.save(existing);
                });

        // Create new active record
        RestaurantCommission commission = new RestaurantCommission();
        commission.setRestaurantId(restaurantId);
        commission.setCommissionPercentage(newCommissionPercent);
        commission.setEffectiveFrom(Instant.now());
        commission.setActive(true);

        RestaurantCommission saved = commissionRepository.save(commission);

        return ResponseEntity.ok(new APIResponse<>(
                HttpStatus.OK,
                saved,
                "Commission updated to " + newCommissionPercent + "% for restaurant " + restaurantId));
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // Private helper
    // ─────────────────────────────────────────────────────────────────────────────

    private EarningsFilterRequest buildFilter(String restaurantId, LocalDate startDate, LocalDate endDate,
                                              Long minAmount, Long maxAmount, OrderStatus orderStatus,
                                              String groupBy, int page, int size) {
        int effectiveSize = Math.min(size, 100);
        EarningsFilterRequest filter = new EarningsFilterRequest();
        filter.setRestaurantId(restaurantId);
        filter.setStartDate(startDate);
        filter.setEndDate(endDate);
        filter.setMinAmount(minAmount);
        filter.setMaxAmount(maxAmount);
        filter.setOrderStatus(orderStatus);
        filter.setGroupBy(groupBy);
        filter.setPage(page);
        filter.setSize(effectiveSize);
        return filter;
    }
}
