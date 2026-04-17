package com.godesii.godesii_services.entity.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;


@Entity
@Table(name = "order_items")
public class OrderItem {

    private String orderItemId;
    private String productId;
    private Integer quantity;
    private Long priceAtPurchase;
    private Order order;

    @Id
    @UuidGenerator
    @Column(name = "order_item_id", length = 36)
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore
    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
