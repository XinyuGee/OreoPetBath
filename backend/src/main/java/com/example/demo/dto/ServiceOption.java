package com.example.demo.dto;

import com.example.demo.model.ServiceOffering;

public record ServiceOption(Long id, String name, String description) {
  public static ServiceOption from(ServiceOffering s) {
    return new ServiceOption(s.getId(), s.getName(), s.getDescription());
  }
}