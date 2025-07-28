package com.example.demo.dto;

import java.time.LocalTime;

import com.example.demo.model.ServiceOffering;

public record ServiceOfferingDTO(
        Long id,
        String code,
        String name,
        String description,
        String allowedDays,
        String startTime,
        String endTime) {
    public static ServiceOfferingDTO from(ServiceOffering s) {
        return new ServiceOfferingDTO(
                s.getId(),
                s.getCode(),
                s.getName(),
                s.getDescription(),
                s.getAllowedDays(),
                s.getStartTime().toString(),
                s.getEndTime().toString());
    }
}
