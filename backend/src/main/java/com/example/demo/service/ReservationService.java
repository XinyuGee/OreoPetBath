package com.example.demo.service;

import com.example.demo.config.ReservationProps;
import com.example.demo.dto.ReservationRequest;
import com.example.demo.exception.BookingConflictException;
import com.example.demo.model.*;
import com.example.demo.repo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ReservationService {

  private final ReservationRepo reservationRepo;
  private final PetRepo petRepo;
  private final ServiceOfferingRepo serviceRepo;
  private final ReservationProps props;

  @Transactional(isolation = Isolation.SERIALIZABLE)
  public Reservation create(ReservationRequest req) {
    var pet = petRepo.findById(req.petId()).orElseThrow();
    var service = serviceRepo.findById(req.serviceId()).orElseThrow();
    String noConflictService = "BOARD";

    LocalDateTime requested = req.reservationTime();
    boolean isBoarding = noConflictService.equalsIgnoreCase(service.getCode());

    if (!isBoarding) {
      LocalDateTime start = requested.minusMinutes(props.bufferMinutes());
      LocalDateTime end = requested.plusMinutes(props.bufferMinutes());

      // Use pessimistic locking to prevent concurrent bookings
      boolean clash = reservationRepo
          .existsByReservationTimeBetweenAndStatusAndService_CodeNot(
              start, end, ReservationStatus.BOOKED, noConflictService);

      if (clash) {
        throw new BookingConflictException(
            "Another reservation is already within the requested time.");
      }
    }

    return reservationRepo.save(
        Reservation.builder()
            .pet(pet)
            .service(service)
            .reservationTime(requested)
            .ownerPhone(pet.getOwnerPhone())
            .status(ReservationStatus.BOOKED)
            .notes(req.notes())
            .build());
  }

  public List<Reservation> findAll() {
    return reservationRepo.findAll();
  }

  public List<Reservation> findByPetId(Long petId) {
    return reservationRepo.findByPetId(petId);
  }

  @Transactional
  public void cancel(long id, String phone) {
    // Use pessimistic locking to prevent concurrent modifications
    Reservation r = reservationRepo.findByIdWithLock(id)
        .orElseThrow(() -> new EntityNotFoundException("Reservation " + id));

    if (!Objects.equals(r.getOwnerPhone(), phone))
      throw new IllegalArgumentException("Phone number does not match");

    if (r.getStatus() != ReservationStatus.BOOKED)
      throw new IllegalStateException("Only BOOKED reservations can be canceled");

    r.setStatus(ReservationStatus.CANCELED);
    // JPA will automatically save due to @Transactional and dirty checking
  }

  @Transactional
  public void complete(Long id) {
    // Use pessimistic locking to prevent concurrent modifications
    Reservation r = reservationRepo.findByIdWithLock(id)
        .orElseThrow(() -> new EntityNotFoundException("Reservation " + id));
    r.setStatus(ReservationStatus.COMPLETED);
    // JPA will automatically save due to @Transactional and dirty checking
  }
}