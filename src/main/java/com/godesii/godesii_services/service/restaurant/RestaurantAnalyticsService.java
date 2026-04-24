package com.godesii.godesii_services.service.restaurant;

import com.godesii.godesii_services.dto.analytics.*;
import com.godesii.godesii_services.entity.order.Order;
import com.godesii.godesii_services.entity.order.OrderStatus;
import com.godesii.godesii_services.entity.restaurant.RestaurantCommission;
import com.godesii.godesii_services.repository.order.OrderAnalyticsSpecification;
import com.godesii.godesii_services.repository.order.OrderRepository;
import com.godesii.godesii_services.repository.restaurant.RestaurantCommissionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Core service for Restaurant Earnings Analytics.
 *
 * GST Calculation follows Indian tax regime (food aggregator model):
 *
 *   Food GST (restaurant's liability to remit to government):
 *     - 5% total = CGST 2.5% + SGST 2.5% (intra-state)
 *     - 5% as IGST (inter-state — rare for food delivery)
 *     - Applied on: itemTotal
 *
 *   Platform GST (GoDesii's liability):
 *     - 18% total = CGST 9% + SGST 9% (intra-state)
 *     - Applied on: platformFee + deliveryFee
 *
 *   Restaurant Net Payout:
 *     itemTotal - totalFoodGst - platformCommission
 *
 *   GoDesii Net Earnings (per order):
 *     platformCommission + platformFee + deliveryFee + packagingCharges - platformGst
 */
@Service
public class RestaurantAnalyticsService {

    private static final Logger log = LoggerFactory.getLogger(RestaurantAnalyticsService.class);
    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
    private static final int SCALE = 2;

    private final OrderRepository orderRepository;
    private final RestaurantCommissionRepository commissionRepository;

    @Value("${godesii.tax.gst.food-percentage:5.0}")
    private BigDecimal foodGstPercentage;

    @Value("${godesii.tax.gst.platform-percentage:18.0}")
    private BigDecimal platformGstPercentage;

    @Value("${godesii.commission.default-percentage:20.0}")
    private BigDecimal defaultCommissionPercentage;

    public RestaurantAnalyticsService(OrderRepository orderRepository,
                                      RestaurantCommissionRepository commissionRepository) {
        this.orderRepository = orderRepository;
        this.commissionRepository = commissionRepository;
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // Public API
    // ─────────────────────────────────────────────────────────────────────────────

    /**
     * Main entry point for the analytics API.
     * Returns summary KPIs, chart data, and a paginated order table.
     */
    public RestaurantEarningsResponse getRestaurantEarnings(EarningsFilterRequest filter) {
        log.info("Fetching earnings analytics for restaurant: {}, period: {} to {}",
                filter.getRestaurantId(), filter.getStartDate(), filter.getEndDate());

        // Resolve commission rate for this restaurant (DB record or config fallback)
        BigDecimal commissionPct = resolveCommissionPercentage(filter.getRestaurantId());

        // Build the dynamic filter specification
        Specification<Order> spec = OrderAnalyticsSpecification.buildFilter(
                filter.getRestaurantId(),
                filter.getStartDate(),
                filter.getEndDate(),
                filter.getMinAmount(),
                filter.getMaxAmount(),
                filter.getOrderStatus()
        );

        // Fetch ALL matching orders for summary + chart (no pagination yet)
        List<Order> allOrders = orderRepository.findAll(spec);

        // Fetch paginated subset for the table
        Page<Order> pagedOrders = orderRepository.findAll(spec,
                PageRequest.of(filter.getPage(), filter.getSize(),
                        Sort.by(Sort.Direction.DESC, "orderDate")));

        // Build response sections
        EarningsSummary summary = buildSummary(
                filter.getRestaurantId(), allOrders, commissionPct,
                filter.getStartDate(), filter.getEndDate());

        List<EarningsChartPoint> chartData = buildChartData(allOrders, commissionPct, filter.getGroupBy());

        List<OrderEarningDetail> orderDetails = pagedOrders.getContent().stream()
                .map(order -> buildOrderDetail(order, commissionPct))
                .collect(Collectors.toList());

        // Assemble response
        RestaurantEarningsResponse response = new RestaurantEarningsResponse();
        response.setSummary(summary);
        response.setChartData(chartData);
        response.setOrders(orderDetails);
        response.setCurrentPage(pagedOrders.getNumber());
        response.setTotalPages(pagedOrders.getTotalPages());
        response.setTotalElements(pagedOrders.getTotalElements());
        response.setPageSize(pagedOrders.getSize());

        return response;
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // Summary Builder
    // ─────────────────────────────────────────────────────────────────────────────

    private EarningsSummary buildSummary(String restaurantId, List<Order> orders,
                                          BigDecimal commissionPct,
                                          java.time.LocalDate startDate,
                                          java.time.LocalDate endDate) {
        EarningsSummary summary = new EarningsSummary();
        summary.setRestaurantId(restaurantId);
        summary.setTotalOrders(orders.size());
        summary.setCommissionPercentage(commissionPct);

        // Format period label
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM yyyy");
        String from = startDate != null ? startDate.format(fmt) : "All time";
        String to = endDate != null ? endDate.format(fmt) : "Today";
        summary.setPeriod(from + " – " + to);

        // Accumulate totals
        BigDecimal grossRevenue = BigDecimal.ZERO;
        BigDecimal totalCgst = BigDecimal.ZERO;
        BigDecimal totalSgst = BigDecimal.ZERO;
        BigDecimal totalIgst = BigDecimal.ZERO;
        BigDecimal totalFoodGst = BigDecimal.ZERO;
        BigDecimal totalPlatformGst = BigDecimal.ZERO;
        BigDecimal totalCommission = BigDecimal.ZERO;
        BigDecimal totalPlatformFees = BigDecimal.ZERO;  // deliveryFee + platformFee + packaging

        for (Order order : orders) {
            BigDecimal itemTotal = safeDecimal(order.getItemTotal());
            BigDecimal platFee = safeDecimal(order.getPlatformFee());
            BigDecimal delFee = safeDecimal(order.getDeliveryFee());
            BigDecimal packFee = safeDecimal(order.getPackagingCharges());

            // GST calculations
            GstBreakdown gst = calculateFoodGst(itemTotal);
            BigDecimal platGst = calculatePlatformGst(platFee.add(delFee));

            // Commission
            BigDecimal commission = itemTotal.multiply(commissionPct)
                    .divide(HUNDRED, SCALE, RoundingMode.HALF_UP);

            grossRevenue = grossRevenue.add(itemTotal);
            totalCgst = totalCgst.add(gst.cgst);
            totalSgst = totalSgst.add(gst.sgst);
            totalIgst = totalIgst.add(gst.igst);
            totalFoodGst = totalFoodGst.add(gst.total);
            totalPlatformGst = totalPlatformGst.add(platGst);
            totalCommission = totalCommission.add(commission);
            totalPlatformFees = totalPlatformFees.add(platFee).add(delFee).add(packFee);
        }

        // Net restaurant payout = grossRevenue - commission (GST is collected extra from customer and remitted to govt)
        BigDecimal netEarnings = grossRevenue.subtract(totalCommission);

        // GoDesii earnings = commission + platform fees - platform GST liability
        BigDecimal platformEarnings = totalCommission.add(totalPlatformFees).subtract(totalPlatformGst);

        // Average order value
        BigDecimal avgOrderValue = orders.isEmpty() ? BigDecimal.ZERO
                : grossRevenue.divide(BigDecimal.valueOf(orders.size()), SCALE, RoundingMode.HALF_UP);

        summary.setGrossRevenue(grossRevenue);
        summary.setTotalCgst(totalCgst);
        summary.setTotalSgst(totalSgst);
        summary.setTotalIgst(totalIgst);
        summary.setTotalFoodGst(totalFoodGst);
        summary.setTotalPlatformGst(totalPlatformGst);
        summary.setTotalPlatformCommissionDeducted(totalCommission);
        summary.setNetEarningsToRestaurant(netEarnings);
        summary.setPlatformEarnings(platformEarnings);
        summary.setAvgOrderValue(avgOrderValue);

        return summary;
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // Chart Data Builder
    // ─────────────────────────────────────────────────────────────────────────────

    private List<EarningsChartPoint> buildChartData(List<Order> orders,
                                                     BigDecimal commissionPct,
                                                     String groupBy) {
        // Group orders by time bucket key
        Map<String, List<Order>> grouped = orders.stream()
                .filter(o -> o.getOrderDate() != null)
                .collect(Collectors.groupingBy(o -> buildChartLabel(o, groupBy)));

        // Sort keys chronologically and build chart points
        return grouped.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> buildChartPoint(entry.getKey(), entry.getValue(), commissionPct))
                .collect(Collectors.toList());
    }

    private String buildChartLabel(Order order, String groupBy) {
        java.time.LocalDate date = order.getOrderDate().atZone(ZoneOffset.UTC).toLocalDate();
        return switch (groupBy == null ? "DAY" : groupBy.toUpperCase()) {
            case "MONTH" -> date.format(DateTimeFormatter.ofPattern("yyyy-MM"));          // "2024-01"
            case "WEEK" -> date.getYear() + "-W" + String.format("%02d",
                    date.get(java.time.temporal.WeekFields.ISO.weekOfWeekBasedYear()));   // "2024-W03"
            default -> date.format(DateTimeFormatter.ISO_LOCAL_DATE);                    // "2024-01-15"
        };
    }

    private EarningsChartPoint buildChartPoint(String label, List<Order> orders,
                                                BigDecimal commissionPct) {
        BigDecimal grossRevenue = BigDecimal.ZERO;
        BigDecimal totalGst = BigDecimal.ZERO;
        BigDecimal totalCommission = BigDecimal.ZERO;

        for (Order order : orders) {
            BigDecimal itemTotal = safeDecimal(order.getItemTotal());
            GstBreakdown gst = calculateFoodGst(itemTotal);
            BigDecimal commission = itemTotal.multiply(commissionPct)
                    .divide(HUNDRED, SCALE, RoundingMode.HALF_UP);

            grossRevenue = grossRevenue.add(itemTotal);
            totalGst = totalGst.add(gst.total);
            totalCommission = totalCommission.add(commission);
        }

        BigDecimal netEarnings = grossRevenue.subtract(totalCommission);

        EarningsChartPoint point = new EarningsChartPoint();
        point.setLabel(label);
        point.setGrossRevenue(grossRevenue);
        point.setNetEarnings(netEarnings);
        point.setGstDeducted(totalGst);
        point.setCommissionDeducted(totalCommission);
        point.setOrderCount(orders.size());
        return point;
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // Per-Order Detail Builder
    // ─────────────────────────────────────────────────────────────────────────────

    /**
     * Builds the per-order detail row for the paginated table.
     * Shows full breakdown: what customer paid, what GST is owed, commission taken, final payout.
     */
    public OrderEarningDetail buildOrderDetail(Order order, BigDecimal commissionPct) {
        BigDecimal itemTotal = safeDecimal(order.getItemTotal());
        BigDecimal deliveryFee = safeDecimal(order.getDeliveryFee());
        BigDecimal packagingCharges = safeDecimal(order.getPackagingCharges());
        BigDecimal platformFee = safeDecimal(order.getPlatformFee());
        BigDecimal discountAmount = safeDecimal(order.getDiscountAmount());
        BigDecimal totalAmount = safeDecimal(order.getTotalAmount());

        // Food GST (restaurant's liability)
        GstBreakdown gst = calculateFoodGst(itemTotal);

        // Commission deducted by GoDesii
        BigDecimal commission = itemTotal.multiply(commissionPct)
                .divide(HUNDRED, SCALE, RoundingMode.HALF_UP);

        // Net restaurant payout
        BigDecimal netPayout = itemTotal.subtract(commission);

        OrderEarningDetail detail = new OrderEarningDetail();
        detail.setOrderId(order.getOrderId());
        detail.setOrderDate(order.getOrderDate());
        detail.setOrderStatus(order.getOrderStatus());
        detail.setItemTotal(itemTotal);
        detail.setDeliveryFee(deliveryFee);
        detail.setPackagingCharges(packagingCharges);
        detail.setPlatformFee(platformFee);
        detail.setDiscountAmount(discountAmount);
        detail.setTotalAmount(totalAmount);
        detail.setCgst(gst.cgst);
        detail.setSgst(gst.sgst);
        detail.setIgst(gst.igst);
        detail.setTotalFoodGst(gst.total);
        detail.setPlatformCommissionDeducted(commission);
        detail.setNetPayout(netPayout);

        return detail;
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // GST Calculation Methods — Indian Tax Regime
    // ─────────────────────────────────────────────────────────────────────────────

    /**
     * Calculates food GST breakdown on the item total.
     *
     * Indian GST on food (restaurant's liability):
     *   Intra-state: CGST = foodGst%/2, SGST = foodGst%/2, IGST = 0
     *   Inter-state: IGST = foodGst%, CGST = 0, SGST = 0
     *
     * We default to intra-state (same state supply) as most food delivery transactions.
     * Inter-state flag can be extended in future if restaurant state != customer state.
     */
    private GstBreakdown calculateFoodGst(BigDecimal itemTotal) {
        // Intra-state: split food GST equally between CGST and SGST
        BigDecimal halfGstRate = foodGstPercentage.divide(BigDecimal.valueOf(2), 4, RoundingMode.HALF_UP);

        BigDecimal cgst = itemTotal.multiply(halfGstRate)
                .divide(HUNDRED, SCALE, RoundingMode.HALF_UP);
        BigDecimal sgst = itemTotal.multiply(halfGstRate)
                .divide(HUNDRED, SCALE, RoundingMode.HALF_UP);
        BigDecimal igst = BigDecimal.ZERO; // Default intra-state

        return new GstBreakdown(cgst, sgst, igst);
    }

    /**
     * Calculates 18% GST on platform services (GoDesii's liability).
     * Applied on: platformFee + deliveryFee
     */
    private BigDecimal calculatePlatformGst(BigDecimal platformServiceAmount) {
        return platformServiceAmount.multiply(platformGstPercentage)
                .divide(HUNDRED, SCALE, RoundingMode.HALF_UP);
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // Commission Resolution
    // ─────────────────────────────────────────────────────────────────────────────

    /**
     * Resolves the applicable commission % for a restaurant.
     * Priority: DB record (RestaurantCommission) → application.yaml default
     */
    private BigDecimal resolveCommissionPercentage(String restaurantId) {
        if (restaurantId == null) {
            return defaultCommissionPercentage;
        }
        try {
            Long restaurantIdLong = Long.parseLong(restaurantId);
            return commissionRepository.findByRestaurantIdAndIsActiveTrue(restaurantIdLong)
                    .map(RestaurantCommission::getCommissionPercentage)
                    .orElseGet(() -> {
                        log.warn("No active commission record for restaurant {}. Using default: {}%",
                                restaurantId, defaultCommissionPercentage);
                        return defaultCommissionPercentage;
                    });
        } catch (NumberFormatException e) {
            log.warn("Restaurant ID {} is not a valid Long, using default commission.", restaurantId);
            return defaultCommissionPercentage;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────────

    private BigDecimal safeDecimal(Long value) {
        return value == null ? BigDecimal.ZERO : BigDecimal.valueOf(value);
    }

    /**
     * Internal value object for GST split.
     */
    private static class GstBreakdown {
        final BigDecimal cgst;
        final BigDecimal sgst;
        final BigDecimal igst;
        final BigDecimal total;

        GstBreakdown(BigDecimal cgst, BigDecimal sgst, BigDecimal igst) {
            this.cgst = cgst;
            this.sgst = sgst;
            this.igst = igst;
            this.total = cgst.add(sgst).add(igst);
        }
    }
}
