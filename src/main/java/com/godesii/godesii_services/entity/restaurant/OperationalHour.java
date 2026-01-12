package com.godesii.godesii_services.entity.restaurant;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@Table(name = "operational_hour")
@Setter
@Getter
public class OperationalHour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "day_of_week")
    private DayOfWeek dayOfWeek; // 0=Sunday, 1=Monday...
    private LocalTime openTime;
    private LocalTime closeTime;

    // Service-specific logic popular in 2026
    private String serviceType; // "DINE_IN", "DELIVERY"
}
