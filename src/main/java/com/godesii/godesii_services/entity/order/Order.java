package com.godesii.godesii_services.entity.order;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import com.godesii.godesii_services.entity.payment.PaymentMethod;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    private String orderId;
    private String username;
    private String restaurantId;
    private OrderStatus orderStatus;
    private Long totalAmount;
    private Instant orderDate;
    private OrderAddress orderAddress;
    private List<OrderItem> orderItems;

    // Payment fields
    private String paymentId;
    private PaymentMethod paymentMethod; // Changed to enum
    private String paymentStatus;
    private String razorpayOrderId; // Razorpay-specific order ID

    // Order tracking fields
    private Instant confirmedAt;
    private Instant cancelledAt;
    private String cancellationReason;

    // Delivery tracking
    private String deliveryNotes; // Special instructions from customer
    private Instant estimatedDeliveryTime; // Expected delivery time
    private Integer preparationTime; // In minutes, set by restaurant

    // Delivery partner fields
    private String deliveryPartnerId;
    private String deliveryPartnerName;
    private String deliveryPartnerPhone;
    private Instant assignedToDeliveryAt;
    private Instant pickedUpAt;

    // Cancellation tracking
    private String cancelledBy;
    private CancellationReason cancellationCategory;
    private Boolean refundIssued;

    // Refund fields
    private String refundId;
    private Instant refundedAt;

    @Id
    @UuidGenerator
    @Column(name = "order_id")
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    @Column(name = "user_name")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Column(name = "restaurant_id")
    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    @Column(name = "order_status")
    @Enumerated(EnumType.STRING)
    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    @Column(name = "payment_id")
    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    @Column(name = "payment_method")
    @Enumerated(EnumType.STRING)
    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    @Column(name = "payment_status")
    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    @Column(name = "confirmed_at")
    @Temporal(TemporalType.TIMESTAMP)
    public Instant getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(Instant confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    @Column(name = "cancelled_at")
    @Temporal(TemporalType.TIMESTAMP)
    public Instant getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(Instant cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    @Column(name = "cancellation_reason")
    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    @Column(name = "refund_id")
    public String getRefundId() {
        return refundId;
    }

    public void setRefundId(String refundId) {
        this.refundId = refundId;
    }

    @Column(name = "refunded_at")
    @Temporal(TemporalType.TIMESTAMP)
    public Instant getRefundedAt() {
        return refundedAt;
    }

    public void setRefundedAt(Instant refundedAt) {
        this.refundedAt = refundedAt;
    }

    @Column(name = "razorpay_order_id")
    public String getRazorpayOrderId() {
        return razorpayOrderId;
    }

    public void setRazorpayOrderId(String razorpayOrderId) {
        this.razorpayOrderId = razorpayOrderId;
    }

    @Column(name = "delivery_notes", length = 500)
    public String getDeliveryNotes() {
        return deliveryNotes;
    }

    public void setDeliveryNotes(String deliveryNotes) {
        this.deliveryNotes = deliveryNotes;
    }

    @Column(name = "estimated_delivery_time")
    @Temporal(TemporalType.TIMESTAMP)
    public Instant getEstimatedDeliveryTime() {
        return estimatedDeliveryTime;
    }

    public void setEstimatedDeliveryTime(Instant estimatedDeliveryTime) {
        this.estimatedDeliveryTime = estimatedDeliveryTime;
    }

    @Column(name = "preparation_time")
    public Integer getPreparationTime() {
        return preparationTime;
    }

    public void setPreparationTime(Integer preparationTime) {
        this.preparationTime = preparationTime;
    }

    @Column(name = "delivery_partner_id")
    public String getDeliveryPartnerId() {
        return deliveryPartnerId;
    }

    public void setDeliveryPartnerId(String deliveryPartnerId) {
        this.deliveryPartnerId = deliveryPartnerId;
    }

    @Column(name = "delivery_partner_name", length = 100)
    public String getDeliveryPartnerName() {
        return deliveryPartnerName;
    }

    public void setDeliveryPartnerName(String deliveryPartnerName) {
        this.deliveryPartnerName = deliveryPartnerName;
    }

    @Column(name = "delivery_partner_phone", length = 15)
    public String getDeliveryPartnerPhone() {
        return deliveryPartnerPhone;
    }

    public void setDeliveryPartnerPhone(String deliveryPartnerPhone) {
        this.deliveryPartnerPhone = deliveryPartnerPhone;
    }

    @Column(name = "assigned_to_delivery_at")
    @Temporal(TemporalType.TIMESTAMP)
    public Instant getAssignedToDeliveryAt() {
        return assignedToDeliveryAt;
    }

    public void setAssignedToDeliveryAt(Instant assignedToDeliveryAt) {
        this.assignedToDeliveryAt = assignedToDeliveryAt;
    }

    @Column(name = "picked_up_at")
    @Temporal(TemporalType.TIMESTAMP)
    public Instant getPickedUpAt() {
        return pickedUpAt;
    }

    public void setPickedUpAt(Instant pickedUpAt) {
        this.pickedUpAt = pickedUpAt;
    }

    @Column(name = "cancelled_by", length = 20)
    public String getCancelledBy() {
        return cancelledBy;
    }

    public void setCancelledBy(String cancelledBy) {
        this.cancelledBy = cancelledBy;
    }

    @Column(name = "cancellation_category")
    @Enumerated(EnumType.STRING)
    public CancellationReason getCancellationCategory() {
        return cancellationCategory;
    }

    public void setCancellationCategory(CancellationReason cancellationCategory) {
        this.cancellationCategory = cancellationCategory;
    }

    @Column(name = "refund_issued")
    public Boolean getRefundIssued() {
        return refundIssued;
    }

    public void setRefundIssued(Boolean refundIssued) {
        this.refundIssued = refundIssued;
    }

    @Column(name = "total_amount")
    public Long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Long totalAmount) {
        this.totalAmount = totalAmount;
    }

    @Column(name = "order_date")
    @Temporal(TemporalType.TIMESTAMP)
    public Instant getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Instant orderDate) {
        this.orderDate = orderDate;
    }

    @Embedded
    public OrderAddress getOrderAddress() {
        return orderAddress;
    }

    public void setOrderAddress(OrderAddress orderAddress) {
        this.orderAddress = orderAddress;
    }

    @JoinTable(name = "order_order_item", joinColumns = @JoinColumn(name = "order_id"), inverseJoinColumns = @JoinColumn(name = "order_item_id"))
    @ManyToMany(fetch = FetchType.LAZY)
    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }
}
