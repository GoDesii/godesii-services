package com.godesii.godesii_services.dto.analytics;

import com.godesii.godesii_services.entity.order.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Per-order earning breakdown shown in the paginated table on the analytics screen.
 * Shows exactly how much GoDesii deducted and what the restaurant receives for each order.
 */
public class OrderEarningDetail {

    private String orderId;
    private Instant orderDate;
    private OrderStatus orderStatus;

    // ─── Customer paid ──────────────────────────────────────────────────────────
    /** Raw food cost items ordered */
    private BigDecimal itemTotal;
    private BigDecimal deliveryFee;
    private BigDecimal packagingCharges;
    private BigDecimal platformFee;
    private BigDecimal discountAmount;
    /** Grand total paid by customer */
    private BigDecimal totalAmount;

    // ─── GST on food (restaurant remits to govt) ────────────────────────────────
    /** CGST = 2.5% of itemTotal (intra-state) */
    private BigDecimal cgst;
    /** SGST = 2.5% of itemTotal (intra-state) */
    private BigDecimal sgst;
    /** IGST = 5% of itemTotal (inter-state — typically 0 for same-state orders) */
    private BigDecimal igst;
    private BigDecimal totalFoodGst;

    // ─── Platform commission ─────────────────────────────────────────────────────
    /** commissionPercentage% of itemTotal deducted by GoDesii */
    private BigDecimal platformCommissionDeducted;

    // ─── Final payout ─────────────────────────────────────────────────────────────
    /**
     * What restaurant receives:
     * itemTotal - totalFoodGst - platformCommissionDeducted
     */
    private BigDecimal netPayout;

    // Getters & Setters
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public Instant getOrderDate() { return orderDate; }
    public void setOrderDate(Instant orderDate) { this.orderDate = orderDate; }

    public OrderStatus getOrderStatus() { return orderStatus; }
    public void setOrderStatus(OrderStatus orderStatus) { this.orderStatus = orderStatus; }

    public BigDecimal getItemTotal() { return itemTotal; }
    public void setItemTotal(BigDecimal itemTotal) { this.itemTotal = itemTotal; }

    public BigDecimal getDeliveryFee() { return deliveryFee; }
    public void setDeliveryFee(BigDecimal deliveryFee) { this.deliveryFee = deliveryFee; }

    public BigDecimal getPackagingCharges() { return packagingCharges; }
    public void setPackagingCharges(BigDecimal packagingCharges) { this.packagingCharges = packagingCharges; }

    public BigDecimal getPlatformFee() { return platformFee; }
    public void setPlatformFee(BigDecimal platformFee) { this.platformFee = platformFee; }

    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public BigDecimal getCgst() { return cgst; }
    public void setCgst(BigDecimal cgst) { this.cgst = cgst; }

    public BigDecimal getSgst() { return sgst; }
    public void setSgst(BigDecimal sgst) { this.sgst = sgst; }

    public BigDecimal getIgst() { return igst; }
    public void setIgst(BigDecimal igst) { this.igst = igst; }

    public BigDecimal getTotalFoodGst() { return totalFoodGst; }
    public void setTotalFoodGst(BigDecimal totalFoodGst) { this.totalFoodGst = totalFoodGst; }

    public BigDecimal getPlatformCommissionDeducted() { return platformCommissionDeducted; }
    public void setPlatformCommissionDeducted(BigDecimal platformCommissionDeducted) { this.platformCommissionDeducted = platformCommissionDeducted; }

    public BigDecimal getNetPayout() { return netPayout; }
    public void setNetPayout(BigDecimal netPayout) { this.netPayout = netPayout; }
}
