package com.godesii.godesii_services.dto;

import com.godesii.godesii_services.entity.restaurant.DayOfWeek;
import com.godesii.godesii_services.entity.restaurant.OperationalHour;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class OperationalHourRequest {

    private Long id;
    private String dayOfWeek;
    private LocalTime openTime;
    private LocalTime closeTime;
    private String serviceType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public LocalTime getOpenTime() {
        return openTime;
    }

    public void setOpenTime(LocalTime openTime) {
        this.openTime = openTime;
    }

    public LocalTime getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(LocalTime closeTime) {
        this.closeTime = closeTime;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public static List<OperationalHour> mapToEntities(List<OperationalHourRequest> requests) {
        List<OperationalHour> list = new ArrayList<>(requests.size());
        for (OperationalHourRequest request : requests) {
            list.add(mapToEntity(request));
        }
        return list;
    }

    public static OperationalHour mapToEntity(OperationalHourRequest request) {
        OperationalHour operationalHour = new OperationalHour();
        operationalHour.setId(request.getId());
        operationalHour.setOpenTime(request.getOpenTime());
        operationalHour.setCloseTime(request.getCloseTime());
        operationalHour.setServiceType(request.getServiceType());
        operationalHour.setDayOfWeek(DayOfWeek.fromInt(Integer.parseInt(request.getDayOfWeek())));
        return operationalHour;
    }

    public static List<OperationalHour> updateEntities(List<OperationalHour> existing,
            List<OperationalHourRequest> requests) {
        // Create a new list to store the updated entities
        List<OperationalHour> updatedList = new ArrayList<>();

        // Process each request
        for (OperationalHourRequest request : requests) {
            OperationalHour existingHour = null;

            // First, try to find by ID if the request has an ID
            if (request.getId() != null) {
                existingHour = existing.stream()
                        .filter(x -> x.getId() != null && x.getId().equals(request.getId()))
                        .findFirst()
                        .orElse(null);
            }

            // If not found by ID, try to match by dayOfWeek and serviceType
            if (existingHour == null) {
                DayOfWeek requestDayOfWeek = DayOfWeek.fromInt(Integer.parseInt(request.getDayOfWeek()));
                existingHour = existing.stream()
                        .filter(x -> x.getDayOfWeek() == requestDayOfWeek
                                && (x.getServiceType() != null && x.getServiceType().equals(request.getServiceType())))
                        .findFirst()
                        .orElse(null);
            }

            // Update existing or create new
            if (existingHour != null) {
                // Update the existing entity
                updateEntity(existingHour, request);
                updatedList.add(existingHour);
            } else {
                // Create new entity
                OperationalHour newHour = mapToEntity(request);
                updatedList.add(newHour);
            }
        }

        return updatedList;
    }

    public static void updateEntity(OperationalHour existing, OperationalHourRequest request) {
        existing.setDayOfWeek(DayOfWeek.fromInt(Integer.parseInt(request.getDayOfWeek())));
        existing.setOpenTime(request.getOpenTime());
        existing.setCloseTime(request.getCloseTime());
        existing.setServiceType(request.getServiceType());
    }

}
