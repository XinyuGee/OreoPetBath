package com.example.demo.service;

import com.example.demo.dto.ServiceOfferingDTO;
import com.example.demo.model.ServiceOffering;
import com.example.demo.repo.ServiceOfferingRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceOfferingService {
  private final ServiceOfferingRepo repo;

  public List<ServiceOfferingDTO> findAll() {
    return repo.findAll()
        .stream()
        .map(ServiceOfferingDTO::from)
        .toList();
  }

  public ServiceOfferingDTO findById(Long id) {
    return repo.findById(id)
        .map(ServiceOfferingDTO::from)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "Service with id %d not found".formatted(id)));
  }
}