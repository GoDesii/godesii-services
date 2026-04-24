package com.godesii.godesii_services.dto.analytics;

import java.math.BigDecimal;

/**
 * Aggregated earnings summary for a restaurant over a selected period.
 *
 * Mirrors the "Earnings Overview" card shown in Zomato/Swiggy restaurant partner dashboards.
 *
 * GST breakdown follows Indian tax regime:
 *   - Food GST: 5% (CGST 2.5% + SGST 2.5%) — restaurant's liability
 *   - Platform GST: 18% (CGST 9% + SGST 9%) — GoDesii's liability on platform/delivery fees
 */
public class EarningsSummary {

    private String restaurantId;
    private String restaurantName;

    /** Human-readable period label e.g. "01 Apr 2024 – 30 Apr 2024" */
    private String period;

    private int totalOrders;

    // ─── Revenue ──────────────────────────────────────────────────────────────
    /** Sum of all itemTotals (food cost before any deduction) */
    private BigDecimal grossRevenue;

    /** Average order value across the period */
    private BigDecimal avgOrderValue;

    // ─── GST Breakdown (Restaurant's liability — 5% on food) ──────────────────
    /** Total CGST collected from customers (2.5% of itemTotal) */
    private BigDecimal totalCgst;

    /** Total SGST collected from customers (2.5% of itemTotal) */
    private BigDecimal totalSgst;

    /**
     * IGST applies only on inter-state supply.
     * For most orders this will be zero (intra-state = CGST+SGST applies instead).
     */
    private BigDecimal totalIgst;

    /** Total food GST = CGST + SGST + IGST */
    private BigDecimal totalFoodGst;

    // ─── Platform GST (GoDesii's liability — 18% on platform/delivery fees) ────
    /** Total 18% GST collected by GoDesii on platform fee + delivery fee */
    private BigDecimal totalPlatformGst;

    // ─── Commission ────────────────────────────────────────────────────────────
    /** Commission percentage applied (from RestaurantCommission or config fallback) */
    private BigDecimal commissionPercentage;

    /** Total commission deducted by GoDesii from restaurant's item total */
    private BigDecimal totalPlatformCommissionDeducted;

    // ─── Final Earnings ─────────────────────────────────────────────────────────
    /**
     * What the restaurant actually receives after deductions:
     * grossRevenue - totalFoodGst - totalPlatformCommissionDeducted
     */
    private BigDecimal netEarningsToRestaurant;

    /**
     * GoDesii's total earnings from this restaurant:
     * totalPlatformCommissionDeducted + platformFees + deliveryFees - totalPlatformGst
     */
    private BigDecimal platformEarnings;

    // Getters & Setters
    public String getRestaurantId() { return restaurantId; }
    public void setRestaurantId(String restaurantId) { this.restaurantId = restaurantId; }

    public String getRestaurantName() { return restaurantName; }
    public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }

    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }

    public int getTotalOrders() { return totalOrders; }
    public void setTotalOrders(int totalOrders) { this.totalOrders = totalOrders; }

    public BigDecimal getGrossRevenue() { return grossRevenue; }
    public void setGrossRevenue(BigDecimal grossRevenue) { this.grossRevenue = grossRevenue; }

    public BigDecimal getAvgOrderValue() { return avgOrderValue; }
    public void setAvgOrderValue(BigDecimal avgOrderValue) { this.avgOrderValue = avgOrderValue; }

    public BigDecimal getTotalCgst() { return totalCgst; }
    public void setTotalCgst(BigDecimal totalCgst) { this.totalCgst = totalCgst; }

    public BigDecimal getTotalSgst() { return totalSgst; }
    public void setTotalSgst(BigDecimal totalSgst) { this.totalSgst = totalSgst; }

    public BigDecimal getTotalIgst() { return totalIgst; }
    public void setTotalIgst(BigDecimal totalIgst) { this.totalIgst = totalIgst; }

    public BigDecimal getTotalFoodGst() { return totalFoodGst; }
    public void setTotalFoodGst(BigDecimal totalFoodGst) { this.totalFoodGst = totalFoodGst; }

    public BigDecimal getTotalPlatformGst() { return totalPlatformGst; }
    public void setTotalPlatformGst(BigDecimal totalPlatformGst) { this.totalPlatformGst = totalPlatformGst; }

    public BigDecimal getCommissionPercentage() { return commissionPercentage; }
    public void setCommissionPercentage(BigDecimal commissionPercentage) { this.commissionPercentage = commissionPercentage; }

    public BigDecimal getTotalPlatformCommissionDeducted() { return totalPlatformCommissionDeducted; }
    public void setTotalPlatformCommissionDeducted(BigDecimal totalPlatformCommissionDeducted) { this.totalPlatformCommissionDeducted = totalPlatformCommissionDeducted; }

    public BigDecimal getNetEarningsToRestaurant() { return netEarningsToRestaurant; }
    public void setNetEarningsToRestaurant(BigDecimal netEarningsToRestaurant) { this.netEarningsToRestaurant = netEarningsToRestaurant; }

    public BigDecimal getPlatformEarnings() { return platformEarnings; }
    public void setPlatformEarnings(BigDecimal platformEarnings) { this.platformEarnings = platformEarnings; }
}
