package com.example.demo.controller;

import com.example.demo.model.Pet;
import com.example.demo.service.PetService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PetController.class, excludeAutoConfiguration = {org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class})
class PetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PetService petService;

    @Autowired
    private ObjectMapper objectMapper;

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
    void testGetAll_ShouldReturnListOfPets() throws Exception {
        // Given
        Pet pet2 = Pet.builder()
                .id(2L)
                .name("Whiskers")
                .species("Cat")
                .ownerName("Jane Smith")
                .ownerPhone("987-654-3210")
                .build();

        List<Pet> pets = Arrays.asList(testPet, pet2);
        when(petService.findAll()).thenReturn(pets);

        // When & Then
        mockMvc.perform(get("/api/pets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Fluffy"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Whiskers"));

        verify(petService, times(1)).findAll();
    }

    @Test
    void testGetById_WhenPetExists_ShouldReturnPet() throws Exception {
        // Given
        when(petService.findById(1L)).thenReturn(testPet);

        // When & Then
        mockMvc.perform(get("/api/pets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Fluffy"))
                .andExpect(jsonPath("$.species").value("Dog"));

        verify(petService, times(1)).findById(1L);
    }

    @Test
    void testCreate_ShouldCreateAndReturnPet() throws Exception {
        // Given
        Pet newPet = Pet.builder()
                .name("Fluffy")
                .species("Dog")
                .ownerName("John Doe")
                .ownerPhone("123-456-7890")
                .build();

        when(petService.save(any(Pet.class))).thenReturn(testPet);

        // When & Then
        mockMvc.perform(post("/api/pets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newPet)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Fluffy"));

        verify(petService, times(1)).save(any(Pet.class));
    }

    @Test
    void testUpdate_ShouldUpdateAndReturnPet() throws Exception {
        // Given
        Pet updatedPet = Pet.builder()
                .id(1L)
                .name("Fluffy Updated")
                .species("Dog")
                .ownerName("John Doe")
                .ownerPhone("123-456-7890")
                .build();

        when(petService.save(any(Pet.class))).thenReturn(updatedPet);

        // When & Then
        mockMvc.perform(put("/api/pets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedPet)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Fluffy Updated"));

        verify(petService, times(1)).save(any(Pet.class));
    }

    @Test
    void testDelete_ShouldDeletePet() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/pets/1"))
                .andExpect(status().isOk());

        verify(petService, times(1)).delete(1L);
    }
}

