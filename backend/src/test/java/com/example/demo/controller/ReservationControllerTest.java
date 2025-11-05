package com.example.demo.controller;

import com.example.demo.dto.ReservationDTO;
import com.example.demo.dto.ReservationRequest;
import com.example.demo.model.*;
import com.example.demo.service.ReservationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ReservationController.class, excludeAutoConfiguration = {org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class})
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservationService reservationService;

    @Autowired
    private ObjectMapper objectMapper;

    private Reservation testReservation;
    private Pet testPet;
    private ServiceOffering testService;

    @BeforeEach
    void setUp() {
        testPet = Pet.builder()
                .id(1L)
                .name("Fluffy")
                .species("Dog")
                .ownerName("John Doe")
                .ownerPhone("123-456-7890")
                .build();

        testService = ServiceOffering.builder()
                .id(1L)
                .code("BATH")
                .name("Bath Service")
                .description("Full bath service")
                .build();

        LocalDateTime reservationTime = LocalDateTime.of(2024, 1, 15, 10, 0);
        testReservation = Reservation.builder()
                .id(1L)
                .pet(testPet)
                .service(testService)
                .reservationTime(reservationTime)
                .ownerPhone("123-456-7890")
                .status(ReservationStatus.BOOKED)
                .notes("Test notes")
                .build();
    }

    @Test
    void testCreate_ShouldCreateReservation() throws Exception {
        // Given
        ReservationRequest request = new ReservationRequest(
                1L, 1L, LocalDateTime.of(2024, 1, 15, 10, 0), "Test notes");

        when(reservationService.create(any(ReservationRequest.class))).thenReturn(testReservation);

        // When & Then
        mockMvc.perform(post("/api/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("BOOKED"));

        verify(reservationService, times(1)).create(any(ReservationRequest.class));
    }

    @Test
    void testGetAll_ShouldReturnListOfReservations() throws Exception {
        // Given
        Reservation reservation2 = Reservation.builder()
                .id(2L)
                .pet(testPet)
                .service(testService)
                .reservationTime(LocalDateTime.of(2024, 1, 16, 14, 0))
                .ownerPhone("123-456-7890")
                .status(ReservationStatus.BOOKED)
                .build();

        List<Reservation> reservations = Arrays.asList(testReservation, reservation2);
        when(reservationService.findAll()).thenReturn(reservations);

        // When & Then
        mockMvc.perform(get("/api/reservations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));

        verify(reservationService, times(1)).findAll();
    }

    @Test
    void testGetByDate_ShouldReturnReservationsForDate() throws Exception {
        // Given
        LocalDate date = LocalDate.of(2024, 1, 15);
        List<Reservation> reservations = Arrays.asList(testReservation);
        when(reservationService.findAll()).thenReturn(reservations);

        // When & Then
        mockMvc.perform(get("/api/reservations/date")
                .param("d", "2024-01-15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(reservationService, times(1)).findAll();
    }

    @Test
    void testGetByPetId_ShouldReturnReservationsForPet() throws Exception {
        // Given
        List<Reservation> petReservations = Arrays.asList(testReservation);
        when(reservationService.findByPetId(1L)).thenReturn(petReservations);

        // When & Then
        mockMvc.perform(get("/api/reservations/pet/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(reservationService, times(1)).findByPetId(1L);
    }

    @Test
    void testCancel_ShouldCancelReservation() throws Exception {
        // Given
        String cancelBody = "{\"phone\":\"123-456-7890\"}";
        doNothing().when(reservationService).cancel(1L, "123-456-7890");

        // When & Then
        mockMvc.perform(patch("/api/reservations/1/cancel")
                .contentType(MediaType.APPLICATION_JSON)
                .content(cancelBody))
                .andExpect(status().isNoContent());

        verify(reservationService, times(1)).cancel(1L, "123-456-7890");
    }

    @Test
    void testComplete_ShouldCompleteReservation() throws Exception {
        // Given
        doNothing().when(reservationService).complete(1L);

        // When & Then
        mockMvc.perform(patch("/api/reservations/1/complete"))
                .andExpect(status().isOk());

        verify(reservationService, times(1)).complete(1L);
    }

    @Test
    void testDashboard_ShouldReturnReservationDTOs() throws Exception {
        // Given
        List<Reservation> reservations = Arrays.asList(testReservation);
        when(reservationService.findAll()).thenReturn(reservations);

        // When & Then
        mockMvc.perform(get("/api/reservations/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].petName").value("Fluffy"))
                .andExpect(jsonPath("$[0].ownerName").value("John Doe"));

        verify(reservationService, times(1)).findAll();
    }

    @Test
    void testDashboard_WithPhoneFilter_ShouldFilterByPhone() throws Exception {
        // Given
        List<Reservation> reservations = Arrays.asList(testReservation);
        when(reservationService.findAll()).thenReturn(reservations);

        // When & Then
        mockMvc.perform(get("/api/reservations/dashboard")
                .param("phone", "123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].phone").value("123-456-7890"));

        verify(reservationService, times(1)).findAll();
    }

    @Test
    void testDashboard_WithDateFilter_ShouldFilterByDate() throws Exception {
        // Given
        List<Reservation> reservations = Arrays.asList(testReservation);
        when(reservationService.findAll()).thenReturn(reservations);

        // When & Then
        mockMvc.perform(get("/api/reservations/dashboard")
                .param("date", "2024-01-15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].date").value("2024-01-15"));

        verify(reservationService, times(1)).findAll();
    }
}

