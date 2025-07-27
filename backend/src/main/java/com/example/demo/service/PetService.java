package com.example.demo.service;

import com.example.demo.model.Pet;
import com.example.demo.repo.PetRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PetService {
  private final PetRepo repo;

  public Pet save(Pet pet) {
    return repo.save(pet);
  }

  public List<Pet> findAll() {
    return repo.findAll();
  }

  public Pet findById(Long id) {
    return repo.findById(id).orElseThrow();
  }

  public void delete(Long id) {
    repo.deleteById(id);
  }
}