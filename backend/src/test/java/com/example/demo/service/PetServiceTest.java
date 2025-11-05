package com.example.demo.service;

import com.example.demo.model.Pet;
import com.example.demo.repo.PetRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PetServiceTest {

  @Mock
  private PetRepo petRepo;

  @InjectMocks
  private PetService petService;

  private Pet testPet;

  @BeforeEach
  void setUp() {
    testPet = Pet.builder()
        .id(1L)
        .name("Fluffy")
        .species("Dog")
        .breed("Golden Retriever")
        .age(3)
        .ownerName("John Doe")
        .ownerPhone("123-456-7890")
        .build();
  }

  @Test
  void testSave_ShouldReturnSavedPet() {
    // Given
    Pet newPet = Pet.builder()
        .name("Fluffy")
        .species("Dog")
        .ownerName("John Doe")
        .ownerPhone("123-456-7890")
        .build();

    when(petRepo.save(any(Pet.class))).thenReturn(testPet);

    // When
    Pet result = petService.save(newPet);

    // Then
    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertEquals("Fluffy", result.getName());
    verify(petRepo, times(1)).save(newPet);
  }

  @Test
  void testFindAll_ShouldReturnListOfPets() {
    // Given
    Pet pet2 = Pet.builder()
        .id(2L)
        .name("Whiskers")
        .species("Cat")
        .ownerName("Jane Smith")
        .ownerPhone("987-654-3210")
        .build();

    List<Pet> pets = Arrays.asList(testPet, pet2);
    when(petRepo.findAll()).thenReturn(pets);

    // When
    List<Pet> result = petService.findAll();

    // Then
    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals("Fluffy", result.get(0).getName());
    assertEquals("Whiskers", result.get(1).getName());
    verify(petRepo, times(1)).findAll();
  }

  @Test
  void testFindById_WhenPetExists_ShouldReturnPet() {
    // Given
    when(petRepo.findById(1L)).thenReturn(Optional.of(testPet));

    // When
    Pet result = petService.findById(1L);

    // Then
    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertEquals("Fluffy", result.getName());
    verify(petRepo, times(1)).findById(1L);
  }

  @Test
  void testFindById_WhenPetDoesNotExist_ShouldThrowException() {
    // Given
    when(petRepo.findById(999L)).thenReturn(Optional.empty());

    // When & Then
    assertThrows(Exception.class, () -> petService.findById(999L));
    verify(petRepo, times(1)).findById(999L);
  }

  @Test
  void testDelete_ShouldCallDeleteById() {
    // When
    petService.delete(1L);

    // Then
    verify(petRepo, times(1)).deleteById(1L);
  }
}
