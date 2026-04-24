package com.godesii.godesii_services.dto.analytics;

import java.math.BigDecimal;

/**
 * Single data point for an earnings trend chart.
 * The frontend can use these to render bar/line charts grouped by DAY, WEEK, or MONTH.
 */
public class EarningsChartPoint {

    /**
     * Human-readable label for X-axis.
     * DAY   → "2024-01-15"
     * WEEK  → "Week 3, Jan 2024"
     * MONTH → "January 2024"
     */
    private String label;

    /** Total food cost across all orders in this time bucket */
    private BigDecimal grossRevenue;

    /** Restaurant's net payout after GST and commission deduction */
    private BigDecimal netEarnings;

    /** Total food GST deducted in this bucket */
    private BigDecimal gstDeducted;

    /** Total platform commission deducted in this bucket */
    private BigDecimal commissionDeducted;

    /** Number of orders in this time bucket */
    private int orderCount;

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public BigDecimal getGrossRevenue() { return grossRevenue; }
    public void setGrossRevenue(BigDecimal grossRevenue) { this.grossRevenue = grossRevenue; }

    public BigDecimal getNetEarnings() { return netEarnings; }
    public void setNetEarnings(BigDecimal netEarnings) { this.netEarnings = netEarnings; }

    public BigDecimal getGstDeducted() { return gstDeducted; }
    public void setGstDeducted(BigDecimal gstDeducted) { this.gstDeducted = gstDeducted; }

    public BigDecimal getCommissionDeducted() { return commissionDeducted; }
    public void setCommissionDeducted(BigDecimal commissionDeducted) { this.commissionDeducted = commissionDeducted; }

    public int getOrderCount() { return orderCount; }
    public void setOrderCount(int orderCount) { this.orderCount = orderCount; }
}
