package com.godesii.godesii_services.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDto {
    private long totalRestaurants;
    private long totalMenus;
    private long totalMenuItems;
    private long availableItems;
}
