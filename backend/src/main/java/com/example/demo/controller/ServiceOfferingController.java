package com.example.demo.controller;

import com.example.demo.dto.ServiceOption;
import com.example.demo.dto.ServiceOfferingDTO;
import com.example.demo.model.ServiceOffering;
import com.example.demo.repo.ServiceOfferingRepo;
import com.example.demo.service.ServiceOfferingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceOfferingController {

  private final ServiceOfferingService svc;

  @GetMapping
  public List<ServiceOfferingDTO> all() {
    return svc.findAll();
  }

  @GetMapping("/{id}")
  public ServiceOfferingDTO one(@PathVariable Long id) {
    return svc.findById(id);
  }
}