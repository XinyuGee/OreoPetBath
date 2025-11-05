package com.example.demo.controller;

import com.example.demo.dto.ServiceOfferingDTO;
import com.example.demo.service.ServiceOfferingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ServiceOfferingController.class, excludeAutoConfiguration = {org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class})
class ServiceOfferingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ServiceOfferingService serviceOfferingService;

    private ServiceOfferingDTO testServiceDTO;

    @BeforeEach
    void setUp() {
        testServiceDTO = new ServiceOfferingDTO(
                1L,
                "BATH",
                "Bath Service",
                "Full bath service for pets",
                "MONDAY,TUESDAY,WEDNESDAY",
                "09:00:00",
                "17:00:00"
        );
    }

    @Test
    void testGetAll_ShouldReturnListOfServiceOfferingDTOs() throws Exception {
        // Given
        ServiceOfferingDTO service2 = new ServiceOfferingDTO(
                2L,
                "GROOM",
                "Grooming Service",
                "Full grooming service",
                "MONDAY,TUESDAY",
                "10:00:00",
                "16:00:00"
        );

        List<ServiceOfferingDTO> services = Arrays.asList(testServiceDTO, service2);
        when(serviceOfferingService.findAll()).thenReturn(services);

        // When & Then
        mockMvc.perform(get("/api/services"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].code").value("BATH"))
                .andExpect(jsonPath("$[0].name").value("Bath Service"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].code").value("GROOM"));

        verify(serviceOfferingService, times(1)).findAll();
    }

    @Test
    void testGetById_WhenServiceExists_ShouldReturnServiceOfferingDTO() throws Exception {
        // Given
        when(serviceOfferingService.findById(1L)).thenReturn(testServiceDTO);

        // When & Then
        mockMvc.perform(get("/api/services/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.code").value("BATH"))
                .andExpect(jsonPath("$.name").value("Bath Service"))
                .andExpect(jsonPath("$.description").value("Full bath service for pets"));

        verify(serviceOfferingService, times(1)).findById(1L);
    }

    @Test
    void testGetAll_WhenEmpty_ShouldReturnEmptyList() throws Exception {
        // Given
        when(serviceOfferingService.findAll()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/services"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(serviceOfferingService, times(1)).findAll();
    }
}

