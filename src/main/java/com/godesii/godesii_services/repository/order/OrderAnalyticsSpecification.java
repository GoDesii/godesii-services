package com.godesii.godesii_services.repository.order;

import com.godesii.godesii_services.entity.order.Order;
import com.godesii.godesii_services.entity.order.OrderStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

/**
 * Composable JPA Specifications for dynamic Order queries.
 * Used by RestaurantAnalyticsService to build filterable analytics queries
 * without writing separate JPQL for every filter combination.
 */
public class OrderAnalyticsSpecification {

    private OrderAnalyticsSpecification() {
        // Utility class — no instantiation
    }

    /**
     * Filter orders by restaurantId.
     */
    public static Specification<Order> byRestaurantId(String restaurantId) {
        return (root, query, cb) ->
                restaurantId == null ? cb.conjunction()
                        : cb.equal(root.get("restaurantId"), restaurantId);
    }

    /**
     * Filter orders where orderDate >= startDate (inclusive).
     */
    public static Specification<Order> fromDate(LocalDate startDate) {
        return (root, query, cb) -> {
            if (startDate == null) return cb.conjunction();
            Instant start = startDate.atStartOfDay(ZoneOffset.UTC).toInstant();
            return cb.greaterThanOrEqualTo(root.get("orderDate"), start);
        };
    }

    /**
     * Filter orders where orderDate <= endDate (inclusive, end of day).
     */
    public static Specification<Order> toDate(LocalDate endDate) {
        return (root, query, cb) -> {
            if (endDate == null) return cb.conjunction();
            Instant end = endDate.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();
            return cb.lessThan(root.get("orderDate"), end);
        };
    }

    /**
     * Filter orders where totalAmount >= minAmount.
     */
    public static Specification<Order> minAmount(Long minAmount) {
        return (root, query, cb) ->
                minAmount == null ? cb.conjunction()
                        : cb.greaterThanOrEqualTo(root.get("totalAmount"), minAmount);
    }

    /**
     * Filter orders where totalAmount <= maxAmount.
     */
    public static Specification<Order> maxAmount(Long maxAmount) {
        return (root, query, cb) ->
                maxAmount == null ? cb.conjunction()
                        : cb.lessThanOrEqualTo(root.get("totalAmount"), maxAmount);
    }

    /**
     * Filter by order status. Defaults to DELIVERED if null.
     */
    public static Specification<Order> byStatus(OrderStatus status) {
        OrderStatus effectiveStatus = (status != null) ? status : OrderStatus.DELIVERED;
        return (root, query, cb) ->
                cb.equal(root.get("orderStatus"), effectiveStatus);
    }

    /**
     * Compose all filters together into a single Specification.
     * Every filter is AND-ed together; nulls are treated as "match all".
     */
    public static Specification<Order> buildFilter(
            String restaurantId,
            LocalDate startDate,
            LocalDate endDate,
            Long minAmount,
            Long maxAmount,
            OrderStatus status) {

        return byRestaurantId(restaurantId)
                .and(fromDate(startDate))
                .and(toDate(endDate))
                .and(minAmount(minAmount))
                .and(maxAmount(maxAmount))
                .and(byStatus(status));
    }
}
