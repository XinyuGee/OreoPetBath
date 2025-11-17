package com.example.demo.service;

import com.example.demo.config.ReservationProps;
import com.example.demo.dto.ReservationRequest;
import com.example.demo.exception.BookingConflictException;
import com.example.demo.model.*;
import com.example.demo.repo.*;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

  @Mock
  private ReservationRepo reservationRepo;

  @Mock
  private PetRepo petRepo;

  @Mock
  private ServiceOfferingRepo serviceRepo;

  @Mock
  private ReservationProps props;

  @InjectMocks
  private ReservationService reservationService;

  private Pet testPet;
  private ServiceOffering testService;
  private ServiceOffering boardingService;
  private Reservation testReservation;
  private ReservationRequest testRequest;

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

    boardingService = ServiceOffering.builder()
        .id(2L)
        .code("BOARD")
        .name("Boarding")
        .description("Overnight boarding")
        .build();

    LocalDateTime reservationTime = LocalDateTime.of(2024, 1, 15, 10, 0);
    testRequest = new ReservationRequest(1L, 1L, reservationTime, "Test notes");

    testReservation = Reservation.builder()
        .id(1L)
        .pet(testPet)
        .service(testService)
        .reservationTime(reservationTime)
        .ownerPhone("123-456-7890")
        .status(ReservationStatus.BOOKED)
        .notes("Test notes")
        .build();

    // Use lenient stubbing to avoid unnecessary stubbing exceptions
    lenient().when(props.bufferMinutes()).thenReturn(30);
  }

  @Test
  void testCreate_WhenNoConflict_ShouldCreateReservation() {
    // Given
    when(petRepo.findById(1L)).thenReturn(Optional.of(testPet));
    when(serviceRepo.findById(1L)).thenReturn(Optional.of(testService));
    when(reservationRepo.existsByReservationTimeBetweenAndStatusAndService_CodeNot(
        any(LocalDateTime.class), any(LocalDateTime.class), eq(ReservationStatus.BOOKED), eq("BOARD")))
        .thenReturn(false);
    when(reservationRepo.save(any(Reservation.class))).thenReturn(testReservation);

    // When
    Reservation result = reservationService.create(testRequest);

    // Then
    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertEquals(ReservationStatus.BOOKED, result.getStatus());
    assertEquals(testPet, result.getPet());
    assertEquals(testService, result.getService());
    verify(petRepo, times(1)).findById(1L);
    verify(serviceRepo, times(1)).findById(1L);
    verify(reservationRepo, times(1)).save(any(Reservation.class));
  }

  @Test
  void testCreate_WhenConflictExists_ShouldThrowBookingConflictException() {
    // Given
    when(petRepo.findById(1L)).thenReturn(Optional.of(testPet));
    when(serviceRepo.findById(1L)).thenReturn(Optional.of(testService));
    when(reservationRepo.existsByReservationTimeBetweenAndStatusAndService_CodeNot(
        any(LocalDateTime.class), any(LocalDateTime.class), eq(ReservationStatus.BOOKED), eq("BOARD")))
        .thenReturn(true);

    // When & Then
    assertThrows(BookingConflictException.class, () -> reservationService.create(testRequest));
    verify(reservationRepo, never()).save(any(Reservation.class));
  }

  @Test
  void testCreate_WhenBoardingService_ShouldNotCheckConflict() {
    // Given
    ReservationRequest boardingRequest = new ReservationRequest(1L, 2L,
        LocalDateTime.of(2024, 1, 15, 10, 0), "Boarding notes");

    when(petRepo.findById(1L)).thenReturn(Optional.of(testPet));
    when(serviceRepo.findById(2L)).thenReturn(Optional.of(boardingService));
    when(reservationRepo.save(any(Reservation.class))).thenReturn(testReservation);

    // When
    Reservation result = reservationService.create(boardingRequest);

    // Then
    assertNotNull(result);
    verify(reservationRepo, never()).existsByReservationTimeBetweenAndStatusAndService_CodeNot(
        any(), any(), any(), any());
    verify(reservationRepo, times(1)).save(any(Reservation.class));
  }

  @Test
  void testCreate_WhenPetNotFound_ShouldThrowException() {
    // Given
    when(petRepo.findById(1L)).thenReturn(Optional.empty());

    // When & Then
    assertThrows(Exception.class, () -> reservationService.create(testRequest));
    verify(reservationRepo, never()).save(any(Reservation.class));
  }

  @Test
  void testCreate_WhenServiceNotFound_ShouldThrowException() {
    // Given
    when(petRepo.findById(1L)).thenReturn(Optional.of(testPet));
    when(serviceRepo.findById(1L)).thenReturn(Optional.empty());

    // When & Then
    assertThrows(Exception.class, () -> reservationService.create(testRequest));
    verify(reservationRepo, never()).save(any(Reservation.class));
  }

  @Test
  void testFindAll_ShouldReturnListOfReservations() {
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
    when(reservationRepo.findAll()).thenReturn(reservations);

    // When
    List<Reservation> result = reservationService.findAll();

    // Then
    assertNotNull(result);
    assertEquals(2, result.size());
    verify(reservationRepo, times(1)).findAll();
  }

  @Test
  void testFindByPetId_ShouldReturnReservationsForPet() {
    // Given
    List<Reservation> petReservations = Arrays.asList(testReservation);
    when(reservationRepo.findByPetId(1L)).thenReturn(petReservations);

    // When
    List<Reservation> result = reservationService.findByPetId(1L);

    // Then
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(1L, result.get(0).getId());
    verify(reservationRepo, times(1)).findByPetId(1L);
  }

  @Test
  void testCancel_WhenValid_ShouldCancelReservation() {
    // Given
    when(reservationRepo.findByIdWithLock(1L)).thenReturn(Optional.of(testReservation));

    // When
    reservationService.cancel(1L, "123-456-7890");

    // Then
    assertEquals(ReservationStatus.CANCELED, testReservation.getStatus());
    verify(reservationRepo, times(1)).findByIdWithLock(1L);
  }

  @Test
  void testCancel_WhenReservationNotFound_ShouldThrowEntityNotFoundException() {
    // Given
    when(reservationRepo.findByIdWithLock(999L)).thenReturn(Optional.empty());

    // When & Then
    assertThrows(EntityNotFoundException.class,
        () -> reservationService.cancel(999L, "123-456-7890"));
  }

  @Test
  void testCancel_WhenPhoneDoesNotMatch_ShouldThrowIllegalArgumentException() {
    // Given
    when(reservationRepo.findByIdWithLock(1L)).thenReturn(Optional.of(testReservation));

    // When & Then
    assertThrows(IllegalArgumentException.class,
        () -> reservationService.cancel(1L, "wrong-phone"));
  }

  @Test
  void testCancel_WhenStatusNotBooked_ShouldThrowIllegalStateException() {
    // Given
    testReservation.setStatus(ReservationStatus.COMPLETED);
    when(reservationRepo.findByIdWithLock(1L)).thenReturn(Optional.of(testReservation));

    // When & Then
    assertThrows(IllegalStateException.class,
        () -> reservationService.cancel(1L, "123-456-7890"));
  }

  @Test
  void testComplete_WhenValid_ShouldCompleteReservation() {
    // Given
    when(reservationRepo.findByIdWithLock(1L)).thenReturn(Optional.of(testReservation));

    // When
    reservationService.complete(1L);

    // Then
    assertEquals(ReservationStatus.COMPLETED, testReservation.getStatus());
    verify(reservationRepo, times(1)).findByIdWithLock(1L);
  }

  @Test
  void testComplete_WhenReservationNotFound_ShouldThrowEntityNotFoundException() {
    // Given
    when(reservationRepo.findByIdWithLock(999L)).thenReturn(Optional.empty());

    // When & Then
    assertThrows(EntityNotFoundException.class, () -> reservationService.complete(999L));
  }
}
