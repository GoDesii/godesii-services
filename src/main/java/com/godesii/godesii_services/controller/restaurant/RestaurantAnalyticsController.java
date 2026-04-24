package com.godesii.godesii_services.controller.restaurant;

import com.godesii.godesii_services.common.APIResponse;
import com.godesii.godesii_services.dto.analytics.EarningsFilterRequest;
import com.godesii.godesii_services.dto.analytics.RestaurantEarningsResponse;
import com.godesii.godesii_services.entity.order.OrderStatus;
import com.godesii.godesii_services.entity.restaurant.RestaurantCommission;
import com.godesii.godesii_services.repository.restaurant.RestaurantCommissionRepository;
import com.godesii.godesii_services.service.restaurant.RestaurantAnalyticsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;

/**
 * REST Controller for Restaurant Earnings Analytics.
 *
 * Base path: /api/v1/analytics/restaurant
 *
 * Endpoints:
 *   GET  /{restaurantId}/earnings    — Full earnings analytics with flexible filters
 *   GET  /{restaurantId}/commission  — View current commission config for a restaurant
 *   PUT  /{restaurantId}/commission  — Update commission % (admin/platform use only)
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
    // GET /{restaurantId}/earnings
    // ─────────────────────────────────────────────────────────────────────────────

    /**
     * Fetches earnings analytics for a restaurant with flexible filters.
     *
     * <p>Shows:
     * <ul>
     *   <li>Aggregated KPI summary (gross revenue, GST breakdown, commission, net payout)</li>
     *   <li>Time-series chart data grouped by DAY / WEEK / MONTH</li>
     *   <li>Paginated per-order detail table</li>
     * </ul>
     *
     * <p>All filter params are optional. Default behavior:
     * <ul>
     *   <li>orderStatus defaults to DELIVERED (only count completed orders as revenue)</li>
     *   <li>groupBy defaults to DAY</li>
     *   <li>Pagination: page=0, size=20</li>
     * </ul>
     *
     * @param restaurantId  The restaurant's ID
     * @param startDate     Start of date range (ISO date: yyyy-MM-dd)
     * @param endDate       End of date range (ISO date: yyyy-MM-dd)
     * @param minAmount     Minimum order total filter (in rupees)
     * @param maxAmount     Maximum order total filter (in rupees)
     * @param orderStatus   Filter by order status (default: DELIVERED)
     * @param groupBy       Chart grouping: DAY | WEEK | MONTH (default: DAY)
     * @param page          Page number (default: 0)
     * @param size          Page size (default: 20, max: 100)
     */
    @GetMapping("/{restaurantId}/earnings")
    public ResponseEntity<APIResponse<RestaurantEarningsResponse>> getEarnings(
            @PathVariable String restaurantId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long minAmount,
            @RequestParam(required = false) Long maxAmount,
            @RequestParam(required = false) OrderStatus orderStatus,
            @RequestParam(defaultValue = "DAY") String groupBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        // Cap page size to prevent abuse
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

        RestaurantEarningsResponse response = analyticsService.getRestaurantEarnings(filter);

        return ResponseEntity.ok(new APIResponse<>(
                HttpStatus.OK,
                response,
                "Earnings analytics fetched successfully"));
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // GET /{restaurantId}/commission
    // ─────────────────────────────────────────────────────────────────────────────

    /**
     * Returns the current active commission configuration for a restaurant.
     * If no DB record exists, the application.yaml default applies (visible in response message).
     */
    @GetMapping("/{restaurantId}/commission")
    public ResponseEntity<APIResponse<RestaurantCommission>> getCommission(
            @PathVariable Long restaurantId) {

        return commissionRepository.findByRestaurantIdAndIsActiveTrue(restaurantId)
                .map(commission -> ResponseEntity.ok(
                        new APIResponse<>(HttpStatus.OK, commission, "Commission config found")))
                .orElse(ResponseEntity.ok(
                        new APIResponse<>(HttpStatus.OK, null,
                                "No custom commission found — platform default (20%) applies")));
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // PUT /{restaurantId}/commission
    // ─────────────────────────────────────────────────────────────────────────────

    /**
     * Creates or updates the commission % for a restaurant.
     * Deactivates any existing active record before creating the new one.
     *
     * <p><b>Note:</b> This endpoint should be secured to platform admin roles only.
     *
     * @param restaurantId         The restaurant's ID
     * @param newCommissionPercent The new commission percentage (e.g. 22.5)
     */
    @PutMapping("/{restaurantId}/commission")
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
}
