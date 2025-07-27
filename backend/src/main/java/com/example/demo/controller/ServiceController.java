package com.example.demo.controller;

import com.example.demo.model.Service;
import com.example.demo.repo.ServiceRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceController {
  private final ServiceRepo repo;

  @GetMapping
  public List<Service> all() {
    return repo.findAll();
  }

  @GetMapping("{id}")
  public Service one(@PathVariable Long id) {
    return repo.findById(id).orElseThrow();
  }

  @PostMapping
  public Service create(@RequestBody Service s) {
    return repo.save(s);
  }

  @PutMapping("{id}")
  public Service update(@PathVariable Long id,
      @RequestBody Service s) {
    s.setId(id);
    return repo.save(s);
  }

  @DeleteMapping("{id}")
  public void delete(@PathVariable Long id) {
    repo.deleteById(id);
  }
}