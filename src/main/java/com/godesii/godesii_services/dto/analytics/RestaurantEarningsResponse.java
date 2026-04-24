package com.godesii.godesii_services.dto.analytics;

import java.util.List;

/**
 * Top-level response object for the Restaurant Earnings Analytics API.
 *
 * Structure mirrors the Zomato/Swiggy restaurant partner earnings dashboard:
 *   1. summary   — KPI cards at the top (total orders, gross revenue, net payout)
 *   2. chartData — time-series data points for the earnings trend chart
 *   3. orders    — paginated per-order breakdown table
 */
public class RestaurantEarningsResponse {

    /** Aggregated KPIs for the selected period */
    private EarningsSummary summary;

    /** Chart data points grouped by DAY / WEEK / MONTH */
    private List<EarningsChartPoint> chartData;

    /** Paginated per-order earning details */
    private List<OrderEarningDetail> orders;

    // Pagination metadata
    private int currentPage;
    private int totalPages;
    private long totalElements;
    private int pageSize;

    public EarningsSummary getSummary() { return summary; }
    public void setSummary(EarningsSummary summary) { this.summary = summary; }

    public List<EarningsChartPoint> getChartData() { return chartData; }
    public void setChartData(List<EarningsChartPoint> chartData) { this.chartData = chartData; }

    public List<OrderEarningDetail> getOrders() { return orders; }
    public void setOrders(List<OrderEarningDetail> orders) { this.orders = orders; }

    public int getCurrentPage() { return currentPage; }
    public void setCurrentPage(int currentPage) { this.currentPage = currentPage; }

    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }

    public long getTotalElements() { return totalElements; }
    public void setTotalElements(long totalElements) { this.totalElements = totalElements; }

    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }
}
