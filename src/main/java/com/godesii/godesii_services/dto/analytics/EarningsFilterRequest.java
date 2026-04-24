package com.godesii.godesii_services.dto.analytics;

import com.godesii.godesii_services.entity.order.OrderStatus;

import java.time.LocalDate;

/**
 * Query parameter holder for restaurant analytics filter.
 * All fields are optional — nulls are treated as "no filter".
 */
public class EarningsFilterRequest {

    /** Target restaurant ID (required for restaurant-facing endpoint) */
    private String restaurantId;

    /** Start of date range — inclusive */
    private LocalDate startDate;

    /** End of date range — inclusive */
    private LocalDate endDate;

    /** Minimum order value filter (in paise/lowest currency unit) */
    private Long minAmount;

    /** Maximum order value filter */
    private Long maxAmount;

    /** Order status to include in analytics. Defaults to DELIVERED in service. */
    private OrderStatus orderStatus;

    /**
     * Aggregation granularity for chart data.
     * Accepted: "DAY", "WEEK", "MONTH"
     * Defaults to "DAY" if not specified.
     */
    private String groupBy = "DAY";

    /** Page number (0-indexed). Default: 0 */
    private int page = 0;

    /** Page size. Default: 20 */
    private int size = 20;

    public String getRestaurantId() { return restaurantId; }
    public void setRestaurantId(String restaurantId) { this.restaurantId = restaurantId; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public Long getMinAmount() { return minAmount; }
    public void setMinAmount(Long minAmount) { this.minAmount = minAmount; }

    public Long getMaxAmount() { return maxAmount; }
    public void setMaxAmount(Long maxAmount) { this.maxAmount = maxAmount; }

    public OrderStatus getOrderStatus() { return orderStatus; }
    public void setOrderStatus(OrderStatus orderStatus) { this.orderStatus = orderStatus; }

    public String getGroupBy() { return groupBy; }
    public void setGroupBy(String groupBy) { this.groupBy = groupBy; }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
}
