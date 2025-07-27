package com.example.demo.dto;

import java.time.LocalTime;

import com.example.demo.model.ServiceOffering;

public record ServiceOfferingDTO(
        Long id,
        String code,
        String name,
        String description,
        String allowedDays,
        LocalTime startTime,
        LocalTime endTime) {
    public static ServiceOfferingDTO from(ServiceOffering s) {
        return new ServiceOfferingDTO(
                s.getId(),
                s.getCode(),
                s.getName(),
                s.getDescription(),
                String.join(",", s.getAllowedDays()),
                s.getStartTime(),
                s.getEndTime());
    }
}
