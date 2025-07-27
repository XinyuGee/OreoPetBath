package com.example.demo.controller;

import com.example.demo.model.Pet;
import com.example.demo.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pets")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PetController {
  private final PetService svc;

  @GetMapping
  public List<Pet> all() {
    return svc.findAll();
  }

  @GetMapping("{id}")
  public Pet one(@PathVariable Long id) {
    return svc.findById(id);
  }

  @PostMapping
  public Pet create(@RequestBody Pet pet) {
    return svc.save(pet);
  }

  @PutMapping("{id}")
  public Pet update(@PathVariable Long id,
      @RequestBody Pet pet) {
    pet.setId(id);
    return svc.save(pet);
  }

  @DeleteMapping("{id}")
  public void delete(@PathVariable Long id) {
    svc.delete(id);
  }
}