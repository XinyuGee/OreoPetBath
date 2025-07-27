package com.example.demo.repo;

import com.example.demo.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PetRepo extends JpaRepository<Pet, Long> {

}
