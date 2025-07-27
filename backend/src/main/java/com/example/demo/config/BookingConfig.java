package com.example.demo.config;

import java.time.*;
import java.util.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.*;

@Component
@ConfigurationProperties(prefix = "booking")
public class BookingConfig {

  private List<ServiceRule> services = List.of();

  /** Returns the rule for the given ServiceOffering name (or id) */
  public Optional<ServiceRule> ruleFor(String serviceCode) {
    return services.stream()
        .filter(r -> r.getCode().equalsIgnoreCase(serviceCode))
        .findFirst();
  }

  public List<ServiceRule> getServices() {
    return services;
  }

  public void setServices(List<ServiceRule> services) {
    this.services = services;
  }

  // --- nested static class holds one rule set ------------------
  @Data
  public static class ServiceRule {
    private String code;
    private List<DayOfWeek> allowedDays;
    private LocalTime startTime;
    private LocalTime endTime;

    public boolean allows(LocalDateTime ts) {
      return allowedDays.contains(ts.getDayOfWeek())
          && !ts.toLocalTime().isBefore(startTime)
          && !ts.toLocalTime().isAfter(endTime);
    }
  }
}