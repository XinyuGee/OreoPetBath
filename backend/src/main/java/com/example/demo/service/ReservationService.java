package com.example.demo.service;

import com.example.demo.config.ReservationProps;
import com.example.demo.dto.ReservationRequest;
import com.example.demo.dto.ReservationDTO;
import com.example.demo.exception.BookingConflictException;
import com.example.demo.model.*;
import com.example.demo.repo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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

  public Reservation create(ReservationRequest req) {

    LocalDateTime requested = req.reservationTime();
    LocalDateTime start = requested.minusMinutes(props.bufferMinutes());
    LocalDateTime end = requested.plusMinutes(props.bufferMinutes());

    boolean clash = reservationRepo
        .findByReservationTimeBetweenAndStatus(
            start, end, ReservationStatus.BOOKED)
        .stream()
        .findAny()
        .isPresent();

    if (clash) {
      throw new BookingConflictException(
          "Another reservation is already within "
              + props.bufferMinutes() + " minutes of the requested time.");
    }

    var pet = petRepo.findById(req.petId()).orElseThrow();
    var service = serviceRepo.findById(req.serviceId()).orElseThrow();

    return reservationRepo.save(
        Reservation.builder()
            .pet(pet)
            .service(service)
            .reservationTime(requested)
            .ownerPhone(pet.getOwnerPhone())
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

    Reservation r = reservationRepo.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Reservation " + id));

    if (!Objects.equals(r.getOwnerPhone(), phone))
      throw new IllegalArgumentException("Phone number does not match");

    if (r.getStatus() != ReservationStatus.BOOKED)
      throw new IllegalStateException("Only BOOKED reservations can be canceled");

    r.setStatus(ReservationStatus.CANCELED);
  }
}