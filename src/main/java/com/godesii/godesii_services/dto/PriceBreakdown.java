package com.godesii.godesii_services.dto;

public class PriceBreakdown {

    private Long itemTotal;
    private Long packagingCharges;
    private Long gst;
    private Long platformFee;
    private Long deliveryFee;
    private Long discount;
    private Long totalAmount;

    public Long getItemTotal() {
        return itemTotal;
    }

    public void setItemTotal(Long itemTotal) {
        this.itemTotal = itemTotal;
    }

    public Long getPackagingCharges() {
        return packagingCharges;
    }

    public void setPackagingCharges(Long packagingCharges) {
        this.packagingCharges = packagingCharges;
    }

    public Long getGst() {
        return gst;
    }

    public void setGst(Long gst) {
        this.gst = gst;
    }

    public Long getPlatformFee() {
        return platformFee;
    }

    public void setPlatformFee(Long platformFee) {
        this.platformFee = platformFee;
    }

    public Long getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(Long deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public Long getDiscount() {
        return discount;
    }

    public void setDiscount(Long discount) {
        this.discount = discount;
    }

    public Long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Long totalAmount) {
        this.totalAmount = totalAmount;
    }
}
