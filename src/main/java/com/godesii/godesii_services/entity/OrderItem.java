package com.godesii.godesii_services.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "order_items")
public class OrderItem {

    private String orderItemId;
    private String productId;
    private Integer quantity;
    private Long priceAtPurchase;
    private List<Order> orders;

    @Id
    @UuidGenerator
    @Column(name = "order_item_id")
    public String getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(String orderItemId) {
        this.orderItemId = orderItemId;
    }

    @Column(name = "product_id")
    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    @Column(name = "quantity")
    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Column(name = "price_at_purchase")
    public Long getPriceAtPurchase() {
        return priceAtPurchase;
    }

    public void setPriceAtPurchase(Long priceAtPurchase) {
        this.priceAtPurchase = priceAtPurchase;
    }

    @ManyToMany(mappedBy = "orderItems")
    @JsonIgnore
    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
}
